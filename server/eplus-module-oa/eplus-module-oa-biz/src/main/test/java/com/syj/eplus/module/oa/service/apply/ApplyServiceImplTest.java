package com.syj.eplus.module.oa.service.apply;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import com.syj.eplus.framework.common.enums.FeeShareSourceTypeEnum;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyPageReqVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplyRespVO;
import com.syj.eplus.module.oa.controller.admin.apply.vo.ApplySaveReqVO;
import com.syj.eplus.module.oa.dal.dataobject.apply.ApplyDO;
import com.syj.eplus.module.oa.dal.mysql.apply.ApplyMapper;
import javax.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import static cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils.buildBetweenTime;
import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.*;
import static com.syj.eplus.module.oa.enums.ErrorCodeConstants.APPLY_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ApplyServiceImpl} 的单元测试类
 *
 * @author ePlus
 */
@Import(ApplyServiceImpl.class)
public class ApplyServiceImplTest extends BaseDbUnitTest {

    @Resource
    private ApplyServiceImpl applyService;

    @Resource
    private ApplyMapper applyMapper;

    @Test
    public void testCreateApply_success() {
        // 准备参数
        ApplySaveReqVO createReqVO = randomPojo(ApplySaveReqVO.class).setId(null);

        // 调用
        Long applyId = applyService.createApply(createReqVO, FeeShareSourceTypeEnum.ENTERTAIN_APPLY);
        // 断言
        assertNotNull(applyId);
        // 校验记录的属性是否正确
        ApplyDO apply = applyMapper.selectById(applyId);
        assertPojoEquals(createReqVO, apply, "id");
    }

    @Test
    public void testUpdateApply_success() {
        // mock 数据
        ApplyDO dbApply = randomPojo(ApplyDO.class);
        applyMapper.insert(dbApply);// @Sql: 先插入出一条存在的数据
        // 准备参数
        ApplySaveReqVO updateReqVO = randomPojo(ApplySaveReqVO.class, o -> {
            o.setId(dbApply.getId()); // 设置更新的 ID
        });

        // 调用
        applyService.updateApply(updateReqVO, FeeShareSourceTypeEnum.ENTERTAIN_APPLY);
        // 校验是否更新正确
        ApplyDO apply = applyMapper.selectById(updateReqVO.getId()); // 获取最新的
        assertPojoEquals(updateReqVO, apply);
    }

    @Test
    public void testUpdateApply_notExists() {
        // 准备参数
        ApplySaveReqVO updateReqVO = randomPojo(ApplySaveReqVO.class);

        // 调用, 并断言异常
        assertServiceException(() -> applyService.updateApply(updateReqVO, FeeShareSourceTypeEnum.ENTERTAIN_APPLY), APPLY_NOT_EXISTS);
    }

    @Test
    public void testDeleteApply_success() {
        // mock 数据
        ApplyDO dbApply = randomPojo(ApplyDO.class);
        applyMapper.insert(dbApply);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbApply.getId();

        // 调用
        applyService.deleteApply(id);
       // 校验数据不存在了
       assertNull(applyMapper.selectById(id));
    }

    @Test
    public void testDeleteApply_notExists() {
        // 准备参数
        Long id = randomLongId();

        // 调用, 并断言异常
        assertServiceException(() -> applyService.deleteApply(id), APPLY_NOT_EXISTS);
    }

    @Test
    @Disabled  // TODO 请修改 null 为需要的值，然后删除 @Disabled 注解
    public void testGetApplyPage() {
       // mock 数据
       ApplyDO dbApply = randomPojo(ApplyDO.class, o -> { // 等会查询到
           o.setIntendedObjectives(null);
           o.setCode(null);
           o.setWecomId(null);
           o.setApplyTime(null);
           o.setPurpose(null);
           o.setDest(null);
           o.setStartTime(null);
           o.setEndTime(null);
           o.setDuration(null);
           o.setTransportationType(null);
           o.setEntertainEntourage(null);
           o.setEntertainLevel(null);
           o.setEntertainNum(null);
           o.setEntertainTime(null);
           o.setEntertainName(null);
           o.setEntertainType(null);
           o.setGeneralExpense(null);
           o.setRemarks(null);
           o.setIsApplyExpense(null);
           o.setAuditStatus(null);
           o.setAnnex(null);
           o.setCreateTime(null);
       });
       applyMapper.insert(dbApply);
       // 测试 intendedObjectives 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setIntendedObjectives(null)));
       // 测试 code 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setCode(null)));
       // 测试 wecomId 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setWecomId(null)));
       // 测试 applyTime 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setApplyTime(null)));
       // 测试 purpose 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setPurpose(null)));
       // 测试 dest 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setDest(null)));
       // 测试 startTime 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setStartTime(null)));
       // 测试 endTime 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEndTime(null)));
       // 测试 duration 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setDuration(null)));
       // 测试 transportationType 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setTransportationType(null)));
       // 测试 entertainEntourage 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEntertainEntourage(null)));
       // 测试 entertainLevel 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEntertainLevel(null)));
       // 测试 entertainNum 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEntertainNum(null)));
       // 测试 entertainTime 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEntertainTime(null)));
       // 测试 entertainName 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEntertainName(null)));
       // 测试 entertainType 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setEntertainType(null)));
       // 测试 generalExpense 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setGeneralExpense(null)));
       // 测试 remarks 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setRemarks(null)));
       // 测试 isApplyExpense 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setIsApplyExpense(null)));
       // 测试 auditStatus 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setAuditStatus(null)));
       // 测试 annex 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setAnnex(null)));
       // 测试 createTime 不匹配
       applyMapper.insert(cloneIgnoreId(dbApply, o -> o.setCreateTime(null)));
       // 准备参数
       ApplyPageReqVO reqVO = new ApplyPageReqVO();
       reqVO.setIntendedObjectives(null);
       reqVO.setCode(null);
       reqVO.setWecomId(null);
       reqVO.setApplyTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setPurpose(null);
       reqVO.setDest(null);
       reqVO.setStartTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setEndTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setDuration(null);
       reqVO.setTransportationType(null);
       reqVO.setEntertainEntourage(null);
       reqVO.setEntertainLevel(null);
       reqVO.setEntertainNum(null);
       reqVO.setEntertainTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));
       reqVO.setEntertainName(null);
       reqVO.setEntertainType(null);
       reqVO.setGeneralExpense(null);
       reqVO.setRemarks(null);
       reqVO.setIsApplyExpense(null);
       reqVO.setAuditStatus(null);
       reqVO.setAnnex(null);
       reqVO.setCreateTime(buildBetweenTime(2023, 2, 1, 2023, 2, 28));

       // 调用
       PageResult<ApplyRespVO> pageResult = applyService.getApplyPage(reqVO);
       // 断言
       assertEquals(1, pageResult.getTotal());
       assertEquals(1, pageResult.getList().size());
       assertPojoEquals(dbApply, pageResult.getList().get(0));
    }

}