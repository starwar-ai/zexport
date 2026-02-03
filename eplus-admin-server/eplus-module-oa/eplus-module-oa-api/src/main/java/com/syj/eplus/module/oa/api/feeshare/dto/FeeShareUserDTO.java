package com.syj.eplus.module.oa.api.feeshare.dto;

import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.syj.eplus.framework.common.entity.JsonAmount;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author zhangcm
 */
@Data
@Accessors(chain = true)
public class FeeShareUserDTO extends AdminUserRespDTO {

    /**
     * 费用归属金额
     */
    private JsonAmount amount;

    private Integer descId;
    private String deptName;

}
