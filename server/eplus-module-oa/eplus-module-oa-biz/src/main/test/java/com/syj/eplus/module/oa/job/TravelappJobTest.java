package com.syj.eplus.module.oa.job;

import com.syj.eplus.module.oa.job.job.TravelappJob;
import com.syj.eplus.module.oa.service.travelapp.TravelAppService;
import com.syj.eplus.module.wechat.service.WechatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TravelappJobTest {

    @Mock
    private WechatService mockWechatService;
    @Mock
    private TravelAppService mockTravelAppService;

    @InjectMocks
    private TravelappJob travelappJobUnderTest;

    @Test
    void testExecute() throws Exception {
        assertThat(travelappJobUnderTest.execute("param")).isNull();
    }
}
