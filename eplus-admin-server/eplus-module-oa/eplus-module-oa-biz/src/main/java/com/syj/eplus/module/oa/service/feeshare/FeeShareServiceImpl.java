package com.syj.eplus.module.oa.service.feeshare;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.dict.CommonDict;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.dict.core.util.DictFrameworkUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.bpm.api.task.BpmProcessInstanceApi;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskApproveReqDTO;
import cn.iocoder.yudao.module.bpm.api.task.dto.BpmTaskRejectReqDTO;
import cn.iocoder.yudao.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import cn.iocoder.yudao.module.infra.api.rate.RateApi;
import cn.iocoder.yudao.module.system.api.dept.DeptApi;
import cn.iocoder.yudao.module.system.api.dept.dto.DeptRespDTO;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.dict.CommonCurrencyDict;
import com.syj.eplus.framework.common.dict.FeeShareBusinessTypeDict;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.*;
import com.syj.eplus.framework.common.util.NumUtil;
import com.syj.eplus.module.crm.api.cust.CustApi;
import com.syj.eplus.module.crm.api.cust.dto.CustAllDTO;
import com.syj.eplus.module.dms.enums.api.ForwarderFeeApi;
import com.syj.eplus.module.ems.api.send.EmsSendApi;
import com.syj.eplus.module.exms.api.exhibition.ExhibitionApi;
import com.syj.eplus.module.exms.api.exhibition.dto.ExhibitionDTO;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import com.syj.eplus.module.oa.api.feeshare.dto.*;
import com.syj.eplus.module.oa.controller.admin.feeshare.vo.*;
import com.syj.eplus.module.oa.convert.feeshare.FeeShareConvert;
import com.syj.eplus.module.oa.dal.dataobject.feeshare.FeeShareDO;
import com.syj.eplus.module.oa.dal.dataobject.feesharedesc.FeeShareDescDO;
import com.syj.eplus.module.oa.dal.dataobject.feeshareitem.FeeShareItemDO;
import com.syj.eplus.module.oa.dal.dataobject.paymentapp.PaymentAppDO;
import com.syj.eplus.module.oa.dal.dataobject.reimb.ReimbDO;
import com.syj.eplus.module.oa.dal.mysql.feeshare.FeeShareMapper;
import com.syj.eplus.module.oa.dal.mysql.paymentapp.PaymentAppMapper;
import com.syj.eplus.module.oa.dal.mysql.reimb.ReimbMapper;
import com.syj.eplus.module.oa.service.entertain.EntertainReimbService;
import com.syj.eplus.module.oa.service.feeshareitem.FeeShareItemService;
import com.syj.eplus.module.oa.service.generalreimb.GeneralReimbService;
import com.syj.eplus.module.oa.service.other.OtherReimbService;
import com.syj.eplus.module.oa.service.paymentapp.PaymentAppService;
import com.syj.eplus.module.oa.service.travelreimb.TravelReimbService;
import com.syj.eplus.module.pjms.api.ProjectApi;
import com.syj.eplus.module.pjms.api.dto.ProjectDTO;
import com.syj.eplus.module.scm.api.purchasecontract.PurchaseContractApi;
import com.syj.eplus.module.scm.api.purchasecontract.dto.PurchaseContractAllDTO;
import com.syj.eplus.module.scm.api.vender.VenderApi;
import com.syj.eplus.module.scm.api.vender.dto.VenderAllDTO;
import com.syj.eplus.module.sms.api.SaleContractApi;
import com.syj.eplus.module.sms.api.dto.SmsContractAllDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;
import static com.syj.eplus.module.sms.enums.ErrorCodeConstants.CURRENCY_RATE_NOT_EXISTS;


/**
 * 费用归集 Service 实现类
 *
 * @author zhangcm
 */
