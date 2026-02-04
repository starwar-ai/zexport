package com.syj.eplus.module.fms.convert.receipt;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.fms.api.payment.api.receipt.dto.ReceiptCreateDTO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptRespVO;
import com.syj.eplus.module.fms.controller.admin.receipt.vo.ReceiptSaveReqVO;
import com.syj.eplus.module.fms.dal.dataobject.receipt.ReceiptDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Mapper
public interface ReceiptConvert {

        ReceiptConvert INSTANCE = Mappers.getMapper(ReceiptConvert.class);

        ReceiptRespVO convert(ReceiptDO receiptDO);

        default ReceiptRespVO convertReceiptRespVO(ReceiptDO receiptDO,Map<Long, SimpleCompanyDTO> simpleCompanyDTOMap){
                ReceiptRespVO receiptRespVO = convert(receiptDO);
                if (CollUtil.isNotEmpty(simpleCompanyDTOMap)){
                        SimpleCompanyDTO simpleCompanyDTO = simpleCompanyDTOMap.get(receiptDO.getCompanyId());
                        if (Objects.nonNull(simpleCompanyDTO)){
                                receiptRespVO.setCompanyName(simpleCompanyDTO.getCompanyName());
                        }
                }
                return receiptRespVO;
        }

    ReceiptDO convertReceiptDO(ReceiptSaveReqVO saveReqVO);

        default List<ReceiptDO> convertReceiptDOList(List<ReceiptSaveReqVO> saveReqVOList){
                return CollectionUtils.convertList(saveReqVOList, this::convertReceiptDO);
        }

    ReceiptSaveReqVO convertReceiptSaveReqByDTO(ReceiptCreateDTO receiptCreateDTO);

    default PageResult<ReceiptRespVO> convertReceiptPageResult(PageResult<ReceiptDO> receiptDOPageResult, Map<Long, SimpleCompanyDTO> simpleCompanyDTOList) {
        List<ReceiptDO> receiptDOList = receiptDOPageResult.getList();
        if (CollUtil.isEmpty(receiptDOList)) {
            return PageResult.empty();
        }
        List<ReceiptRespVO> receiptRespVOList = receiptDOList.stream().map(s -> convertReceiptRespVO(s, simpleCompanyDTOList)).toList();
        return new PageResult<ReceiptRespVO>().setTotal(receiptDOPageResult.getTotal()).setList(receiptRespVOList);
    }
}