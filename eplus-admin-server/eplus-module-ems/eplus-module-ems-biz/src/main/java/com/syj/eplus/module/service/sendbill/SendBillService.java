package com.syj.eplus.module.service.sendbill;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillImportVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillPageReqVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillRespVO;
import com.syj.eplus.module.controller.admin.sendbill.vo.SendBillSaveReqVO;
import com.syj.eplus.module.dal.dataobject.sendbill.SendBillDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 寄件导入单据 Service 接口
 *
 * @author zhangcm
 */
public interface SendBillService {

    /**
     * 创建寄件导入单据
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSendBill(@Valid SendBillSaveReqVO createReqVO);

    /**
     * 更新寄件导入单据
     *
     * @param updateReqVO 更新信息
     */
    void updateSendBill(@Valid SendBillSaveReqVO updateReqVO);

    /**
     * 删除寄件导入单据
     *
     * @param id 编号
     */
    void deleteSendBill(Long id);

    /**
     * 获得寄件导入单据
     *
* @param  id 编号 
     * @return 寄件导入单据
     */
SendBillRespVO getSendBill(Long id);

    /**
     * 获得寄件导入单据分页
     *
     * @param pageReqVO 分页查询
     * @return 寄件导入单据分页
     */
    PageResult<SendBillDO> getSendBillPage(SendBillPageReqVO pageReqVO);

    List<SendBillRespVO> importExcelNotInsert(List<SendBillImportVO> list);

    void insertBillBatch(List<SendBillDO> billDoList);


    SendBillRespVO importExcel(List<SendBillImportVO> list);

    List<SendBillDO> getBYBatchCode(String batchCode);
}