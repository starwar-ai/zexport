package com.syj.eplus.framework.common.entity;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导出上下文工具类
 * 用于统一管理导出场景的判断逻辑，避免各模块重复编写
 *
 * @author 波波
 */
public class ExportContext {

    /**
     * 导出模式枚举
     */
    @Getter
    @AllArgsConstructor
    public enum ExportMode {
        DOCUMENT(1, "单据维度", false),  // 单据导出，不需要子表
        PRODUCT(2, "产品维度", true);    // 产品导出，需要子表

        private final Integer code;
        private final String name;
        private final boolean needChildren;

        public static ExportMode getByCode(Integer code) {
            if (code == null) return DOCUMENT;
            for (ExportMode mode : values()) {
                if (mode.code.equals(code)) return mode;
            }
            return DOCUMENT;
        }
    }

    /**
     * 判断是否为导出场景
     * @param pageParam 分页参数
     * @return true-导出场景
     */
    public static boolean isExport(PageParam pageParam) {
        return pageParam != null && PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize());
    }

    /**
     * 判断导出时是否需要加载子表数据
     * @param pageParam 分页参数（需要实现 ExportPageParam 接口）
     * @return true-需要加载子表
     */
    public static boolean needChildren(PageParam pageParam) {
        if (!isExport(pageParam)) {
            return true; // 非导出场景，默认需要子表
        }
        if (pageParam instanceof ExportPageParam exportParam) {
            // 显式指定了 needChildren
            if (exportParam.getNeedChildren() != null) {
                return exportParam.getNeedChildren();
            }
            // 根据 isTree 判断（兼容现有逻辑）
            if (exportParam.getIsTree() != null) {
                return exportParam.getIsTree();
            }
        }
        return false; // 导出场景默认不需要子表
    }

    /**
     * 导出分页参数接口
     * 实现此接口的 PageReqVO 可以使用导出上下文功能
     */
    public interface ExportPageParam {
        /**
         * 是否树形结构（兼容现有逻辑）
         * @return true-树形结构/产品维度，需要子表数据
         */
        Boolean getIsTree();
        
        /**
         * 导出时是否需要子表数据
         * @return true-需要子表数据
         */
        Boolean getNeedChildren();
    }
}
