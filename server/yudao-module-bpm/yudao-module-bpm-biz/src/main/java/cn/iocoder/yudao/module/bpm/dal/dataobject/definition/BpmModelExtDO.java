package cn.iocoder.yudao.module.bpm.dal.dataobject.definition;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: du
 * @Date: 2024/08/27/9:20
 * @Description:
 */
@TableName(value = "bpm_model_ext", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BpmModelExtDO extends BaseDO {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 模型定义key
     */
    private String modelKey;

    /**
     * 是否交给上级领导审批
     */
    private Integer transferFlag;
}
