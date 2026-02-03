package com.syj.eplus.module.pms.controller.admin.sku.vo;

import cn.iocoder.yudao.framework.common.annotations.ChangeIgnore;
import cn.iocoder.yudao.module.system.api.logger.dto.OperateLogRespDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataRespVO;
import com.syj.eplus.module.pms.dal.dataobject.skuauxiliary.SkuAuxiliaryDO;
import com.syj.eplus.module.scm.api.quoteitem.dto.QuoteitemDTO;
import com.syj.eplus.module.sms.api.dto.HistoryTradePriceDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/5/8 15:44
 */
@Data
@Accessors(chain = false)
public class SkuInfoRespVO extends SkuRespVO {

    @Schema(description = "节点sku列表")
    @ChangeIgnore
    private List<SkuInfoRespVO> subProductList;

    @Schema(description = "配件列表")
    @ChangeIgnore
    private List<SkuInfoRespVO> accessoriesList;

    @Schema(description = "辅料配比列表")
    @ChangeIgnore
    private List<SkuAuxiliaryDO> skuAuxiliaryList;

    @Schema(description = "数量")
    private Integer qty;

    @Schema(description = "供应商报价")
    @ChangeIgnore
    private List<QuoteitemDTO> quoteitemDTOList;

    @Schema(description = "海关信息")
    private HsdataRespVO hsdata;

    @Schema(description = "操作日志")
    @ChangeIgnore
    private List<OperateLogRespDTO> operateLogRespDTOList;

    @Schema(description = "最近历史记录")
    @ChangeIgnore
    private List<HistoryTradePriceDTO> dealRecordList;

    @Schema(description = "最近历史成交价")
    private JsonAmount lastDealPrice;

    @Schema(description = "客户国家Id")
    private Long countryId;

    @Schema(description = "客户国家名称")
    private String countryName;

    @Schema(description = "客户地区名称")
    private String regionName;

    @Schema(description = "产品删除标记")
    private  Integer skuDeletedFlag;

}
