package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkInfoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkInfo.class);
        WorkInfo workInfo1 = new WorkInfo();
        workInfo1.setId(1L);
        WorkInfo workInfo2 = new WorkInfo();
        workInfo2.setId(workInfo1.getId());
        assertThat(workInfo1).isEqualTo(workInfo2);
        workInfo2.setId(2L);
        assertThat(workInfo1).isNotEqualTo(workInfo2);
        workInfo1.setId(null);
        assertThat(workInfo1).isNotEqualTo(workInfo2);
    }
}
