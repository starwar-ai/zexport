import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import com.syj.eplus.module.pms.controller.admin.hsdata.vo.HsdataSimpleRespVO;
import com.syj.eplus.module.pms.service.hsdata.HsdataService;
import com.syj.eplus.module.pms.service.hsdata.HsdataServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 14:21
 */
@Import({HsdataServiceImpl.class})
public class HsdataTest extends BaseDbUnitTest {
    @Resource
    HsdataService hsdataService;

    @Test
    public void test() {
        List<HsdataSimpleRespVO> hsdataSimpleList = hsdataService.getHsdataSimpleList("");
        System.out.println(hsdataSimpleList);
    }

}