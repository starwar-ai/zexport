import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import com.syj.eplus.module.pms.controller.admin.brand.vo.BrandSimpleRespVO;
import com.syj.eplus.module.pms.service.brand.BrandService;
import com.syj.eplus.module.pms.service.brand.BrandServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/7 15:06
 */

@Import({BrandServiceImpl.class})
public class BrandTest extends BaseDbUnitTest {
    @Resource
    BrandService brandService;

    @Test
    public void test() {
        List<BrandSimpleRespVO> brandSimpleList = brandService.getBrandSimpleList();
        System.out.println(brandSimpleList);
    }
}