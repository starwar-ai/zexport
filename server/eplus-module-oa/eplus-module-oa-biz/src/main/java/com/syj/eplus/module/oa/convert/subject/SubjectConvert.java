package com.syj.eplus.module.oa.convert.subject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectRespVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSaveReqVO;
import com.syj.eplus.module.oa.controller.admin.subject.vo.SubjectSimpleRespVO;
import com.syj.eplus.module.oa.dal.dataobject.subject.SubjectDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMultiMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.filterList;


@Mapper
public interface SubjectConvert {

        SubjectConvert INSTANCE = Mappers.getMapper(SubjectConvert.class);

        SubjectRespVO convert(SubjectDO subjectDO);

        default SubjectRespVO convertSubjectRespVO(SubjectDO subjectDO){
                SubjectRespVO subjectRespVO = convert(subjectDO);
                return subjectRespVO;
        }

    List<SubjectSimpleRespVO> convertSubjectSimpleRespVO (List<SubjectDO> beans);
        /**
         * 构建树形科目
         *
         * @param sourceList 原始数据
         * @return 转换后的父子级数组
         */
        default List<SubjectSimpleRespVO> convertChildrenList(List<SubjectDO> sourceList, Map<Long, SubjectDO> subjectDOMap) {
                if (CollUtil.isEmpty(sourceList)) {
                        return null;
                }
                List<SubjectSimpleRespVO> categorySimpleRespVOList = convertSubjectSimpleRespVO(sourceList);
                List<SubjectSimpleRespVO> childrenCategory = filterList(categorySimpleRespVOList, r -> ObjectUtil.isNotEmpty(r.getParentId())&&r.getParentId()>0);
                if (CollUtil.isEmpty(childrenCategory)) {
                        return null;
                }
                childrenCategory.forEach(s -> {
                        SubjectDO categoryDO = subjectDOMap.get(s.getParentId());
                        if (Objects.nonNull(categoryDO)) {
                                s.setParentName(categoryDO.getName());
                        }
                });
                Map<Long, List<SubjectSimpleRespVO>> parentChildrenCategory = convertMultiMap(childrenCategory, SubjectSimpleRespVO::getParentId);
                for (SubjectSimpleRespVO subjectSimpleRespVO : categorySimpleRespVOList) {
                        subjectSimpleRespVO.setChildren(parentChildrenCategory.get(subjectSimpleRespVO.getId()));
                }
                List<SubjectSimpleRespVO> categorySimpleRespVOS = filterList(categorySimpleRespVOList, r -> ObjectUtil.isNotEmpty(r.getParentId()));
                return categorySimpleRespVOS;
        }

        SubjectDO convertSubjectDO(SubjectSaveReqVO saveReqVO);
}