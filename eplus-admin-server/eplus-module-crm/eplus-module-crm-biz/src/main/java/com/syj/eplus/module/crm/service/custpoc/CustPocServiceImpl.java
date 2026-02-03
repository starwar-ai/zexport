package com.syj.eplus.module.crm.service.custpoc;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocPageReqVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocRespVO;
import com.syj.eplus.module.crm.controller.admin.custpoc.vo.CustPocSaveReqVO;
import com.syj.eplus.module.crm.dal.dataobject.custpoc.CustPocDO;
import com.syj.eplus.module.crm.dal.mysql.custpoc.CustPocMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.crm.enums.ErrorCodeConstants.CUST_POC_NOT_EXISTS;

/**
 * 客户联系人 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CustPocServiceImpl implements CustPocService {

    @Resource
    private CustPocMapper custPocMapper;
    @Override
    public Long createCustPoc(CustPocSaveReqVO createReqVO) {
        // 插入
        CustPocDO custPoc = BeanUtils.toBean(createReqVO, CustPocDO.class);
        custPocMapper.insert(custPoc);
        // 返回
        return custPoc.getId();
    }

    @Override
    public void updateCustPoc(CustPocSaveReqVO updateReqVO) {
        // 校验存在
//        validateCustPocExists(updateReqVO.getId());
        // 更新
        CustPocDO updateObj = BeanUtils.toBean(updateReqVO, CustPocDO.class);
        custPocMapper.updateById(updateObj);
    }

    @Override
    public void deleteCustPoc(Long id) {
        // 校验存在
        validateCustPocExists(id);
        // 删除
        custPocMapper.deleteById(id);
    }

    @Override
    public void deleteCustPocByCustId(Long custId) {
        // 删除
        custPocMapper.delete(CustPocDO::getCustId, custId);
    }

    private void validateCustPocExists(Long id) {
        if (custPocMapper.selectById(id) == null) {
            throw exception(CUST_POC_NOT_EXISTS);
        }
    }

    @Override
    public CustPocDO getCustPoc(Long id) {
        return custPocMapper.selectById(id);
    }

    @Override
    public PageResult<CustPocDO> getCustPocPage(CustPocPageReqVO pageReqVO) {
        return custPocMapper.selectPage(pageReqVO);
    }

    @Override
    public void batchSavePoc(List<CustPocSaveReqVO> custPocSaveReqVOList) {
        if (CollUtil.isEmpty(custPocSaveReqVOList)) {
            return;
        }
        custPocSaveReqVOList.forEach(custPocSaveReqVO -> {
            CustPocDO custPocDO = BeanUtils.toBean(custPocSaveReqVO, CustPocDO.class);
            custPocMapper.insert(custPocDO);
            Long pocId = custPocDO.getId();
        });
    }

    @Override
    public List<CustPocRespVO> getPocListByCustId(Long custId) {
        List<CustPocDO> custPocDOList = custPocMapper.selectList(new LambdaQueryWrapperX<CustPocDO>().eq(CustPocDO::getCustId, custId));
        if (CollUtil.isEmpty(custPocDOList)) {
            return Collections.emptyList();
        }
        List<CustPocRespVO> custPocRespVOList = BeanUtils.toBean(custPocDOList, CustPocRespVO.class);
        return custPocRespVOList;
    }

    @Override
    public List<CustPocDO> getPocListByCustIdList(List<Long> custIdList) {
        return custPocMapper.selectList(new LambdaQueryWrapperX<CustPocDO>().in(CustPocDO::getCustId, custIdList));
    }

    @Override
    public void batchDeletePocList(List<Long> idList) {
        custPocMapper.deleteBatchIds(idList);
    }

}