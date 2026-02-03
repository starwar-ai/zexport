package com.syj.eplus.module.home.service.invoiceholder;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.syj.eplus.framework.common.dict.CalculationDict;
import com.syj.eplus.framework.common.entity.UserDept;
import com.syj.eplus.framework.common.enums.ReimbStatusEnum;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderPageReqVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderRespVO;
import com.syj.eplus.module.home.controller.admin.invoiceholder.vo.InvoiceHolderSaveReqVO;
import com.syj.eplus.module.home.convert.invoiceholder.InvoiceHolderConvert;
import com.syj.eplus.module.home.dal.dataobject.invoiceholder.InvoiceHolderDO;
import com.syj.eplus.module.home.dal.mysql.invoiceholder.InvoiceHolderMapper;
import com.syj.eplus.module.oa.api.DictSubjectApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.dms.enums.ErrorCodeConstants.INVOICE_CODE_EXITED;
import static com.syj.eplus.module.home.ErrorCodeConstants.INVOICE_HOLDER_NOT_EXISTS;

/**
 * 发票夹 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class InvoiceHolderServiceImpl implements InvoiceHolderService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private InvoiceHolderMapper invoiceHolderMapper;
    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private DictSubjectApi dictSubjectApi;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInvoiceHolder(InvoiceHolderSaveReqVO createReqVO) {
        //校验发票号，发票号不可重复
        if (StringUtils.isNotEmpty(createReqVO.getInvoiceCode())) {
            createReqVO.setInvoiceCode(createReqVO.getInvoiceCode().trim());
            LambdaQueryWrapper<InvoiceHolderDO> queryWrapperX = new LambdaQueryWrapper<InvoiceHolderDO>().eq(InvoiceHolderDO::getInvoiceCode, createReqVO.getInvoiceCode());
            Long count = invoiceHolderMapper.selectCount(queryWrapperX);
            // 自动生成的判断是否有一个发票号即可
            if (count > CalculationDict.ZERO) {
                throw exception(INVOICE_CODE_EXITED);
            }
        }
        InvoiceHolderDO invoiceHolder = InvoiceHolderConvert.INSTANCE.convertInvoiceHolderDO(createReqVO);
        invoiceHolder.setStatus(ReimbStatusEnum.NOT_REIMBURSED.getValue());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        UserDept inputUser = adminUserApi.getUserDeptByUserId(loginUserId);
        invoiceHolder.setInputUser(inputUser);
        // 插入
        invoiceHolderMapper.insert(invoiceHolder);
        // 返回
        return invoiceHolder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInvoiceHolder(InvoiceHolderSaveReqVO updateReqVO) {
        // 校验存在
        validateInvoiceHolderExists(updateReqVO.getId());
        //校验发票号，发票号不可重复
        if (StringUtils.isNotEmpty(updateReqVO.getInvoiceCode())) {
            updateReqVO.setInvoiceCode(updateReqVO.getInvoiceCode().trim());
            LambdaQueryWrapper<InvoiceHolderDO> queryWrapperX = new LambdaQueryWrapper<InvoiceHolderDO>().
                    eq(InvoiceHolderDO::getInvoiceCode, updateReqVO.getInvoiceCode()).ne(InvoiceHolderDO::getId, updateReqVO.getId());
            Long count = invoiceHolderMapper.selectCount(queryWrapperX);
            if (count > CalculationDict.ZERO) {
                throw exception(INVOICE_CODE_EXITED);
            }
        }
        // 更新
        InvoiceHolderDO updateObj = InvoiceHolderConvert.INSTANCE.convertInvoiceHolderDO(updateReqVO);
        invoiceHolderMapper.updateById(updateObj);
    }

    @Override
    public void deleteInvoiceHolder(Long id) {
        // 校验存在
        validateInvoiceHolderExists(id);
        // 删除
        invoiceHolderMapper.deleteById(id);
    }

    private void validateInvoiceHolderExists(Long id) {
        if (invoiceHolderMapper.selectById(id) == null) {
            throw exception(INVOICE_HOLDER_NOT_EXISTS);
        }
    }

    @Override
    public InvoiceHolderRespVO getInvoiceHolder(Long id) {
        InvoiceHolderDO invoiceHolderDO = invoiceHolderMapper.selectById(id);
        if (invoiceHolderDO == null) {
            return null;
        }
        Map<Long, String> dictSubjectMap = dictSubjectApi.getDictSubjectMap();
        return InvoiceHolderConvert.INSTANCE.convertInvoiceHolderRespVO(invoiceHolderDO,dictSubjectMap);
    }

    @Override
    public PageResult<InvoiceHolderRespVO> getInvoiceHolderPage(InvoiceHolderPageReqVO pageReqVO) {
        PageResult<InvoiceHolderDO> pageResult = invoiceHolderMapper.selectPage(pageReqVO);
        List<InvoiceHolderDO> list = pageResult.getList();
        if (CollUtil.isEmpty(list)){
            return PageResult.empty();
        }
        Map<Long, String> dictSubjectMap = dictSubjectApi.getDictSubjectMap();
        List<InvoiceHolderRespVO> respVOList = list.stream().map(s -> InvoiceHolderConvert.INSTANCE.convert(s, dictSubjectMap)).toList();
        return new PageResult<InvoiceHolderRespVO>().setList(respVOList).setTotal(pageResult.getTotal());
    }

    @Override
    public void updateInveiceHolderStatus(List<Long> idList, Integer status) {
        if (CollUtil.isEmpty(idList)){
            return;
        }
        List<InvoiceHolderDO> list = idList.stream().map(s -> new InvoiceHolderDO().setId(s).setStatus(status)).toList();
        invoiceHolderMapper.updateBatch(list);
    }

}