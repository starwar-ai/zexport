package com.syj.eplus.module.oa.service.dictsubject;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.system.api.dict.DictTypeApi;
import cn.iocoder.yudao.module.system.api.dict.dto.DictTypeRespDTO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictFeeRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.dictsubject.vo.DictSubjectSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectRespVO;
import com.syj.eplus.module.oa.convert.dictsubject.DictSubjectConvert;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.mysql.dictsubject.DictSubjectMapper;
import com.syj.eplus.module.oa.service.subject.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.DICT_SUBJECT_NOT_EXISTS;

/**
 * 类别配置 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class DictSubjectServiceImpl implements DictSubjectService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DictSubjectMapper dictSubjectMapper;

    @Resource
    private DictTypeApi dictTypeApi;


    @Resource
    private SubjectService subjectService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDictSubject(DictSubjectSaveReqVO createReqVO) {
//        HashMap map = new HashMap<String, Object>();
//        DictSubjectDO dictSubjectDO = dictSubjectMapper.selectOneByMap(map);
//        if(dictSubjectDO!=null){
//            throw exception(DICT_SUBJECT_MUL);
//        }
        DictSubjectDO dictSubject = DictSubjectConvert.INSTANCE.convertDictSubjectDO(createReqVO);
        if (Objects.nonNull(dictSubject.getSubjectId())){
            SubjectRespVO subject = subjectService.getSubject(dictSubject.getSubjectId());
            if(Objects.nonNull(subject)){
                dictSubject.setSubjectName(subject.getName());
            }
        }
        // 插入
        dictSubjectMapper.insert(dictSubject);
        // 返回
        return dictSubject.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictSubject(DictSubjectSaveReqVO updateReqVO) {
        // 校验存在
        validateDictSubjectExists(updateReqVO.getId());
        // 更新
        DictSubjectDO updateObj = DictSubjectConvert.INSTANCE.convertDictSubjectDO(updateReqVO);
        SubjectRespVO subject = subjectService.getSubject(updateObj.getSubjectId());
        if(Objects.nonNull(subject)){
            updateObj.setSubjectDescription(subject.getDescription()).setSubjectName(subject.getName());
        }

        dictSubjectMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictSubject(Long id) {
        // 校验存在
        validateDictSubjectExists(id);
        // 删除
        dictSubjectMapper.deleteById(id);
    }

    private void validateDictSubjectExists(Long id) {
        if (dictSubjectMapper.selectById(id) == null) {
            throw exception(DICT_SUBJECT_NOT_EXISTS);
        }
    }

    @Override
    public DictSubjectRespVO getDictSubject(Long id) {
        DictSubjectDO dictSubjectDO = dictSubjectMapper.selectById(id);
        if (dictSubjectDO == null) {
           return null;
        }
        DictSubjectRespVO dictSubjectRespVO = DictSubjectConvert.INSTANCE.convertDictSubjectRespVO(dictSubjectDO);
        if(CollUtil.isNotEmpty(dictSubjectRespVO.getSystemDictTypeList())){
            List<DictTypeRespDTO> listByTypeList = dictTypeApi.getListByTypeList(dictSubjectRespVO.getSystemDictTypeList());
            dictSubjectRespVO.setSystemDictTypeDescList(listByTypeList);
        }


        return dictSubjectRespVO;
    }

    @Override
    public List<DictSubjectDO> getSubject(String systemDictType) {
        List<DictSubjectDO> dictSubjectDOList = dictSubjectMapper.selectList();
        if (CollUtil.isEmpty(dictSubjectDOList)){
            return null;
        }
        List<DictSubjectDO> list = dictSubjectDOList.stream().filter(s ->
                s.getSystemDictTypeList().stream().anyMatch(type -> Objects.equals(type, systemDictType))).toList();
        if (CollUtil.isEmpty(list)){
            return null;
        }
        return list;
    }

    @Override
    public PageResult<DictSubjectRespVO> getDictSubjectPage(DictSubjectPageReqVO pageReqVO) {
        PageResult<DictSubjectDO> doList = dictSubjectMapper.selectPage(pageReqVO);
        List<DictSubjectDO> list = doList.getList();
        if(CollUtil.isEmpty(list)){
            return new PageResult<DictSubjectRespVO>().setList(null).setTotal((long)0);
        }

        List<DictSubjectRespVO> voList = BeanUtils.toBean(list, DictSubjectRespVO.class);
        List<String> typeList = voList.stream().flatMap(s -> s.getSystemDictTypeList().stream()).distinct().toList();
        List<DictTypeRespDTO> listByTypeList = dictTypeApi.getListByTypeList(typeList);
        if(CollUtil.isNotEmpty(listByTypeList)){
            voList.forEach(s->{
                List<String> dictTypeList = s.getSystemDictTypeList();
                if(CollUtil.isNotEmpty(dictTypeList)){
                    List<DictTypeRespDTO> dtoList = new ArrayList<>();
                    dictTypeList.forEach(d->{
                        Optional<DictTypeRespDTO> first = listByTypeList.stream().filter(t -> Objects.equals(t.getType(), d)).findFirst();
                        first.ifPresent(dictTypeRespDTO -> dtoList.add(new DictTypeRespDTO().setType(d).setName(dictTypeRespDTO.getName())));
                    });
                    s.setSystemDictTypeDescList(dtoList);
                }


            });
        }
        return new PageResult<DictSubjectRespVO>().setList(voList).setTotal(doList.getTotal());
    }

    @Override
    public List<DictFeeRespVO> getFeeList() {
        List<DictSubjectDO> doList = dictSubjectMapper.selectList();
        if(CollUtil.isEmpty(doList)){
            return null;
        }
        return doList.stream().map(s->new DictFeeRespVO().setId(s.getId()).setFeeName(s.getFeeName()).setFeeDesc(s.getFeeDesc()).setShowFeeFlag(s.getShowFeeFlag()).setShowDescFlag(s.getShowDescFlag())).toList();
    }

    @Override
    public Map<Long, DictSubjectDO> getDictSubjectMap() {
        List<DictSubjectDO> dictSubjectDOList = dictSubjectMapper.selectList();
        if (CollUtil.isEmpty(dictSubjectDOList)){
            return Map.of();
        }
        Map<Long, DictSubjectDO> dictSubjectDOMap = new HashMap<>();
        dictSubjectDOList.forEach(s->{
            dictSubjectDOMap.merge(s.getId(),s, (old,now)->now);
        });
        return dictSubjectDOMap;
    }
}