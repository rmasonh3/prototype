package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EstimateBasisTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EstimateBasis.class);
        EstimateBasis estimateBasis1 = new EstimateBasis();
        estimateBasis1.setId(1L);
        EstimateBasis estimateBasis2 = new EstimateBasis();
        estimateBasis2.setId(estimateBasis1.getId());
        assertThat(estimateBasis1).isEqualTo(estimateBasis2);
        estimateBasis2.setId(2L);
        assertThat(estimateBasis1).isNotEqualTo(estimateBasis2);
        estimateBasis1.setId(null);
        assertThat(estimateBasis1).isNotEqualTo(estimateBasis2);
    }
}