@Service
@Validated
public class FeeShareServiceImpl implements FeeShareService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private FeeShareMapper feeShareMapper;
    @Resource
    private FeeShareItemService feeShareItemService;
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private ReimbMapper reimbMapper;
    @Resource
    private PaymentAppMapper paymentAppMapper;
    @Resource
    private EmsSendApi emsSendApi;

    @Resource
    private ForwarderFeeApi forwarderFeeApi;
    @Resource
    private CustApi custApi;
    @Resource
    private DeptApi deptApi;
    @Resource
    private VenderApi venderApi;

    @Resource
    private PurchaseContractApi purchaseContractApi;

    @Resource
    private TravelReimbService travelReimbService;

    @Resource
    private PaymentAppService paymentAppService;
    @Resource
    private EntertainReimbService entertainReimbService  ;
    @Resource
    private GeneralReimbService generalReimbService;
    @Resource
    private OtherReimbService otherReimbService;
    @Resource
    private SaleContractApi saleContractApi;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private RateApi rateApi;
    @Resource
    private BpmProcessInstanceApi bpmProcessInstanceApi;
    @Resource
    private ExhibitionApi exhibitionApi;
    @Resource
    private ProjectApi projectApi;
    private static final String CODE_PREFIX = "FS";
    public static final String SN_TYPE = "OA_FEE_SHARE";
    private static final String PROCESS_DEFINITION_KEY = "oa_fee_share";
    private FeeSharePageReqVO pageReqVO;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createFeeShare(FeeShareSaveReqVO createReqVO) {
        if(Objects.isNull(createReqVO.getPreCollectionFlag())){
            createReqVO.setPreCollectionFlag(0);
        }
        FeeShareDO feeShare = FeeShareConvert.INSTANCE.convertFeeShareDO(createReqVO);
        String code = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        feeShare.setCode(code);
        // 插入
        feeShareMapper.insert(feeShare);
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(createReqVO.getSubmitFlag())) {
            submitTask(feeShare.getId(), WebFrameworkUtils.getLoginUserId(),createReqVO.getBusinessCode());
        }
        // 返回
        return feeShare.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFeeShare(FeeShareSaveReqVO updateReqVO) {
        if(Objects.isNull(updateReqVO.getPreCollectionFlag())){
            updateReqVO.setPreCollectionFlag(0);
        }
        // 校验存在
        FeeShareDO feeShareDO = validateFeeShareExists(updateReqVO.getId());
        // 更新
        FeeShareDO updateObj = FeeShareConvert.INSTANCE.convertFeeShareDO(updateReqVO);
        updateObj.setBusinessType(feeShareDO.getBusinessType());
        updateObj.setBusinessId(feeShareDO.getBusinessId());
        updateObj.setBusinessCode(feeShareDO.getBusinessCode());
        List<JsonAmount> amountList = updateReqVO.getTotalAmountList();
        JsonAmount feeShareAmount = getFeeShareAmount(amountList);
        updateObj.setAmount(feeShareAmount);
        updateObj.setId(feeShareDO.getId());
        feeShareMapper.updateById(updateObj);
        List<FeeShareItemDTO> children = new ArrayList<>();
        List<FeeShareItemDTO> custChildren = updateReqVO.getCrmChildren();
        if (CollUtil.isNotEmpty(custChildren)) {
            custChildren.forEach(s -> s.setBusinessSubjectType(OaFeeShareSourceType.CUST.getCode()));
            children.addAll(custChildren);
        }
        List<FeeShareItemDTO> venderChildren = updateReqVO.getVenderChildren();
        if (CollUtil.isNotEmpty(venderChildren)) {
            venderChildren.forEach(s -> s.setBusinessSubjectType(OaFeeShareSourceType.VENDEOR.getCode()));
            children.addAll(venderChildren);
        }
        List<FeeShareItemDTO> smsChildren = updateReqVO.getSmsChildren();
        if (CollUtil.isNotEmpty(smsChildren)) {
            smsChildren.forEach(s -> s.setBusinessSubjectType(OaFeeShareSourceType.SMS.getCode()));
            children.addAll(smsChildren);
        }
        List<FeeShareItemDTO> purchaseChildren = updateReqVO.getPurchaseChildren();
        if (CollUtil.isNotEmpty(purchaseChildren)) {
            purchaseChildren.forEach(s -> s.setBusinessSubjectType(OaFeeShareSourceType.PURCHASE.getCode()));
            children.addAll(purchaseChildren);
        }
        if (CollUtil.isNotEmpty(children)) {
            children.forEach(s->s.setBusinessSubjectCode(s.getCode()));
            feeShareItemService.createFeeShareItemBatch(children, updateReqVO.getId());
        }
        if (SubmitFlagEnum.SUBMIT.getStatus().equals(updateReqVO.getSubmitFlag())) {
            submitTask(updateObj.getId(), WebFrameworkUtils.getLoginUserId(),updateObj.getBusinessCode());
        }
    }

    private void deleteExceptIdsByBusnissCode(Integer type, String code, List<Long> idList) {
        List<FeeShareDO> list = feeShareMapper.selectList(new LambdaQueryWrapperX<FeeShareDO>()
                .eq(FeeShareDO::getBusinessType, type)
                .eq(FeeShareDO::getBusinessCode, code)).stream().toList();
        if (CollUtil.isEmpty(list)) {
            return;
        }
        Set<Long> keepIdSet = CollUtil.isEmpty(idList) ? Collections.emptySet() : new HashSet<>(idList);
        List<Long> deleteIdList = list.stream()
                .map(FeeShareDO::getId)
                .filter(id -> CollUtil.isEmpty(keepIdSet) || !keepIdSet.contains(id))
                .toList();
        if (CollUtil.isEmpty(deleteIdList)) {
            return;
        }
        feeShareMapper.deleteBatchIds(deleteIdList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFeeShareList(FeeShareListSaveReqVO reqVO) {
        List<FeeShareSaveReqVO> updateReqVO = reqVO.getFeeShare();
        if(updateReqVO.isEmpty())
            return;
        updateReqVO.forEach(s->{
             s.setBusinessId(reqVO.getBusinessId()).setBusinessCode(reqVO.getBusinessCode());
             s.setBusinessType(reqVO.getBusinessType());
        });

        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept userDept = adminUserApi.getUserDeptByUserId(loginUserId);
        List<Long> idList = updateReqVO.stream().map(FeeShareSaveReqVO::getId).distinct().toList();
        deleteExceptIdsByBusnissCode(reqVO.getBusinessType(),reqVO.getBusinessCode(),idList);
        updateReqVO.forEach(feeShare->{
            FeeShareDO feeShareDO = FeeShareConvert.INSTANCE.convertFeeShareDO(feeShare);

            feeShareDO.setBusinessType(feeShare.getBusinessType());
            feeShareDO.setBusinessId(feeShare.getBusinessId());
            feeShareDO.setExhibitionId(feeShare.getExhibitionId());
            feeShareDO.setProjectId(feeShare.getProjectId());
            feeShareDO.setBusinessCode(feeShare.getBusinessCode());
            feeShareDO.setAmount(feeShare.getAmount());
            if(feeShareDO.getId() == null){
                feeShareDO.setInputUser(userDept).setShareUser(userDept);
                feeShareDO.setSourceStatus(2).setStatus(1);
                feeShareDO.setCompanyId(reqVO.getCompanyId()).setCompanyName(reqVO.getCompanyName());
                feeShareMapper.insert(feeShareDO);
            }else {
                feeShareMapper.updateById(feeShareDO);
            }

            List<FeeShareItemDTO> children = new ArrayList<>();
            List<FeeShareItemDTO> custChildren = feeShare.getCrmChildren();
            if (CollUtil.isNotEmpty(custChildren)) {
                custChildren.forEach(s ->{
                    s.setBusinessSubjectType(OaFeeShareSourceType.CUST.getCode());
                    s.setDescId(s.getDescId());
                });
                children.addAll(custChildren);
            }
            List<FeeShareItemDTO> venderChildren = feeShare.getVenderChildren();
            if (CollUtil.isNotEmpty(venderChildren)) {
                venderChildren.forEach(s -> {
                    s.setBusinessSubjectType(OaFeeShareSourceType.VENDEOR.getCode());
                    s.setDescId(s.getDescId());
                });
                children.addAll(venderChildren);
            }
            List<FeeShareItemDTO> smsChildren = feeShare.getSmsChildren();
            if (CollUtil.isNotEmpty(smsChildren)) {
                smsChildren.forEach(s ->{
                    s.setBusinessSubjectType(OaFeeShareSourceType.SMS.getCode());
                    s.setDescId(s.getDescId());
                });
                children.addAll(smsChildren);
            }
            List<FeeShareItemDTO> purchaseChildren = feeShare.getPurchaseChildren();
            if (CollUtil.isNotEmpty(purchaseChildren)) {
                purchaseChildren.forEach(s -> {
                    s.setBusinessSubjectType(OaFeeShareSourceType.PURCHASE.getCode());
                    s.setDescId(s.getDescId());
                });
                children.addAll(purchaseChildren);
            }

            List<FeeShareItemDTO> userChildren = feeShare.getUserChildren();
            if (CollUtil.isNotEmpty(userChildren)) {
                userChildren.forEach(s -> {
                    s.setBusinessSubjectType(OaFeeShareSourceType.USER.getCode());
                    s.setDescId(s.getDescId());
                });
                children.addAll(userChildren);
            }

            if (CollUtil.isNotEmpty(children)) {
                children.forEach(s-> {
                    s.setBusinessSubjectCode(s.getCode());
                    s.setDescId(s.getDescId());
                });
                feeShareItemService.createFeeShareItemBatch(children, feeShareDO.getId());
            }
        });

        if (SubmitFlagEnum.SUBMIT.getStatus().equals(reqVO.getSubmitFlag())) {
            submitTask(reqVO.getBusinessId(), WebFrameworkUtils.getLoginUserId(),reqVO.getBusinessCode());
        }
    }


    @Override
    public void deleteFeeShare(Long id) {
        // 校验存在
        validateFeeShareExists(id);
        // 删除
        feeShareMapper.deleteById(id);
    }

    private FeeShareDO validateFeeShareExists(Long id) {
        FeeShareDO feeShareDO = feeShareMapper.selectById(id);
        if ( feeShareDO== null) {
            throw exception(FEE_SHARE_NOT_EXISTS);
        }
        if(feeShareDO.getPreCollectionFlag() == 1){
            throw exception(FEE_SHARE_IS_PRE_COLLECTION);
        }
        return feeShareDO;
    }

    private FeeShareDO validateFeeShareExistsByBusinessId(Long businessId){
        FeeShareDO feeShareDO = feeShareMapper.selectOne(
                new LambdaQueryWrapperX<FeeShareDO>()
                        .eq(FeeShareDO::getBusinessId,businessId)
                        .ne(FeeShareDO::getPreCollectionFlag,1));
        if ( feeShareDO== null) {
            throw exception(FEE_SHARE_NOT_EXISTS);
        }
        return feeShareDO;
    }

    @Override
    public FeeShareRespVO getFeeShare(FeeShareDetailReq req) {
        Long id = req.getId();
        String processInstanceId = req.getProcessInstanceId();
        FeeShareDO feeShareDO;
        if (Objects.nonNull(id)) {
            feeShareDO = feeShareMapper.selectOne(
                    new LambdaQueryWrapperX<FeeShareDO>()
                            .eq(FeeShareDO::getId,id)
                            .ne(FeeShareDO::getPreCollectionFlag,1));
        } else {
            feeShareDO = feeShareMapper.selectOne(
                    new LambdaQueryWrapperX<FeeShareDO>()
                            .eq(FeeShareDO::getProcessInstanceId,processInstanceId)
                            .ne(FeeShareDO::getPreCollectionFlag,1));
        }
        if (feeShareDO == null) {
            return null;
        }
        FeeShareRespVO feeShareRespVO = FeeShareConvert.INSTANCE.convertFeeShareRespVO(feeShareDO);
        Long exhibitionId = feeShareDO.getExhibitionId();
        ExhibitionDTO exhibitionDTO = exhibitionApi.getExhibitionById(exhibitionId, null);
        if (Objects.nonNull(exhibitionDTO)){
            feeShareRespVO.setExhibitionName(exhibitionDTO.getExhibitionName());
        }
        Long projectId = feeShareDO.getProjectId();
        ProjectDTO projectDTO = projectApi.getProjectDTOById(projectId);
        if (Objects.nonNull(projectDTO)){
            feeShareRespVO.setProjectName(projectDTO.getName());
        }
        return feeShareRespVO;
    }

    @Override
    public PageResult<FeeSharePageRespVO> getFeeSharePage(FeeSharePageReqVO pageReqVO) {
        this.pageReqVO = pageReqVO;
        pageReqVO.setPreCollectionFlag(0);
        Integer pageNo = pageReqVO.getPageNo();
        Integer pageSize = pageReqVO.getPageSize();
//        pageReqVO.setSourceStatus(BpmProcessInstanceResultEnum.APPROVE.getResult());
        pageReqVO.setPageSize(-1);
        //转换前端传参
        if(Objects.isNull(pageReqVO.getStatus()) ){

        } else if (pageReqVO.getStatus() == 0) {
            pageReqVO.setStatus(0).setNotAuditStatus(1);
         } else if (pageReqVO.getStatus() == 1) {
            pageReqVO.setAuditStatus(1);
        }else if (pageReqVO.getStatus() == 2){
            pageReqVO.setStatus(1).setNotAuditStatus(1);
        }
        PageResult<FeeShareDO> feeShareDOPageResult = feeShareMapper.selectPage(pageReqVO);
        List<FeeShareDO> list = feeShareDOPageResult.getList();

        if(CollUtil.isNotEmpty(list)){
            List<Long> idList = list.stream().map(FeeShareDO::getId).toList();
            Map<Long, List<FeeShareDescItemDTO>> descListMap = GetFeeShareDescList(idList);
            Map<Long, String> longStringMap = GetFeeShareDesc(idList);

               list.forEach(s->{
                   if(CollUtil.isNotEmpty(longStringMap)){
                       s.setFeeShareDetail(longStringMap.get(s.getId()));
                   }
                   if(CollUtil.isNotEmpty(descListMap)){
                       s.setDetailList(descListMap.get(s.getId()));
                   }
               });
            List<FeeSharePageRespVO> result = new ArrayList<>();
            Map<String, List<FeeShareDO>> listMap = list.stream().collect(Collectors.groupingBy(FeeShareDO::getBusinessCode));
            if(CollUtil.isNotEmpty(listMap)){
                Integer no = 0;
                for (var map : listMap.entrySet()){
                    if(no >= pageNo * pageSize - pageSize && no < pageNo * pageSize ){
                        Optional<FeeShareDO> first = map.getValue().stream().findFirst();
                        if(first.isPresent()){
                            FeeSharePageRespVO vo = BeanUtils.toBean(first.get(), FeeSharePageRespVO.class);
                            vo.setFeeShareDetail("");
                            List<String> descList = map.getValue().stream().map(FeeShareDO::getFeeShareDetail).toList();
                            vo.setFeeShareDetailList(descList);

                            List<FeeShareDescItemDTO> dtoItems = map.getValue().stream().flatMap(feeShareDO -> feeShareDO.getDetailList().stream()).toList();
                            vo.setDetailList(dtoItems);

                            ReimbDO reimbDO = reimbMapper.selectOne(new LambdaQueryWrapperX<ReimbDO>().eq(ReimbDO::getCode, map.getKey())
                                    .eq(ReimbDO::getReimbType, first.get().getBusinessType()));
                            if(Objects.nonNull(reimbDO)){
                                vo.setAmount(reimbDO.getAmountList().get(0));
                            }
                            //查询总金额
                            if(vo.getBusinessType() == FeeShareBusinessTypeDict.TRAVEL_APP){
                                JsonAmount amount = travelReimbService.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }else if (vo.getBusinessType() == FeeShareBusinessTypeDict.GENERAL_APP){
                                JsonAmount amount = generalReimbService.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }else if(vo.getBusinessType() == FeeShareBusinessTypeDict.OTHER_APP){
                                JsonAmount amount = otherReimbService.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }else if (vo.getBusinessType() == FeeShareBusinessTypeDict.ENTERTAIN_APP){
                                JsonAmount amount = entertainReimbService.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }else if (vo.getBusinessType() == FeeShareBusinessTypeDict.PAYMENT_APP){
                                JsonAmount amount = paymentAppService.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }else if (vo.getBusinessType() == FeeShareBusinessTypeDict.SEND_APP){
                                JsonAmount amount = emsSendApi.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }else if (vo.getBusinessType() == FeeShareBusinessTypeDict.FORWARDER_FEE){
                                JsonAmount amount = forwarderFeeApi.getFeeShareAmountByCode(vo.getBusinessCode());
                                if(Objects.nonNull(amount)){
                                    vo.setAmount(amount);
                                }
                            }

                            result.add(vo);
                        }
                    }
                    no++;
                }
            }
            // 按id倒序
            if (CollUtil.isNotEmpty(result)){
                result = result.stream().sorted(Comparator.comparing(FeeSharePageRespVO::getId).reversed()).toList();
            }
            return new PageResult<FeeSharePageRespVO>().setList(result).setTotal((long) listMap.size());
        }
        return new PageResult<>();
    }


    private Map<Long,String> GetFeeShareDesc(List<Long> feeShareIdList){
        if(CollUtil.isEmpty(feeShareIdList)){
            return null;
        }
        Map<Long,String> result = new HashMap<>();
        List<Long> idList = feeShareIdList.stream().distinct().toList();
        if(CollUtil.isEmpty(idList)){
            return result;
        }
        List<FeeShareDO> feeShareDOList = feeShareMapper.selectList(FeeShareDO::getId, idList);
        if(CollUtil.isEmpty(feeShareDOList)){
            return result;
        }
        Map<Long,List<FeeShareItemDO>> itemMap = feeShareItemService.getListByFeeShareIdList(idList);
        feeShareDOList.forEach(s->{
            String desc=  "";
            Integer feeShareType = s.getFeeShareType();
            String feeShareTypeString = DictFrameworkUtils.getDictDataLabel("fee_share_type", feeShareType);
            if(Objects.nonNull(feeShareType)){
                if(feeShareType.equals(OaFeeShareTypeEnum.OPERATION.getCode())){
                    String companyOperateType = DictFrameworkUtils.getDictDataLabel("company_operate_type", s.getRelationType());
                    desc = feeShareTypeString + CommonDict.HYPHEN + companyOperateType + CommonDict.HYPHEN+ s.getAmount().showAmount();
                }
                if(feeShareType.equals(OaFeeShareTypeEnum.MARKET.getCode())){
                    String marketExpansionType = DictFrameworkUtils.getDictDataLabel("market_expansion_type", s.getRelationType());
                    desc = feeShareTypeString + CommonDict.HYPHEN + marketExpansionType+CommonDict.HYPHEN+ s.getAmount().showAmount();
                }
                if(feeShareType.equals(OaFeeShareTypeEnum.PROJECT.getCode())){
                    if(Objects.equals(s.getRelationType(), OaProjectTypeEnum.DEV.getCode())){
                        ProjectDTO dto = projectApi.getProjectDTOById(s.getProjectId());
                        desc = feeShareTypeString + CommonDict.HYPHEN + dto.getName() + CommonDict.HYPHEN + s.getAmount().showAmount();
                    }
                    else if(Objects.equals(s.getRelationType(), OaProjectTypeEnum.EXMS.getCode())){
                        ExhibitionDTO dto = exhibitionApi.getExhibitionById(s.getExhibitionId());
                        desc = feeShareTypeString + CommonDict.HYPHEN +dto.getExmsEventCategoryName() + CommonDict.HYPHEN + dto.getExhibitionName() + CommonDict.HYPHEN + s.getAmount().showAmount();
                    }else if(Objects.equals(s.getRelationType(), OaProjectTypeEnum.BRAND.getCode())){
                        String brandType = DictFrameworkUtils.getDictDataLabel("fee_brand_type", s.getRelationType());
                        desc = feeShareTypeString + CommonDict.HYPHEN + brandType+CommonDict.HYPHEN+ s.getAmount().showAmount();
                    }
                }
            }

            if(CollUtil.isNotEmpty(itemMap)){
                List<FeeShareItemDO> itemDOList = itemMap.get(s.getId());
                if(CollUtil.isNotEmpty(itemDOList)) {
                    //销售
                    if (Objects.equals(s.getOrderType(), OaOrderTypeEnum.SMS.getCode())) {
                        List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.SMS.getCode())).toList();
                        if (CollUtil.isNotEmpty(list)) {
                            desc = list.stream().map(itemDO->{
                                JsonAmount amount = itemDO.getAmount();
                                String amountString = amount == null ? "" : CommonDict.HYPHEN + amount.showAmount();
                                if (Objects.nonNull(amount)){
                                  return  "销售订单 " + itemDO.getBusinessSubjectCode() + amountString ;
                                }else {
                                    return "销售订单 " + itemDO.getBusinessSubjectCode();
                                }
                            }).collect(Collectors.joining(","));
                        }
                    }
                    //采购
                    if (Objects.equals(s.getOrderType(), OaOrderTypeEnum.PURCHASE.getCode())) {
                        List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.PURCHASE.getCode())).toList();
                        if (CollUtil.isNotEmpty(list)) {
                            desc = list.stream().map(itemDO->{
                                JsonAmount amount = itemDO.getAmount();
                                String amountString = amount == null ? "" : CommonDict.HYPHEN + amount.showAmount();
                                if (Objects.nonNull(amount)){
                                   return "采购订单 " + itemDO.getBusinessSubjectCode() + amountString;
                                }else {
                                    return "采购订单 " + itemDO.getBusinessSubjectCode();
                                }
                            }).collect(Collectors.joining(","));

                        }
                    }

                    if (Objects.equals(s.getOrderType(), OaOrderTypeEnum.OTHER.getCode())
                            &&  Objects.equals(s.getFeeShareType(),OaFeeShareTypeEnum.NOORDER.getCode()) ) {
                        //客户
                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.CUST.getCode() )){
                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.CUST.getCode())).toList();
                            if (CollUtil.isNotEmpty(list)) {
                                Map<String, CustAllDTO> custMap = custApi.getCustByCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
                                List<String> nameList = new ArrayList<>();
                                if(CollUtil.isNotEmpty(custMap)){
                                    list.forEach(cus->{
                                        CustAllDTO custAllDTO = custMap.get(cus.getBusinessSubjectCode());
                                        JsonAmount amount = cus.getAmount();
                                        String amountString = amount == null ? "" : CommonDict.HYPHEN + amount.showAmount();
                                        if(Objects.nonNull(custAllDTO)){
                                            nameList.add( feeShareTypeString + CommonDict.HYPHEN +  "核心客户 " +custAllDTO.getName() + amountString);
                                        }
                                        else {
                                            nameList.add(feeShareTypeString + CommonDict.HYPHEN + "核心客户 " +cus.getBusinessSubjectCode()+ amountString);
                                        }
                                    });
                                }
                                desc = String.join(",", nameList);
                            }
                        }
                        //供应商
                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.VENDER.getCode() )){
                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.VENDEOR.getCode())).toList();
                            if(CollUtil.isNotEmpty(list)){
                                List<String> nameList = new ArrayList<>();
                                Map<String, VenderAllDTO> venderMap = venderApi.getVenderByCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
                                if(CollUtil.isNotEmpty(venderMap)){
                                    list.forEach(vender->{
                                        VenderAllDTO venderAllDTO = venderMap.get(vender.getBusinessSubjectCode());
                                        JsonAmount amount = vender.getAmount();
                                        String amountString = amount == null ? "" : CommonDict.HYPHEN + amount.showAmount();
                                        if(Objects.nonNull(venderAllDTO)){
                                            nameList.add(feeShareTypeString + CommonDict.HYPHEN + "核心供应商 " +venderAllDTO.getName() + amountString);
                                        }else {
                                            nameList.add(feeShareTypeString + CommonDict.HYPHEN + "核心供应商 " +vender.getBusinessSubjectCode()+ amountString);
                                        }
                                    });
                                }
                                desc = String.join(",", nameList)  ;
                            }
                        }
                        //部门
