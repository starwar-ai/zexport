package com.syj.eplus.module.oa.service.subject;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectPageReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSimpleRespVO;
import com.syj.eplus.module.oa.convert.subject.SubjectConvert;
import com.syj.eplus.module.oa.dal.dataobject.dictsubject.DictSubjectDO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;
import com.syj.eplus.module.oa.dal.mysql.dictsubject.DictSubjectMapper;
import com.syj.eplus.module.oa.dal.mysql.subject.SubjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.SUBJECT_NOT_EXISTS;

/**
 * 科目 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class SubjectServiceImpl implements SubjectService {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private SubjectMapper subjectMapper;

    @Resource
    private DictSubjectMapper dictSubjectMapper;


    @Override
    public Long createSubject(SubjectSaveReqVO createReqVO) {
        SubjectDO subject = SubjectConvert.INSTANCE.convertSubjectDO(createReqVO);
        // 插入
        subjectMapper.insert(subject);
        // 返回
        return subject.getId();
    }

    @Override
    public void updateSubject(SubjectSaveReqVO updateReqVO) {
        // 校验存在
        validateSubjectExists(updateReqVO.getId());
        // 更新
        SubjectDO updateObj = SubjectConvert.INSTANCE.convertSubjectDO(updateReqVO);
        // 更新类别配置
        List<DictSubjectDO> dictSubjectList = dictSubjectMapper.selectList(DictSubjectDO::getSubjectId, updateReqVO.getId());
        dictSubjectList.forEach(item->{
            item.setSubjectName(updateObj.getName());
            item.setSubjectDescription(updateObj.getDescription());
        });
        dictSubjectMapper.updateBatch(dictSubjectList);
        subjectMapper.updateById(updateObj);
    }

    @Override
    public void deleteSubject(Long id) {
        // 校验存在
        validateSubjectExists(id);
        // 删除
        subjectMapper.deleteById(id);
        // 删除类别配置
        dictSubjectMapper.delete(DictSubjectDO::getSubjectId, id);
    }

    private void validateSubjectExists(Long id) {
        if (subjectMapper.selectById(id) == null) {
            throw exception(SUBJECT_NOT_EXISTS);
        }
    }
    @Override
    public SubjectRespVO getSubject(Long id) {
        SubjectDO subjectDO = subjectMapper.selectById(id);
        if (subjectDO == null) {
           return null;
        }
        return SubjectConvert.INSTANCE.convertSubjectRespVO(subjectDO);
    }

    @Override
    public PageResult<SubjectDO> getSubjectPage(SubjectPageReqVO pageReqVO) {
        return subjectMapper.selectPage(pageReqVO);
    }

    @Override
    public List<SubjectSimpleRespVO> getSimpleList() {
        List<SubjectDO> subjectDOS = subjectMapper.selectList();
        if (CollUtil.isEmpty(subjectDOS)) {
            return null;
        }
        return convertTree(subjectDOS, 0L);
    }

    private List<SubjectSimpleRespVO> convertTree(List<SubjectDO> subjectDOS,Long id){
        return subjectDOS.stream()
                // 过滤父节点
                .filter(parent -> Objects.equals(parent.getParentId(), id))
                // 把父节点children递归赋值成为子节点
                .map(s-> BeanUtils.toBean(s,SubjectSimpleRespVO.class))
                .peek(child -> child.setChildren(convertTree(subjectDOS,child.getId()))).toList();
    }

}