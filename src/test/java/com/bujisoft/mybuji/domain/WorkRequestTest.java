package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkRequest.class);
        WorkRequest workRequest1 = new WorkRequest();
        workRequest1.setId(1L);
        WorkRequest workRequest2 = new WorkRequest();
        workRequest2.setId(workRequest1.getId());
        assertThat(workRequest1).isEqualTo(workRequest2);
        workRequest2.setId(2L);
        assertThat(workRequest1).isNotEqualTo(workRequest2);
        workRequest1.setId(null);
        assertThat(workRequest1).isNotEqualTo(workRequest2);
    }
}
