import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import com.syj.eplus.module.infra.api.sn.SnApi;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.syj.eplus.module.oa.service.loanapp.LoanAppService.SN_TYPE;

public class LoanAppTest extends BaseDbUnitTest {

    @MockBean
    private SnApi snApi;

    @Test
    public void testAddRuleAndBuild() {

    }
}