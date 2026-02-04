package cn.iocoder.yudao.module.bpm.api.definition.dto;

import com.syj.eplus.framework.common.entity.UserDept;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/09/13/17:38
 * @Description:
 */
@Data
public class BpmNodeUser {

    private UserDept user;

    private Integer approveFlag;
}
