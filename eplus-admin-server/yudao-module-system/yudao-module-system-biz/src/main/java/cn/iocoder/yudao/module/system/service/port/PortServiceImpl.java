package cn.iocoder.yudao.module.system.service.port;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.syj.eplus.module.infra.api.countryinfo.dto.CountryInfoDTO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortRespVO;
import cn.iocoder.yudao.module.system.controller.admin.port.vo.PortSaveReqVO;
import cn.iocoder.yudao.module.system.convert.port.PortConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.port.PortDO;
import cn.iocoder.yudao.module.system.dal.mysql.port.PortMapper;
import com.syj.eplus.module.infra.api.countryinfo.CountryInfoApi;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.module.infra.api.CodeGeneratorApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.PORT_NOT_EXISTS;

/**
 * 口岸 Service 实现类
 *
 * @author ePlus
 */
@Service
@Validated
public class PortServiceImpl implements PortService {

    public static final String SN_TYPE = "SN_PORT";
    private static final String CODE_PREFIX = "PO";
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private CodeGeneratorApi codeGeneratorApi;
    @Resource
    private PortMapper portMapper;
    @Resource
    private CountryInfoApi countryInfoApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPort(PortSaveReqVO createReqVO) {
        String codeGenerator = codeGeneratorApi.getCodeGenerator(SN_TYPE, CODE_PREFIX);
        createReqVO.setCode(codeGenerator);
        PortDO port = PortConvert.INSTANCE.convertPortDO(createReqVO);

        // 插入
        portMapper.insert(port);

        // 返回
        return port.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePort(PortSaveReqVO updateReqVO) {
        // 校验存在
        PortDO portDO = validatePortExists(updateReqVO.getId());
        // 更新
        PortDO updateObj = PortConvert.INSTANCE.convertPortDO(updateReqVO);

        portMapper.updateById(updateObj);
    }

    @Override
    public void deletePort(Long id) {
        // 校验存在
        validatePortExists(id);
        // 删除
        portMapper.deleteById(id);
    }

    @Override
    public PortDO getPort(Long id) {
        return portMapper.selectById(id);
    }

    private PortDO validatePortExists(Long id) {
        PortDO portDO = portMapper.selectById(id);
        if (portDO == null) {
            throw exception(PORT_NOT_EXISTS);
        }
        return portDO;
    }
    @Override
    public PageResult<PortRespVO> getPortPage(PortPageReqVO pageReqVO) {
        PageResult<PortDO> portDOPageResult = portMapper.selectPage(pageReqVO);
        Map<Long, CountryInfoDTO> countryInfoDTOMap = countryInfoApi.getCountryInfoMap();
        List<PortDO> list = portDOPageResult.getList();
        if (CollUtil.isNotEmpty(list)) {
            List<PortRespVO> portRespVOS = BeanUtils.toBean(list, PortRespVO.class);
            portRespVOS.forEach(item -> {
                if (CollUtil.isNotEmpty(countryInfoDTOMap)) {
                    CountryInfoDTO countryInfoDTO = countryInfoDTOMap.get(item.getCountryId());
                    if (Objects.nonNull(countryInfoDTO)) {
                        item.setCountryName(countryInfoDTO.getName());
                    }
                }
            });
            return new PageResult<PortRespVO>().setList(portRespVOS).setTotal(portDOPageResult.getTotal());
        }
        return PageResult.empty();
    }

    @Override
    public Boolean topPort(Long id) {
        PortDO portDO = validatePortExists(id);
        portDO.setTopFlag(BooleanEnum.YES.getValue());
        int i = portMapper.updateById(portDO);
        return i > 0;
    }


    @Override
    public Boolean rollbackTopPort(Long id) {
        PortDO portDO = validatePortExists(id);
        portDO.setTopFlag(BooleanEnum.NO.getValue());
        int i = portMapper.updateById(portDO);
        return i > 0;
    }



}