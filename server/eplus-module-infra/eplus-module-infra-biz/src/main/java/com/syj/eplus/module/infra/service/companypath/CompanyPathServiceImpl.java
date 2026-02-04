package com.syj.eplus.module.infra.service.companypath;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.syj.eplus.module.infra.api.company.CompanyApi;
import com.syj.eplus.module.infra.api.companypath.dto.CompanyPathDTO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathPageReqVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathRespVO;
import com.syj.eplus.module.infra.controller.admin.companypath.vo.CompanyPathSaveReqVO;
import com.syj.eplus.module.infra.convert.companypath.CompanyPathConvert;
import com.syj.eplus.module.infra.dal.dataobject.companypath.CompanyPathDO;
import com.syj.eplus.module.infra.dal.mysql.companypath.CompanyPathMapper;
import com.syj.eplus.framework.common.entity.JsonCompanyPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.PATH_NOT_EXISTS;
import static com.syj.eplus.module.infra.enums.ErrorCodeConstants.PATH_PARENT_NOT_EXISTS;


/**
 * 抛砖路径 Service 实现类
 *
 * @author du
 */
@Service
@Validated
public class CompanyPathServiceImpl implements CompanyPathService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private CompanyPathMapper pathMapper;

    @Resource
    private CompanyApi companyApi;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPath(CompanyPathSaveReqVO createReqVO) {
        CompanyPathDO path = CompanyPathConvert.INSTANCE.convertCompanyPathDO(createReqVO);
        List<JsonCompanyPath> companyPathList = createReqVO.getPath();
        if (CollUtil.isNotEmpty(companyPathList)) {
            List<Long> companyIdList = companyPathList.stream().map(JsonCompanyPath::getId).toList();
            companyApi.checkCompanyCustAndVender(companyIdList);
            JsonCompanyPath companyPath = linkNodeByLevel(companyPathList);
            path.setPath(companyPath);
        }
        // 插入
        pathMapper.insert(path);
        // 返回
        return path.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePath(CompanyPathSaveReqVO updateReqVO) {
        // 校验存在
        validatePathExists(updateReqVO.getId());
        // 更新
        CompanyPathDO updateObj = CompanyPathConvert.INSTANCE.convertCompanyPathDO(updateReqVO);
        List<JsonCompanyPath> companyPathList = updateReqVO.getPath();
        if (CollUtil.isNotEmpty(companyPathList)) {
            List<Long> companyIdList = companyPathList.stream().map(JsonCompanyPath::getId).toList();
            companyApi.checkCompanyCustAndVender(companyIdList);
            JsonCompanyPath companyPath = linkNodeByLevel(companyPathList);
            updateObj.setPath(companyPath);
        }
        pathMapper.updateById(updateObj);
    }

    @Override
    public void deletePath(Long id) {
        // 校验存在
        validatePathExists(id);
        // 删除
        pathMapper.deleteById(id);
    }

    private void validatePathExists(Long id) {
        if (pathMapper.selectById(id) == null) {
            throw exception(PATH_NOT_EXISTS);
        }
    }

    @Override
    public CompanyPathRespVO getPath(Long id) {
        CompanyPathDO pathDO = pathMapper.selectById(id);
        if (pathDO == null) {
            return null;
        }
        return CompanyPathConvert.INSTANCE.convertCompanyPathRespVO(pathDO);
    }

    @Override
    public PageResult<CompanyPathDO> getPathPage(CompanyPathPageReqVO pageReqVO) {
        return pathMapper.selectPage(pageReqVO);
    }

    @Override
    public Map<Long, CompanyPathDTO> getCompanyPathMap(List<Long> idList) {
        List<CompanyPathDO> companyPathDOList = pathMapper.selectBatchIds(idList);
        if (CollUtil.isEmpty(companyPathDOList)) {
            return null;
        }
        return companyPathDOList.stream().collect(
            Collectors.toMap(
                CompanyPathDO::getId,
                pathDO -> CompanyPathConvert.INSTANCE.convertToDTO(pathDO.getPath())
            )
        );
    }

    /**
     * 根据层级挂载CompanyPath对象，构建层级结构。
     *
     * @param companyPathList CompanyPath列表
     * @return 构建好的层级结构的根节点
     */
    public JsonCompanyPath linkNodeByLevel(List<JsonCompanyPath> companyPathList) {
        if (companyPathList == null || companyPathList.isEmpty()) {
            return null;
        }
        // 按层级升序排序
        companyPathList.sort(Comparator.comparingInt(JsonCompanyPath::getLevel));
        // 初始化根节点（层级最小的节点）
        JsonCompanyPath root = companyPathList.get(0);
        // 从第二层开始遍历，挂载到正确的父节点下
        for (int i = 1; i < companyPathList.size(); i++) {
            JsonCompanyPath currentNode = companyPathList.get(i);
            int currentLevel = currentNode.getLevel();
            // 寻找当前节点的父节点
            JsonCompanyPath parent = findParentNode(companyPathList.subList(0, i), currentLevel - 1);
            if (Objects.nonNull(parent)) {
                parent.setNext(currentNode);
            } else {
                throw exception(PATH_PARENT_NOT_EXISTS, currentLevel - 1, currentNode.getName());
            }
        }
        return root;
    }

    /**
     * 在给定的节点列表中查找指定层级的节点(默认已经排序)
     *
     * @param nodes       节点列表
     * @param targetLevel 目标层级
     * @return 目标层级的节点，未找到返回null
     */
    private JsonCompanyPath findParentNode(List<JsonCompanyPath> nodes, int targetLevel) {
        for (JsonCompanyPath node : nodes) {
            if (node.getLevel() == targetLevel) {
                return node;
            }
        }
        return null;
    }
}