//                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.DEPT.getCode() )){
//                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
//                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.DEPT.getCode())).toList();
//                            if (CollUtil.isNotEmpty(list)) {
//                                List<String> nameList = new ArrayList<>();
//                                Map<String, DeptRespDTO> deptMap = deptApi.getDeptByCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
//                                if(CollUtil.isNotEmpty(deptMap)){
//                                    list.forEach(dept->{
//                                        DeptRespDTO deptRespDTO = deptMap.get(dept.getBusinessSubjectCode());
//                                        JsonAmount amount = dept.getAmount();
//                                        String amountString = amount == null ? "" : CommonDict.HYPHEN + amount.showAmount();
//                                        if(Objects.nonNull(deptRespDTO)){
//                                            nameList.add("部门 " + deptRespDTO.getName() + amountString);
//                                        }else {
//                                            nameList.add("部门 " +  dept.getBusinessSubjectCode()+ amountString);
//                                        }
//                                    });
//                                }
//                                desc = String.join(",", nameList);
//                            }
//                        }
                        //个人
                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.DEPT.getCode() )) {
                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.USER.getCode())).toList();
                            if (CollUtil.isNotEmpty(list)) {
                                List<String> nameList = new ArrayList<>();
                                Map<String, AdminUserRespDTO> userMap = adminUserApi.getUserListByUserCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
                                List<Long> deptIds = userMap.values().stream().map(AdminUserRespDTO::getDeptId).distinct().toList();
                                Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(deptIds);
                                if (CollUtil.isNotEmpty(userMap)) {
                                    list.forEach(user -> {
                                        AdminUserRespDTO adminUserRespDTO = userMap.get(user.getBusinessSubjectCode());
                                        JsonAmount amount = user.getAmount();
                                        String amountString = amount == null ? "" : CommonDict.HYPHEN + amount.showAmount();
                                        if (Objects.nonNull(adminUserRespDTO) && CollUtil.isNotEmpty(deptMap) ) {
                                            nameList.add(feeShareTypeString + CommonDict.HYPHEN + "部门 " + adminUserRespDTO.getNickname() + "(" +deptMap.get(adminUserRespDTO.getDeptId()).getName() +")" + amountString);
                                        } else {
                                            nameList.add(feeShareTypeString + CommonDict.HYPHEN + "部门 " + user.getBusinessSubjectCode() + amountString);
                                        }
                                    });
                                }
                                desc = String.join(",", nameList);
                            }
                        }
                    }
                }
            }
            if(Objects.isNull(s.getOrderType())){
                desc = "暂不归集";
            }
            result.put( s.getId(),desc);
        });
        return result;
    }
    private Map<Long,List<FeeShareDescItemDTO>> GetFeeShareDescList(List<Long> feeShareIdList){
        if(CollUtil.isEmpty(feeShareIdList)){
            return null;
        }
        Map<Long,List<FeeShareDescItemDTO>> result = new HashMap<>();
        List<Long> idList = feeShareIdList.stream().distinct().toList();
        if(CollUtil.isEmpty(idList)){
            return result;
        }
        List<FeeShareDO> feeShareDOList = feeShareMapper.selectList(FeeShareDO::getId, idList);
        if(CollUtil.isEmpty(feeShareDOList)){
            return result;
        }
        Map<Long,List<FeeShareItemDO>> itemMap = feeShareItemService.getListByFeeShareIdList(idList);
        feeShareDOList.forEach(s->{
            List<FeeShareDescItemDTO> desc = new ArrayList<>();
            Integer feeShareType = s.getFeeShareType();
            String feeShareTypeString = DictFrameworkUtils.getDictDataLabel("fee_share_type", feeShareType);
            if(Objects.nonNull(feeShareType)){
                if(feeShareType.equals(OaFeeShareTypeEnum.OPERATION.getCode())){
                    String companyOperateType = DictFrameworkUtils.getDictDataLabel("company_operate_type", s.getRelationType());
                    desc.add(new FeeShareDescItemDTO(feeShareTypeString,companyOperateType,s.getAmount(),s.getDescId(),s.getDescName()));
                }
                if(feeShareType.equals(OaFeeShareTypeEnum.MARKET.getCode())){
                    String marketExpansionType = DictFrameworkUtils.getDictDataLabel("market_expansion_type", s.getRelationType());
                    desc.add(new FeeShareDescItemDTO(feeShareTypeString,marketExpansionType,s.getAmount(),s.getDescId(),s.getDescName()));
                }
                if(feeShareType.equals(OaFeeShareTypeEnum.PROJECT.getCode())){
                    String projectString = DictFrameworkUtils.getDictDataLabel("project_manage_type", s.getRelationType());
                    String typString = feeShareTypeString + CommonDict.HYPHEN  + projectString;
                    if(Objects.equals(s.getRelationType(), OaProjectTypeEnum.DEV.getCode())){
                        ProjectDTO dto = projectApi.getProjectDTOById(s.getProjectId());
                        desc.add(new FeeShareDescItemDTO(typString, dto.getName(),s.getAmount(),s.getDescId(),s.getDescName()));
                    }
                    else if(Objects.equals(s.getRelationType(), OaProjectTypeEnum.EXMS.getCode())){
                        ExhibitionDTO dto = exhibitionApi.getExhibitionById(s.getExhibitionId());
                        desc.add(new FeeShareDescItemDTO(typString,
                                dto.getExmsEventCategoryName() + CommonDict.HYPHEN + dto.getExhibitionName(),
                                s.getAmount(),s.getDescId(),s.getDescName()));
                    }else if(Objects.equals(s.getRelationType(), OaProjectTypeEnum.BRAND.getCode())){
                        String brandType = DictFrameworkUtils.getDictDataLabel("fee_brand_type", s.getBrandType());
                        desc.add(new FeeShareDescItemDTO(typString, brandType,s.getAmount(),s.getDescId(),s.getDescName()))  ;
                    }
                }
            }

            if(CollUtil.isNotEmpty(itemMap)){
                List<FeeShareItemDO> itemDOList = itemMap.get(s.getId());
                if(CollUtil.isNotEmpty(itemDOList)) {
                    //销售
                    if (Objects.equals(s.getOrderType(), OaOrderTypeEnum.SMS.getCode())) {
                        List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.SMS.getCode())).toList();
                        if (CollUtil.isNotEmpty(list)) {
                            desc = list.stream().map(itemDO->{
                                return new FeeShareDescItemDTO("销售订单",itemDO.getBusinessSubjectCode() ,itemDO.getAmount(),s.getDescId(),s.getDescName());
                            }).toList();
                        }
                    }
                    //采购
                    if (Objects.equals(s.getOrderType(), OaOrderTypeEnum.PURCHASE.getCode())) {
                        List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.PURCHASE.getCode())).toList();
                        if (CollUtil.isNotEmpty(list)) {
                            desc = list.stream().map(itemDO->{
                                return new FeeShareDescItemDTO("采购订单",itemDO.getBusinessSubjectCode() ,itemDO.getAmount(),s.getDescId(),s.getDescName());
                            }).toList();

                        }
                    }

                    if (Objects.equals(s.getOrderType(), OaOrderTypeEnum.OTHER.getCode())
                            &&  Objects.equals(s.getFeeShareType(),OaFeeShareTypeEnum.NOORDER.getCode()) ) {
                        //客户
                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.CUST.getCode() )){
                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.CUST.getCode())).toList();
                            if (CollUtil.isNotEmpty(list)) {
                                Map<String, CustAllDTO> custMap = custApi.getCustByCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
                                List<FeeShareDescItemDTO> nameList = new ArrayList<>();
                                if(CollUtil.isNotEmpty(custMap)){
                                    list.forEach(cus->{
                                        CustAllDTO custAllDTO = custMap.get(cus.getBusinessSubjectCode());
                                        JsonAmount amount = cus.getAmount();
                                        String descName = DictFrameworkUtils.getDictDataLabel("fee_share_cust_desc", cus.getDescId());
                                        long custAllDTOLong = 0;
                                        try{
                                            custAllDTOLong = custAllDTO.getDescId() == null ? 0 : (long)custAllDTO.getDescId();
                                        }
                                        catch (Exception ex){    }
                                        if(Objects.nonNull(custAllDTO)){
                                            nameList.add( new FeeShareDescItemDTO(feeShareTypeString + CommonDict.HYPHEN + "核心客户 " ,custAllDTO.getName(),amount,custAllDTOLong,descName));
                                        }
                                        else {
                                            nameList.add( new FeeShareDescItemDTO(feeShareTypeString + CommonDict.HYPHEN + "核心客户 " ,cus.getBusinessSubjectCode(),amount,custAllDTOLong,descName));
                                        }
                                    });
                                }
                                if(CollUtil.isNotEmpty(nameList)){
                                    desc.addAll(nameList);
                                }
                            }
                        }
                        //供应商
                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.VENDER.getCode() )){
                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.VENDEOR.getCode())).toList();
                            if(CollUtil.isNotEmpty(list)){
                                List<FeeShareDescItemDTO> nameList = new ArrayList<>();
                                Map<String, VenderAllDTO> venderMap = venderApi.getVenderByCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
                                if(CollUtil.isNotEmpty(venderMap)){
                                    list.forEach(vender->{
                                        VenderAllDTO venderAllDTO = venderMap.get(vender.getBusinessSubjectCode());
                                        JsonAmount amount = vender.getAmount();
                                        String descName = DictFrameworkUtils.getDictDataLabel("fee_share_vender_desc", vender.getDescId());
                                        long venderLong = 0;
                                        try{
                                            venderLong = vender.getDescId() == null ? 0 : (long)vender.getDescId();
                                        }
                                        catch (Exception ex){    }
                                        if(Objects.nonNull(venderAllDTO)){
                                            nameList.add( new FeeShareDescItemDTO(feeShareTypeString + CommonDict.HYPHEN + "核心供应商 " ,venderAllDTO.getName(),amount,venderLong,descName));
                                        }else {
                                            nameList.add( new FeeShareDescItemDTO(feeShareTypeString + CommonDict.HYPHEN + "核心供应商 " ,vender.getBusinessSubjectCode(),amount,venderLong,descName));
                                        }
                                    });
                                }
                                if(CollUtil.isNotEmpty(nameList)){
                                    desc.addAll(nameList);
                                }
                            }
                        }
                        //部门
