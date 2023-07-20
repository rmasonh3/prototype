package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EstimateDesignTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EstimateDesign.class);
        EstimateDesign estimateDesign1 = new EstimateDesign();
        estimateDesign1.setId(1L);
        EstimateDesign estimateDesign2 = new EstimateDesign();
        estimateDesign2.setId(estimateDesign1.getId());
        assertThat(estimateDesign1).isEqualTo(estimateDesign2);
        estimateDesign2.setId(2L);
        assertThat(estimateDesign1).isNotEqualTo(estimateDesign2);
        estimateDesign1.setId(null);
        assertThat(estimateDesign1).isNotEqualTo(estimateDesign2);
    }
}
