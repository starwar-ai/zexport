import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import com.syj.eplus.module.pms.service.category.CategoryService;
import com.syj.eplus.module.pms.service.spu.SpuServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

/**
 * @Description：
 * @Author：du
 * @Date：2024/2/6 18:05
 */
@Import({SpuServiceImpl.class})
public class CategoryTest extends BaseDbUnitTest {
    @Resource
    CategoryService categoryService;


    @Test
    public void test() {
        String profixCode = categoryService.getProfixCode(3L);
        System.out.println(profixCode);
    }


}
