package com.syj.eplus.module.infra.service.company;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.infra.api.company.dto.CompanyBankRespDTO;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyBankRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyPageReqVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanySaveReqVO;
import com.syj.eplus.module.infra.convert.company.CompanyConvert;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyBank;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyDO;
import com.syj.eplus.module.infra.dal.mysql.company.CompanyBankMapper;
import com.syj.eplus.module.infra.dal.mysql.company.CompanyMapper;
import com.syj.eplus.module.infra.enums.company.CompanyNatureEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;

/**
 * 内部法人单位 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper,CompanyDO> implements CompanyService {

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private CompanyBankMapper companyBankMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCompany(CompanySaveReqVO createReqVO) {
        // 插入
        CompanyDO companyDO = CompanyConvert.INSTANCE.convertCompanyDO(createReqVO);
        companyMapper.insert(companyDO);
        List<CompanyBank> companyBankList = createReqVO.getCompanyBankList();
        if (CollUtil.isNotEmpty(companyBankList)) {
            companyBankList.forEach(s -> {
                s.setCompanyId(companyDO.getId());
            });
            companyBankMapper.insertBatch(companyBankList);
        }
        // 返回
        return companyDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCompany(CompanySaveReqVO updateReqVO) {
        // 校验存在
        validateCompanyExists(updateReqVO.getId());
        // 更新
        CompanyDO updateObj = BeanUtils.toBean(updateReqVO, CompanyDO.class);
        companyMapper.updateById(updateObj);
        List<CompanyBank> companyBankList = updateReqVO.getCompanyBankList();
        if (CollUtil.isEmpty(companyBankList)) {  //现在没有数据，全部删除原先
            companyBankMapper.delete(CompanyBank::getCompanyId, updateObj.getId());
            return;
        }
        companyBankList.forEach(s -> {
            s.setCompanyId(updateReqVO.getId());
        });
        //原数据
        List<CompanyBank> bankList = companyBankMapper.selectList(CompanyBank::getCompanyId, updateObj.getId());
        if(CollUtil.isEmpty(bankList)){   //原先没数据全部为新增
            companyBankList.forEach(s->{
                s.setId(null);
            });
            companyBankMapper.insertBatch(companyBankList);
            return;
        }
        List<Long> sourceIdList = bankList.stream().map(CompanyBank::getId).distinct().toList();   //原数据id列表
            List<Long> idList = companyBankList.stream().map(CompanyBank::getId).distinct().toList();   //新数据id列表
        //判断新增
        List<CompanyBank> addList = companyBankList.stream().filter(s -> !sourceIdList.contains(s.getId()) || s.getId() == null).toList();
        if(CollUtil.isNotEmpty(addList)){
            addList.forEach(s->{
                s.setId(null);
            });
            companyBankMapper.insertBatch(addList);
        }

        //判断删除
        List<CompanyBank> deleteList = bankList.stream().filter(s -> !idList.contains(s.getId())).toList();
        if(CollUtil.isNotEmpty(deleteList)){
            companyBankMapper.deleteBatchIds(deleteList);
        }

        //判断编辑
        List<CompanyBank> updateList = companyBankList.stream().filter(s -> sourceIdList.contains(s.getId())).toList();
        if(CollUtil.isNotEmpty(updateList)){
            companyBankMapper.updateBatch(updateList);
        }

    }

    @Override
    public void deleteCompany(Long id) {
        // 校验存在
        validateCompanyExists(id);
        // 删除
        companyMapper.deleteById(id);
    }

    private CompanyDO validateCompanyExists(Long id) {
        CompanyDO companyDO = companyMapper.selectById(id);
        if (companyDO == null) {
            throw exception(COMPANY_NOT_EXISTS);
        }
        return companyDO;
    }

    @Override
    public CompanyRespVO getCompany(Long id) {
        CompanyDO companyDO = companyMapper.selectById(id);
        if (Objects.isNull(companyDO)) {
            return null;
        }
        Long companyId = companyDO.getId();
        List<CompanyBank> companyBanks = companyBankMapper.selectList(CompanyBank::getCompanyId, companyId);
        CompanyRespVO companyRespVO = CompanyConvert.INSTANCE.convertCompanyRespVO(companyDO);
        companyRespVO.setCompanyBankList(companyBanks);
        return companyRespVO;
    }

    @Override
    public PageResult<CompanyDO> getCompanyPage(CompanyPageReqVO pageReqVO) {
        return companyMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SimpleCompanyDTO> getSimpleCompanyDTO() {
        List<CompanyDO> companyDOList = companyMapper.selectList();
        if (CollUtil.isEmpty(companyDOList)) {
            return Collections.emptyList();
        }
        return CompanyConvert.INSTANCE.convertSimpleCompanyDTOList(companyDOList);
    }

    @Override
    public List<SimpleCompanyDTO> getProcessCompanyDTO() {
        List<CompanyDO> companyDOList = companyMapper.selectList(CompanyDO::getCompanyNature, CompanyNatureEnum.FACTORY.getValue());
        if (CollUtil.isEmpty(companyDOList)) {
            return Collections.emptyList();
        }
        return CompanyConvert.INSTANCE.convertSimpleCompanyDTOList(companyDOList);
    }

    @Override
    public List<SimpleCompanyDTO> getUnProcessCompanyDTO() {
        LambdaQueryWrapper<CompanyDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(CompanyDO::getCompanyNature, CompanyNatureEnum.FACTORY.getValue());
        List<CompanyDO> companyDOList = companyMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(companyDOList)) {
            return Collections.emptyList();
        }
        return CompanyConvert.INSTANCE.convertSimpleCompanyDTOList(companyDOList);
    }

    @Override
    public List<SimpleCompanyDTO> getStringSimpleCompanyDTO(List<String> idList) {
        List<Long> idLongList = idList.stream().map(Long::valueOf).collect(Collectors.toList());
        List<CompanyDO> companyDOPageResult = companyMapper.selectListByIdList(idLongList);
        if (CollUtil.isEmpty(companyDOPageResult)) {
            return Collections.emptyList();
        }
        return CompanyConvert.INSTANCE.convertSimpleCompanyDTOList(companyDOPageResult);
    }

    @Override
    public List<CompanyRespVO> getCompanyList(List<Integer> notContainsNatureList,List<Integer> containsNatureList) {
        List<CompanyDO> companyDOList = companyMapper.selectList();
        if(CollUtil.isNotEmpty(notContainsNatureList)){
            companyDOList = companyDOList.stream().filter(s->!notContainsNatureList.contains(s.getCompanyNature())).toList();
        }
        if(CollUtil.isNotEmpty(containsNatureList)){
            companyDOList = companyDOList.stream().filter(s->!containsNatureList.contains(s.getCompanyNature())).toList();
        }
        if (CollUtil.isEmpty(companyDOList)) {
            return null;
        }
        List<CompanyRespVO> companyRespVOList = CompanyConvert.INSTANCE.convertCompanyRespVOList(companyDOList);
        List<Long> companyIdList = companyDOList.stream().map(CompanyDO::getId).distinct().toList();
        if (CollUtil.isEmpty(companyDOList)) {
            return companyRespVOList;
        }
        List<CompanyBank> companyBankList = companyBankMapper.selectList(new LambdaQueryWrapperX<CompanyBank>().in(CompanyBank::getCompanyId, companyIdList));
        if (CollUtil.isEmpty(companyBankList)) {
            return companyRespVOList;
        }
        Map<Long, List<CompanyBank>> companyMap = companyBankList.stream().collect(Collectors.groupingBy(CompanyBank::getCompanyId));
        companyRespVOList.forEach(s -> {
            s.setCompanyBankList(companyMap.get(s.getId()));
        });
        return companyRespVOList;
    }

    @Override
    public void enableCompany(Long companyId) {
        CompanyDO companyDO = validateCompanyExists(companyId);
        Integer enableFlag = companyDO.getEnableFlag();
        if (Objects.isNull(enableFlag) || BooleanEnum.YES.getValue() == enableFlag.intValue()) {
            throw exception(COMPANY_ENABLE);
        }
        companyMapper.updateById(new CompanyDO().setId(companyId).setEnableFlag(BooleanEnum.YES.getValue()));
    }

    @Override
    public void disableCompany(Long companyId) {
        CompanyDO companyDO = validateCompanyExists(companyId);
        Integer enableFlag = companyDO.getEnableFlag();
        if (Objects.isNull(enableFlag) || BooleanEnum.NO.getValue() == enableFlag.intValue()) {
            throw exception(COMPANY_DIS_ENABLE);
        }
        companyMapper.updateById(new CompanyDO().setId(companyId).setEnableFlag(BooleanEnum.NO.getValue()));
    }

    @Override
    public SimpleCompanyDTO getCompanyDTO(Long id) {
        if(Objects.isNull(id)){
           throw exception(COMPANY_NOT_EXISTS);
        }
        CompanyDO companyDO = companyMapper.selectById(id);
        if(Objects.isNull(companyDO)){
            throw exception(COMPANY_NOT_EXISTS);
        }
        CompanyBank companyBank = companyBankMapper.selectOne(new LambdaQueryWrapperX<CompanyBank>().eq(CompanyBank::getCompanyId, id).eq(CompanyBank::getDefaultFlag, BooleanEnum.YES.getValue()));
        CompanyBankRespDTO companyBankRespDTO = BeanUtils.toBean(companyBank, CompanyBankRespDTO.class);
        SimpleCompanyDTO simpleCompanyDTO = CompanyConvert.INSTANCE.convertSimpleCompanyDTO(companyDO);
        if (Objects.nonNull(simpleCompanyDTO)){
            simpleCompanyDTO.setCompanyBankRespDTO(companyBankRespDTO);
        }
        return simpleCompanyDTO;
    }

    @Override
    public List<CompanyBankRespVO> getCompanyBankList() {
        List<CompanyBank> bankList = companyBankMapper.selectList();
        if(CollUtil.isEmpty(bankList)){
            return null;
        }
        Map<Long,CompanyDO> companyMap = companyMapper.selectList().stream().collect(Collectors.toMap(CompanyDO::getId,s->s,(s1,s2)->s2));
        if(CollUtil.isEmpty(companyMap)){
            throw exception(COMPANY_NOT_EXISTS);
        }
        List<CompanyBankRespVO> voList = BeanUtils.toBean(bankList, CompanyBankRespVO.class);
        voList.forEach(s->{
            CompanyDO company = companyMap.get(s.getCompanyId());
            if(Objects.nonNull(company)){
                s.setCompanyNature(company.getCompanyNature());
                s.setShortname(company.getShortname());
                s.setEnableFlag(company.getEnableFlag());
            }
        });
        return voList;
    }

    @Override
    public List<SimpleCompanyDTO> getAllList() {
        return BeanUtils.toBean(companyMapper.selectList(),SimpleCompanyDTO.class);
    }

}
