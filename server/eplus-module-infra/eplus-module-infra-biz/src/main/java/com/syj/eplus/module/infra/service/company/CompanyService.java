package com.syj.eplus.module.infra.service.company;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.company.dto.SimpleCompanyDTO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyBankRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyPageReqVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanyRespVO;
import com.syj.eplus.module.infra.controller.admin.company.vo.CompanySaveReqVO;
import com.syj.eplus.module.infra.dal.dataobject.company.CompanyDO;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.Valid;
import java.util.List;

/**
 * 内部法人单位 Service 接口
 *
 * @author du
 */
public interface CompanyService extends IService<CompanyDO> {

    /**
     * 创建内部法人单位
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCompany(@Valid CompanySaveReqVO createReqVO);

    /**
     * 更新内部法人单位
     *
     * @param updateReqVO 更新信息
     */
    void updateCompany(@Valid CompanySaveReqVO updateReqVO);

    /**
     * 删除内部法人单位
     *
     * @param id 编号
     */
    void deleteCompany(Long id);

    /**
     * 获得内部法人单位
     *
     * @param id 编号
     * @return 内部法人单位
     */
    CompanyRespVO getCompany(Long id);

    /**
     * 获得内部法人单位分页
     *
     * @param pageReqVO 分页查询
     * @return 内部法人单位分页
     */
    PageResult<CompanyDO> getCompanyPage(CompanyPageReqVO pageReqVO);


    /**
     * 获取法人单位精简列表
     *
     * @return 法人单位精简列表
     */
    List<SimpleCompanyDTO> getSimpleCompanyDTO();


    /**
     * 获取仅包含工厂法人单位精简列表
     *
     * @return 仅包含工厂法人单位精简列表
     */
    List<SimpleCompanyDTO> getProcessCompanyDTO();


    /**
     * 获取不包含工厂法人单位精简列表
     *
     * @return 仅包含工厂法人单位精简列表
     */
    List<SimpleCompanyDTO> getUnProcessCompanyDTO();

    /**
     * 获取法人单位精简列表
     *
     * @return 法人单位精简列表
     */
    List<SimpleCompanyDTO> getStringSimpleCompanyDTO(List<String> idList);

    /**
     * 获取法人单位列表
     *
     * @return 法人单位列表
     */
    List<CompanyRespVO> getCompanyList(List<Integer> notContainsNatureList,List<Integer> containsNatureList);

    /**
     * 启用
     *
     * @param companyId 公司主键
     */
    void enableCompany(Long companyId);

    /**
     * 停用
     *
     * @param companyId 公司主键
     */
    void disableCompany(Long companyId);

    SimpleCompanyDTO getCompanyDTO(Long id);

    List<CompanyBankRespVO> getCompanyBankList();

    List<SimpleCompanyDTO> getAllList();
}