//                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.DEPT.getCode() )){
//                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
//                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.DEPT.getCode())).toList();
//                            if (CollUtil.isNotEmpty(list)) {
//                                List<FeeShareDescItemDTO> nameList = new ArrayList<>();
//                                Map<String, DeptRespDTO> deptMap = deptApi.getDeptByCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
//                                if(CollUtil.isNotEmpty(deptMap)){
//                                    list.forEach(dept->{
//                                        DeptRespDTO deptRespDTO = deptMap.get(dept.getBusinessSubjectCode());
//                                        JsonAmount amount = dept.getAmount();
//                                        if(Objects.nonNull(deptRespDTO)){
//                                            nameList.add( new FeeShareDescItemDTO("部门 " ,deptRespDTO.getName(),amount,s.getDescId(),s.getDescName()));
//                                        }else {
//                                            nameList.add( new FeeShareDescItemDTO("部门 " ,dept.getBusinessSubjectCode(),amount,s.getDescId(),s.getDescName()));
//                                        }
//                                    });
//                                }
//                                if(CollUtil.isNotEmpty(nameList)){
//                                    desc.addAll(nameList);
//                                }
//                            }
//                        }
                        //个人->部门
                        if(Objects.equals(s.getRelationType(),OaNotOrderRelationTypeEnum.DEPT.getCode() )) {
                            List<FeeShareItemDO> list = itemDOList.stream().filter(i ->
                                    Objects.equals(i.getBusinessSubjectType(), OaFeeShareSourceType.USER.getCode())).toList();
                            if (CollUtil.isNotEmpty(list)) {
                                List<FeeShareDescItemDTO> nameList = new ArrayList<>();
                                Map<String, AdminUserRespDTO> userMap = adminUserApi.getUserListByUserCodeList(list.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList());
                                List<Long> deptIds = userMap.values().stream().map(AdminUserRespDTO::getDeptId).distinct().toList();
                                Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(deptIds);
                                if (CollUtil.isNotEmpty(userMap)) {
                                    list.forEach(user -> {
                                        AdminUserRespDTO adminUserRespDTO = userMap.get(user.getBusinessSubjectCode());
                                        JsonAmount amount = user.getAmount();
                                        String descName = DictFrameworkUtils.getDictDataLabel("fee_share_user_desc", user.getDescId());
                                        long userLong = 0;
                                      try{
                                           userLong = user.getDescId() == null ? 0 : (long)user.getDescId();
                                      }
                                      catch (Exception ignored){    }
                                        if (Objects.nonNull(adminUserRespDTO) && CollUtil.isNotEmpty(deptMap)) {
                                            nameList.add( new FeeShareDescItemDTO(feeShareTypeString + CommonDict.HYPHEN + "部门 " ,adminUserRespDTO.getNickname() + "（" + deptMap.get(adminUserRespDTO.getDeptId()).getName() + ")",amount, userLong,descName));
                                        } else {
                                            nameList.add( new FeeShareDescItemDTO(feeShareTypeString + CommonDict.HYPHEN + "部门 " ,user.getBusinessSubjectCode(),amount,userLong,descName));
                                        }
                                    });
                                }
                                if(CollUtil.isNotEmpty(nameList)){
                                    desc.addAll(nameList);
                                }
                            }
                        }
                    }
                }
            }
            if(Objects.isNull(s.getOrderType())){
                desc.add( new FeeShareDescItemDTO("暂不归集 " ,"",null,null,""));
            }
            result.put( s.getId(),desc);
        });
        return result;
    }

    @Override
    public void updateFeeShareByDTO(FeeShareReqDTO feeShare) {
        if (feeShare == null) {
            return;
        }
        Long oldId = feeShare.getId();
        List<FeeShareDO> feeShareDOList = feeShareMapper.selectList(
                new LambdaQueryWrapperX<FeeShareDO>()
                        .eq(FeeShareDO::getBusinessCode, feeShare.getBusinessCode())
                        .eq(FeeShareDO::getBusinessType,feeShare.getBusinessType())
                        .ne(FeeShareDO::getPreCollectionFlag,1)
        );
        if(CollUtil.isNotEmpty(feeShareDOList)){
            List<Long> idList = feeShareDOList.stream().map(FeeShareDO::getId).distinct().toList();
            feeShareMapper.deleteBatchIds(idList);
            feeShareItemService.deleteFeeShareItemByFeeShareIdList(idList);
        }

        FeeShareDO shareDO = FeeShareConvert.INSTANCE.convertFeeShareDOByDTO(feeShare);
        shareDO.setAuditStatus(feeShare.getAuditStatus());
        shareDO.setId(null);
        shareDO.setOrderType(feeShare.getOrderType());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        shareDO.setInputUser(adminUserApi.getUserDeptByUserId(loginUserId));
        shareDO.setPaymentStatus(PaymentStatusEnum.UNPAID.getValue());
        feeShareMapper.insert(shareDO);
        Long shareDOId = shareDO.getId();
        List<FeeShareItemDTO> children = new ArrayList<>();
        List<CustAllDTO> crmChildren = feeShare.getCrmChildren();
        if (CollUtil.isNotEmpty(crmChildren)) {
            crmChildren.forEach(s->{
                children.add(new FeeShareItemDTO().setShareFeeId(shareDOId)
                        .setId(null)
                        .setDescId(s.getDescId())
                        .setAmount(s.getAmount())
                        .setBusinessSubjectCode(s.getCode())
                        .setBusinessSubjectType(OaFeeShareSourceType.CUST.getCode())
                );
            });
        }
        List<VenderAllDTO> venderChildren = feeShare.getVenderChildren();
        if (CollUtil.isNotEmpty(venderChildren)) {
            venderChildren.forEach(s->{
                children.add(new FeeShareItemDTO().setShareFeeId(shareDOId)
                        .setId(null)
                        .setDescId(s.getDescId())
                        .setAmount(s.getAmount())
                        .setBusinessSubjectCode(s.getCode())
                        .setBusinessSubjectType(OaFeeShareSourceType.VENDEOR.getCode())
                );
            });
        }
        List<SmsContractAllDTO> smsChildren = feeShare.getSmsChildren();
        if (CollUtil.isNotEmpty(smsChildren)) {
            smsChildren.forEach(s->{
                FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
                        .setId(null)
                        .setAmount(s.getAmount())
                        .setBusinessSubjectCode(s.getCode())
                        .setBusinessSubjectType(OaFeeShareSourceType.SMS.getCode());
                children.add(feeShareItemDTO);
            });
        }
        List<PurchaseContractAllDTO> purchaseChildren = feeShare.getPurchaseChildren();
        if (CollUtil.isNotEmpty(purchaseChildren)) {
            purchaseChildren.forEach(s->{
                FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
                        .setId(null)
                        .setAmount(s.getAmount())
                        .setBusinessSubjectCode(s.getCode())
                        .setBusinessSubjectType(OaFeeShareSourceType.PURCHASE.getCode());
                children.add(feeShareItemDTO);
            });
        }

//        List<FeeShareDeptDTO> deptChildren = feeShare.getDeptChildren();
//        if (CollUtil.isNotEmpty(deptChildren)) {
//            deptChildren.forEach(s->{
//                FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
//                        .setId(null)
//                        .setAmount(s.getAmount())
//                        .setBusinessSubjectCode(s.getCode())
//                        .setBusinessSubjectType(OaFeeShareSourceType.DEPT.getCode());
//                children.add(feeShareItemDTO);
//            });
//        }

        List<FeeShareUserDTO> userChildren = feeShare.getUserChildren();
        if (CollUtil.isNotEmpty(userChildren)) {
            userChildren.forEach(s->{
                FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
                        .setId(null)
                        .setAmount(s.getAmount())
                        .setDescId(s.getDescId())
                        .setBusinessSubjectCode(s.getCode())
                        .setBusinessSubjectType(OaFeeShareSourceType.DEPT.getCode());
                children.add(feeShareItemDTO);
            });
        }
        if (CollUtil.isNotEmpty(children)) {
            feeShareItemService.createFeeShareItemBatch(children, shareDO.getId());
        }
    }

    @Override
    public List<FeeShareRespDTO> getFeeShareDTO(Integer type, String code,Boolean isPre) {
        List<FeeShareDO> shareDOList = feeShareMapper.selectList(new LambdaQueryWrapperX<FeeShareDO>()
                .eqIfPresent(FeeShareDO::getBusinessType, type)
                .eq(FeeShareDO::getPreCollectionFlag, isPre ? 1 : 0)
//                .eq(FeeShareDO::getStatus, BooleanEnum.YES.getValue())
                .eqIfPresent(FeeShareDO::getBusinessCode, code));
        if (CollUtil.isEmpty(shareDOList)) {
            return List.of();
        }
        List<FeeShareRespDTO> dtoList = BeanUtils.toBean(shareDOList, FeeShareRespDTO.class);
        dtoList.forEach(dto->{
            List<FeeShareItemDO> feeShareItemDOList = feeShareItemService.getFeeShareItemByShareId(dto.getId());
            if(CollUtil.isNotEmpty(feeShareItemDOList)){
                //客户
                List<FeeShareItemDO> crmList = feeShareItemDOList.stream().filter(s -> Objects.equals(s.getBusinessSubjectType(), OaFeeShareSourceType.CUST.getCode())).toList();
                if(CollUtil.isNotEmpty(crmList)){
                    List<String> codeList = crmList.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList();
                    Map<String, CustAllDTO> custMap = custApi.getCustByCodeList(codeList);
                    List<CustAllDTO> crmChildren = new ArrayList<>();
                    if(CollUtil.isNotEmpty(custMap)){
                        crmList.forEach(s->{
                            CustAllDTO custAllDTO = custMap.get(s.getBusinessSubjectCode());
                            if(Objects.nonNull(custAllDTO)){
                                custAllDTO.setAmount(s.getAmount());
                                custAllDTO.setDescId(s.getDescId());
                                crmChildren.add(custAllDTO);
                            }
                        });
                        dto.setCrmChildren(crmChildren);
                    }
                }
                //供应商
                List<FeeShareItemDO> venderList = feeShareItemDOList.stream().filter(s -> Objects.equals(s.getBusinessSubjectType(), OaFeeShareSourceType.VENDEOR.getCode())).toList();
                if(CollUtil.isNotEmpty(venderList)){
                    List<String> codeList = venderList.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList();
                    Map<String, VenderAllDTO> venderMap = venderApi.getVenderByCodeList(codeList);
                    List<VenderAllDTO> vendrChildren = new ArrayList<>();
                    if(CollUtil.isNotEmpty(venderMap)){
                        venderList.forEach(s->{
                            VenderAllDTO venderAllDTO = venderMap.get(s.getBusinessSubjectCode());
                            if(Objects.nonNull(venderAllDTO)){
                                venderAllDTO.setAmount(s.getAmount());
                                venderAllDTO.setDescId(s.getDescId());
                                vendrChildren.add(venderAllDTO);
                            }
                        });
                        dto.setVenderChildren(vendrChildren);
                    }
                }

                //销售
                List<FeeShareItemDO> smsList = feeShareItemDOList.stream().filter(s -> Objects.equals(s.getBusinessSubjectType(), OaFeeShareSourceType.SMS.getCode())).toList();
                if(CollUtil.isNotEmpty(smsList)){
                    List<String> codeList = smsList.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList();
                    Map<String, SmsContractAllDTO> smsMap = saleContractApi.getSmsByCodeList(codeList);
                    List<SmsContractAllDTO> smsChildren = new ArrayList<>();
                    if(CollUtil.isNotEmpty(smsMap)){
                        smsList.forEach(s->{
                            SmsContractAllDTO smsContractAllDTO = smsMap.get(s.getBusinessSubjectCode());
                            if(Objects.nonNull(smsContractAllDTO)){
                                smsContractAllDTO.setAmount(s.getAmount());
                                smsChildren.add(smsContractAllDTO);
                            }
                        });
                        dto.setSmsChildren(smsChildren);
                    }
                }

                //采购
                List<FeeShareItemDO> purchaseList = feeShareItemDOList.stream().filter(s -> Objects.equals(s.getBusinessSubjectType(), OaFeeShareSourceType.PURCHASE.getCode())).toList();
                if(CollUtil.isNotEmpty(purchaseList)){
                    List<String> codeList = purchaseList.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList();
                    Map<String, PurchaseContractAllDTO> purchaseMap = purchaseContractApi.getPurchaseByCodeList(codeList);
                    List<PurchaseContractAllDTO> purchaseChildren = new ArrayList<>();
                    if(CollUtil.isNotEmpty(purchaseMap)){
                        purchaseList.forEach(s->{
                            PurchaseContractAllDTO purchaseContractAllDTO = purchaseMap.get(s.getBusinessSubjectCode());
                            if(Objects.nonNull(purchaseContractAllDTO)){
                                purchaseContractAllDTO.setAmount(s.getAmount());
                                purchaseChildren.add(purchaseContractAllDTO);
                            }
                        });
                        dto.setPurchaseChildren(purchaseChildren);
                    }
                }

                //部门
//                List<FeeShareItemDO> deptList = feeShareItemDOList.stream().filter(s -> Objects.equals(s.getBusinessSubjectType(), OaFeeShareSourceType.DEPT.getCode())).toList();
//                if(CollUtil.isNotEmpty(deptList)){
//                    List<String> codeList = deptList.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList();
//                    Map<String, DeptRespDTO> deptMap = deptApi.getDeptByCodeList(codeList);
//                    List<FeeShareDeptDTO> deptChildren = new ArrayList<>();
//                    if(CollUtil.isNotEmpty(deptMap)){
//                        deptList.forEach(s->{
//                            DeptRespDTO deptRespDTO = deptMap.get(s.getBusinessSubjectCode());
//                            if(Objects.nonNull(deptRespDTO)){
//                                FeeShareDeptDTO deptDto = BeanUtils.toBean(deptRespDTO,FeeShareDeptDTO.class);
//                                deptDto.setAmount(s.getAmount());
//                                deptChildren.add(deptDto);
//                            }
//                        });
//                        dto.setDeptChildren(deptChildren);
//                    }
//                }

                //用户
                List<FeeShareItemDO> userList = feeShareItemDOList.stream().filter(s -> Objects.equals(s.getBusinessSubjectType(), OaFeeShareSourceType.USER.getCode())).toList();
                if(CollUtil.isNotEmpty(userList)){
                    List<String> codeList = userList.stream().map(FeeShareItemDO::getBusinessSubjectCode).distinct().toList();
                    Map<String, AdminUserRespDTO> userMap = adminUserApi.getUserListByUserCodeList(codeList);
                    List<FeeShareUserDTO> userChildren = new ArrayList<>();
                    if(CollUtil.isNotEmpty(userMap)){
                        userList.forEach(s->{
                            AdminUserRespDTO userRespDTO = userMap.get(s.getBusinessSubjectCode());
                            if(Objects.nonNull(userRespDTO)){
                                FeeShareUserDTO userDto = BeanUtils.toBean(userRespDTO,FeeShareUserDTO.class);
                                userDto.setAmount(s.getAmount());
                                userDto.setDescId(s.getDescId());
                                DeptRespDTO dept = deptApi.getDept(userDto.getDeptId());
                                if(Objects.nonNull(dept)){
                                    userDto.setDeptName(dept.getName());
                                }
                                userChildren.add(userDto);
                            }
                        });
                        dto.setUserChildren(userChildren);
                    }
                }
            }

            //特殊处理部人员部门数据
            if(Objects.equals(dto.getOrderType(), OaOrderTypeEnum.OTHER.getCode())
                    && Objects.equals(dto.getFeeShareType(),OaFeeShareTypeEnum.NOORDER.getCode())
//                    && Objects.equals(dto.getRelationType(),OaNotOrderRelationTypeEnum.USER.getCode())
            ){
                UserDept userDept = adminUserApi.getUserDeptByUserId(dto.getId());
                if(Objects.nonNull(userDept)){
                    dto.setDeptId(userDept.getDeptId()).setDeptName(userDept.getDeptName());
                }
            }

            Map<Long, String> longStringMap = GetFeeShareDesc(List.of(dto.getId()));
            if(CollUtil.isNotEmpty(longStringMap)){
                dto.setFeeShareDetail(longStringMap.get(dto.getId()));
            }

            Map<Long, List<FeeShareDescItemDTO>> descListMap = GetFeeShareDescList(List.of(dto.getId()));
            if(CollUtil.isNotEmpty(descListMap)){
                dto.setDetailList(descListMap.get(dto.getId()));
            }
        });

     return dtoList;
    }

    @Override
    public void approveTask(Long userId, BpmTaskApproveReqDTO reqVO) {
        bpmProcessInstanceApi.approveTask(userId, BeanUtils.toBean(reqVO, BpmTaskApproveReqDTO.class));
    }

    @Override
    public void rejectTask(Long userId, BpmTaskRejectReqDTO reqVO) {
        bpmProcessInstanceApi.rejectTask(userId, BeanUtils.toBean(reqVO, BpmTaskRejectReqDTO.class));
    }

    @Override
    public void submitTask(Long feeShareId, Long userId,String businessCode) {
        String processInstanceId = "";
        if(StringUtils.isWhitespace(businessCode)){
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY, feeShareId);
        }else {
            processInstanceId = bpmProcessInstanceApi.createProcessInstance(userId, PROCESS_DEFINITION_KEY,businessCode, feeShareId);
        }
        updateAuditStatus(businessCode, BpmProcessInstanceResultEnum.PROCESS.getResult(), processInstanceId);
    }

    @Override
    public String getProcessDefinitionKey() {
        return PROCESS_DEFINITION_KEY;
    }

    @Override
    public void updateAuditStatus( String businessKey, Integer auditStatus, String processInstanceId) {
        List<FeeShareDO> feeShareDOS = feeShareMapper.selectList(FeeShareDO::getBusinessCode, businessKey);
        if(CollUtil.isEmpty(feeShareDOS)){
            throw exception(FEE_SHARE_NOT_EXISTS);
        }
        feeShareDOS.forEach(s->{
            s.setAuditStatus(auditStatus);
            if (StrUtil.isNotEmpty(processInstanceId)) {
                s.setProcessInstanceId(processInstanceId);
            }
        });
        feeShareMapper.updateBatch(feeShareDOS);
    }

    @Override
    public void updateSourceFeeShareStatus(String businessKey ) {
        List<FeeShareDO> feeShareDOS = feeShareMapper.selectList(FeeShareDO::getBusinessCode, businessKey);

        if (Objects.isNull(feeShareDOS)) {
            throw exception(FEE_SHARE_NOT_EXISTS);
        }

        List<Integer> businessTypes = feeShareDOS.stream().map(FeeShareDO::getBusinessType).toList();
        if(CollUtil.isEmpty(businessTypes)){
            throw exception(FEE_SHARE_BUSINESS_TYPE_NONE);
        }
        if((long) businessTypes.stream().distinct().toList().size() > 1){
            throw exception(FEE_SHARE_BUSINESS_TYPE_MORE);
        }
        Integer businessType = businessTypes.get(0);
        switch (businessType) {
            // 更新报销归集状态
            case FeeShareBusinessTypeDict.TRAVEL_APP, FeeShareBusinessTypeDict.ENTERTAIN_APP, FeeShareBusinessTypeDict.GENERAL_APP -> {
                reimbMapper.updateById(new ReimbDO().setCode(businessKey).setAuxiliaryType(BooleanEnum.YES.getValue()));
            }
            case FeeShareBusinessTypeDict.PAYMENT_APP -> {
                paymentAppMapper.updateById(new PaymentAppDO().setCode(businessKey).setAuxiliaryType(BooleanEnum.YES.getValue()));
            }
            case FeeShareBusinessTypeDict.SEND_APP -> {
                emsSendApi.updateBelongFlagByCode(businessKey, BooleanEnum.YES.getValue());
            }
            case FeeShareBusinessTypeDict.FORWARDER_FEE -> {
                //自动归集不需要写入归集标记状态
//                forwarderFeeApi.updateBelongFlagByCode(businessKey , BooleanEnum.YES.getValue());
            }
            default -> {
                throw exception(UNKNOWN_FEE_SHARE_TYPE);
            }
        }
    }

    @Override
    public void updateSourceStatus(Integer businessType, Long BusinessId, Integer sourceStatus) {
        FeeShareDO feeShareDO =new FeeShareDO().setSourceStatus(sourceStatus);
        if (BpmProcessInstanceResultEnum.APPROVE.getResult().equals(sourceStatus)){
            feeShareDO.setAuditStatus(sourceStatus);
            feeShareDO.setStatus(BooleanEnum.YES.getValue());
        }
        feeShareMapper.update(feeShareDO, new LambdaQueryWrapperX<FeeShareDO>().eq(FeeShareDO::getBusinessType, businessType)
                .eq(FeeShareDO::getBusinessId, BusinessId));
    }
    public JsonAmount getFeeShareAmount(List<JsonAmount> amountList){
        JsonAmount feeShareAmount = new JsonAmount();
        if (CollUtil.isNotEmpty(amountList)){
            Map<String, BigDecimal> dailyRateMap = rateApi.getDailyRateMap();
            BigDecimal rmbAmount = amountList.stream().map(s -> {
                String currency = s.getCurrency();
                if (Objects.isNull(currency)) {
                    return BigDecimal.ZERO;
                }
                if (CommonCurrencyDict.RMB.equals(currency) || CommonCurrencyDict.CNY.equals(currency)) {
                    return s.getAmount();
                }
                BigDecimal rate = dailyRateMap.get(currency);
                if (Objects.isNull(rate) || rate.compareTo(BigDecimal.ZERO) == 0) {
                    throw exception(CURRENCY_RATE_NOT_EXISTS, currency);
                }
                return NumUtil.mul(rate, s.getAmount());
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            feeShareAmount.setAmount(rmbAmount).setCurrency(CommonCurrencyDict.RMB);
        }
        return feeShareAmount;
    }

    @Override
    public void updateStatus(String businessKey, Integer status) {
        List<FeeShareDO> feeShareDOS = feeShareMapper.selectList(FeeShareDO::getBusinessCode, businessKey);
        if(CollUtil.isEmpty(feeShareDOS)){
            throw exception(FEE_SHARE_NOT_EXISTS);
        }
        feeShareDOS.forEach(s->{
            s.setStatus(status);
        });
        feeShareMapper.updateBatch(feeShareDOS);
    }

    @Override
    public Boolean isUsedByDescId(FeeShareDescDO desc) {
        Optional<FeeShareDO> any = feeShareMapper.selectList(
                new LambdaQueryWrapperX<FeeShareDO>()
                        .eq(FeeShareDO::getFeeShareType, desc.getFeeShareType())
                        .eq(FeeShareDO::getRelationType, desc.getRelationType())
                        .eq(FeeShareDO::getDescId, desc.getId())
            ).stream().findAny();
        return any.isPresent();
    }

    @Override
    public Map<String, List<FeeShareRespDTO>> getFeeShareByCodeList(Integer value, List<String> codeList) {
        if(CollUtil.isEmpty(codeList)){
            return null;
        }
        List<FeeShareDO> feeShareDOList;
        if(value == 0){
            feeShareDOList = feeShareMapper.selectList(new LambdaQueryWrapperX<FeeShareDO>()
                    .in(FeeShareDO::getBusinessCode, codeList));
        }else {
            feeShareDOList = feeShareMapper.selectList(new LambdaQueryWrapperX<FeeShareDO>().eq(FeeShareDO::getBusinessType, value)
                    .in(FeeShareDO::getBusinessCode, codeList));
        }

        if(CollUtil.isEmpty(feeShareDOList)){
            return null;
        }
        List<Long> idList = feeShareDOList.stream().map(FeeShareDO::getId).toList();
        Map<Long, String> longStringMap = GetFeeShareDesc(idList);
        Map<Long, List<FeeShareDescItemDTO>> detailListMap = GetFeeShareDescList(idList);
        Map<Long, JsonAmount> amountMap = getTotalAmount(idList);
        List<FeeShareRespDTO> dtoList = BeanUtils.toBean(feeShareDOList, FeeShareRespDTO.class);

        Map<Long, List<FeeShareItemDO>> itemMap = feeShareItemService.getListByFeeShareIdList(idList);

        dtoList.forEach(s->{
            if(CollUtil.isNotEmpty(longStringMap)){
                s.setFeeShareDetail(longStringMap.get(s.getId()));
            }
            if(CollUtil.isNotEmpty(detailListMap)){
                s.setDetailList(detailListMap.get(s.getId()));
            }

            if(CollUtil.isNotEmpty(amountMap)){
                JsonAmount jsonAmount = amountMap.get(s.getId());
                s.setTotalAmount(jsonAmount);
            }

            List<FeeShareItemDO> shareItemDOS = itemMap.get(s.getId());
            if(CollUtil.isNotEmpty(shareItemDOS)){
                List<FeeShareItemDO> smsChildren = shareItemDOS.stream().filter(t -> Objects.equals(t.getBusinessSubjectType(), OaFeeShareSourceType.SMS.getCode())).toList();
                List<SmsContractAllDTO> children = new ArrayList<>();
                if(CollUtil.isNotEmpty(smsChildren)){
                    smsChildren.forEach(ch->{
                        children.add(new SmsContractAllDTO().setCode(ch.getBusinessSubjectCode()));
                    });
                    s.setSmsChildren(children);
                }
            }


        });

        return  dtoList.stream().collect(Collectors.groupingBy(FeeShareRespDTO::getBusinessCode));
    }

    private Map<Long,JsonAmount> getTotalAmount(List<Long> idList){
        if(CollUtil.isEmpty(idList)){
            return null;
        }
        Map<Long,JsonAmount> result = new HashMap<>();
        List<FeeShareDO> feeShareDOList = feeShareMapper.selectList(FeeShareDO::getId, idList);
        if(CollUtil.isEmpty(feeShareDOList)){
            throw exception(FEE_SHARE_NOT_EXISTS);
        }
        Map<Long, List<FeeShareItemDO>> feeShareItemMap = feeShareItemService.getListByFeeShareIdList(idList);
        idList.forEach(a->{
            if(CollUtil.isEmpty(feeShareItemMap)){   //所有费用归集没有子项金额取本身的金额
                Optional<FeeShareDO> first = feeShareDOList.stream().filter(s -> Objects.equals(s.getId(), a)).findFirst();
                first.ifPresent(feeShareDO -> result.put(a, feeShareDO.getAmount()));
            }
            else {
                List<FeeShareItemDO> itemDOList = feeShareItemMap.get(a);
                if(CollUtil.isEmpty(itemDOList)){   //该条费用归集没有子项金额取本身的金额
                    Optional<FeeShareDO> first = feeShareDOList.stream().filter(s -> Objects.equals(s.getId(), a)).findFirst();
                    first.ifPresent(feeShareDO -> result.put(a, feeShareDO.getAmount()));
                }else {   //有子项的情况子项金额求和
                    BigDecimal amount = itemDOList.stream().map(FeeShareItemDO::getAmount).filter(Objects::nonNull)
                            .map(JsonAmount::getAmount).filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    String currency = "";
                    Optional<String> first = itemDOList.stream().map(FeeShareItemDO::getAmount).filter(Objects::nonNull)
                            .map(JsonAmount::getCurrency).filter(Objects::nonNull).findFirst();
                    if(first.isPresent()){
                        currency = first.get();
                    }
                    result.put(a,new JsonAmount().setAmount(amount).setCurrency(currency));
                }
            }
        });
        return result;
    }

    @Override
    public void updatePaymentStatus(Integer businessType, String BusinessCode, Integer paymentStatus) {
        if (StrUtil.isEmpty(BusinessCode)){
            return;
        }
        FeeShareDO feeShareDO =new FeeShareDO().setPaymentStatus(paymentStatus);
        feeShareMapper.update(feeShareDO, new LambdaQueryWrapperX<FeeShareDO>()
                .eq(FeeShareDO::getBusinessCode, BusinessCode));
    }

    @Override
    public void updateFeeShareByDTOList(List<FeeShareReqDTO> list,boolean isPre) {
        if(CollUtil.isEmpty(list)){
            return;
        }
        Optional<FeeShareReqDTO> first = list.stream().findFirst();
        if(first.isEmpty()){
            return;
        }
        List<String> codeList = list.stream().map(FeeShareReqDTO::getBusinessCode).distinct().toList();
        List<FeeShareDO> feeShareDOList = feeShareMapper.selectList(
                new LambdaQueryWrapperX<FeeShareDO>()
                        .in(FeeShareDO::getBusinessCode, codeList)
                        .eq(FeeShareDO::getBusinessType,first.get().getBusinessType())
        );
        if(CollUtil.isNotEmpty(feeShareDOList)){
            List<Long> idList = feeShareDOList.stream().map(FeeShareDO::getId).distinct().toList();
            feeShareMapper.deleteBatchIds(idList);
            feeShareItemService.deleteFeeShareItemByFeeShareIdList(idList);
        }

        list.forEach(feeShare->{
            FeeShareDO shareDO = FeeShareConvert.INSTANCE.convertFeeShareDOByDTO(feeShare);
            //预归集标记赋值
            shareDO.setPreCollectionFlag(isPre ? 1 : 0);
            shareDO.setAuditStatus(feeShare.getAuditStatus());
            shareDO.setId(null);
            shareDO.setOrderType(feeShare.getOrderType());
            shareDO.setPaymentStatus(PaymentStatusEnum.UNPAID.getValue());
            feeShareMapper.insert(shareDO);
            Long shareDOId = shareDO.getId();
            List<FeeShareItemDTO> children = new ArrayList<>();
            List<CustAllDTO> crmChildren = feeShare.getCrmChildren();
            if (CollUtil.isNotEmpty(crmChildren)) {
                crmChildren.forEach(s->{
                    children.add(new FeeShareItemDTO().setShareFeeId(shareDOId)
                            .setId(null)
                            .setDescId(s.getDescId())
                            .setAmount(s.getAmount())
                            .setBusinessSubjectCode(s.getCode())
                            .setBusinessSubjectType(OaFeeShareSourceType.CUST.getCode())
                    );
                });
            }
            List<VenderAllDTO> venderChildren = feeShare.getVenderChildren();
            if (CollUtil.isNotEmpty(venderChildren)) {
                venderChildren.forEach(s->{
                    children.add(new FeeShareItemDTO().setShareFeeId(shareDOId)
                            .setId(null)
                            .setDescId(s.getDescId())
                            .setAmount(s.getAmount())
                            .setBusinessSubjectCode(s.getCode())
                            .setBusinessSubjectType(OaFeeShareSourceType.VENDEOR.getCode())
                    );
                });
            }
            List<SmsContractAllDTO> smsChildren = feeShare.getSmsChildren();
            if (CollUtil.isNotEmpty(smsChildren)) {
                smsChildren.forEach(s->{
                    FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
                            .setId(null)
                            .setAmount(s.getAmount())
                            .setBusinessSubjectCode(s.getCode())
                            .setBusinessSubjectType(OaFeeShareSourceType.SMS.getCode());
                    children.add(feeShareItemDTO);
                });
            }
            List<PurchaseContractAllDTO> purchaseChildren = feeShare.getPurchaseChildren();
            if (CollUtil.isNotEmpty(purchaseChildren)) {
                purchaseChildren.forEach(s->{
                    FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
                            .setId(null)
                            .setAmount(s.getAmount())
                            .setBusinessSubjectCode(s.getCode())
                            .setBusinessSubjectType(OaFeeShareSourceType.PURCHASE.getCode());
                    children.add(feeShareItemDTO);
                });
            }
//            List<FeeShareDeptDTO> deptChildren = feeShare.getDeptChildren();
//            if (CollUtil.isNotEmpty(deptChildren)) {
//                deptChildren.forEach(s->{
//                    FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
//                            .setId(null)
//                            .setAmount(s.getAmount())
//                            .setBusinessSubjectCode(s.getCode())
//                            .setBusinessSubjectType(OaFeeShareSourceType.DEPT.getCode());
//                    children.add(feeShareItemDTO);
//                });
//            }

            List<FeeShareUserDTO> userChildren = feeShare.getUserChildren();
            if (CollUtil.isNotEmpty(userChildren)) {
                userChildren.forEach(s->{
                    FeeShareItemDTO feeShareItemDTO = new FeeShareItemDTO().setShareFeeId(shareDOId)
                            .setId(null)
                            .setDescId(s.getDescId())
                            .setAmount(s.getAmount())
                            .setBusinessSubjectCode(s.getCode())
                            .setBusinessSubjectType(OaFeeShareSourceType.USER.getCode());
                    children.add(feeShareItemDTO);
                });
            }

            if (CollUtil.isNotEmpty(children)) {
                feeShareItemService.createFeeShareItemBatch(children, shareDOId);
            }
        });

    }

    @Override
    public void deleteByCodes(FeeShareSourceTypeEnum feeShareType, List<String> codes) {
        if (feeShareType == null || CollUtil.isEmpty(codes)) {
            return;
        }
        LambdaQueryWrapperX<FeeShareDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.in(FeeShareDO::getBusinessCode, codes)
                .eq(FeeShareDO::getBusinessType, feeShareType.getValue());

        feeShareMapper.delete(queryWrapper);
    }

    @Override
    public List<FeeShareRespDTO> batchGetDetails(Integer isPre,Integer type, List<String> codes) {
        if(CollUtil.isEmpty(codes)){
            return null;
        }
        Integer feeShareType = getFeeShareTypeByApplyType(type);
        List<FeeShareRespDTO> res = new ArrayList<>();
        codes.forEach(s->{
            List<FeeShareRespDTO> feeShares = getFeeShareDTO(feeShareType, s, isPre == 1);
            if(CollUtil.isNotEmpty(feeShares)){
                res.addAll(feeShares);
            }
        });
        return  res;
    }

    @Override
    public boolean validateCustExists(String custCode) {
        return feeShareItemService.validateCustExists(custCode);
    }

    @Override
    public void deleteById(Integer type, Long reimbId) {
        if (reimbId == null ) {
            return;
        }
        LambdaQueryWrapperX<FeeShareDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(FeeShareDO::getBusinessId, reimbId)
                .eq(FeeShareDO::getBusinessType, type);

        feeShareMapper.delete(queryWrapper);
    }

    private Integer getFeeShareTypeByApplyType(Integer type){
        Map<Integer, Integer> keymap = Map.of(
                ApplyTypeEnum.TRAVEL.getValue(), FeeShareSourceTypeEnum.TRAVEL_APPLY.getValue(),
                ApplyTypeEnum.GENERAL.getValue(), FeeShareSourceTypeEnum.GENERAL_APPLY.getValue(),
                ApplyTypeEnum.ENTERTAIN.getValue(), FeeShareSourceTypeEnum.ENTERTAIN_APPLY.getValue());
        return keymap.get(type);
    }

}