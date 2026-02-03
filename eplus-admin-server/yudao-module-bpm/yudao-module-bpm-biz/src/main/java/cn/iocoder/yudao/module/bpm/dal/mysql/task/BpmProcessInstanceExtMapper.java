package cn.iocoder.yudao.module.bpm.dal.mysql.task;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.instance.BpmProcessInstanceMyPageReqVO;
import cn.iocoder.yudao.module.bpm.dal.dataobject.task.BpmProcessInstanceExtDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BpmProcessInstanceExtMapper extends BaseMapperX<BpmProcessInstanceExtDO> {

    default PageResult<BpmProcessInstanceExtDO> selectPage(Long userId, BpmProcessInstanceMyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BpmProcessInstanceExtDO>()
                .eqIfPresent(BpmProcessInstanceExtDO::getStartUserId, userId)
                .likeIfPresent(BpmProcessInstanceExtDO::getName, reqVO.getName())
                .eqIfPresent(BpmProcessInstanceExtDO::getProcessDefinitionId, reqVO.getProcessDefinitionId())
                .eqIfPresent(BpmProcessInstanceExtDO::getCategory, reqVO.getCategory())
                .eqIfPresent(BpmProcessInstanceExtDO::getStatus, reqVO.getStatus())
                .eqIfPresent(BpmProcessInstanceExtDO::getResult, reqVO.getResult())
                .betweenIfPresent(BpmProcessInstanceExtDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(BpmProcessInstanceExtDO::getId));
    }

    default BpmProcessInstanceExtDO selectByProcessInstanceId(String processInstanceId) {
        return selectOne(BpmProcessInstanceExtDO::getProcessInstanceId, processInstanceId);
    }

    default void updateByProcessInstanceId(BpmProcessInstanceExtDO updateObj) {
        update(updateObj, new LambdaQueryWrapperX<BpmProcessInstanceExtDO>()
                .eq(BpmProcessInstanceExtDO::getProcessInstanceId, updateObj.getProcessInstanceId()));
    }

    /**
     * 通过拓展字段客户id查找流程实例
     * @param auditAbleId 流程任务业务id
     * @param processDefinitionId 流程id
     * @return
     */
    default BpmProcessInstanceExtDO selectByProcessInstanceByAuditAbleId(Long auditAbleId,String processDefinitionId) {
        List<BpmProcessInstanceExtDO> bpmProcessInstanceExtList = selectList(new LambdaQueryWrapperX<BpmProcessInstanceExtDO>().eq(BpmProcessInstanceExtDO::getProcessDefinitionId, processDefinitionId)
                .eq(BpmProcessInstanceExtDO::getAuditAbleId,auditAbleId)
                .orderByDesc(BpmProcessInstanceExtDO::getCreateTime));
        if (CollUtil.isEmpty(bpmProcessInstanceExtList)) {
            return null;
        }
        return bpmProcessInstanceExtList.get(0);
    }

}
