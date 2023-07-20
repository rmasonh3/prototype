package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CostingPercentagesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CostingPercentages.class);
        CostingPercentages costingPercentages1 = new CostingPercentages();
        costingPercentages1.setId(1L);
        CostingPercentages costingPercentages2 = new CostingPercentages();
        costingPercentages2.setId(costingPercentages1.getId());
        assertThat(costingPercentages1).isEqualTo(costingPercentages2);
        costingPercentages2.setId(2L);
        assertThat(costingPercentages1).isNotEqualTo(costingPercentages2);
        costingPercentages1.setId(null);
        assertThat(costingPercentages1).isNotEqualTo(costingPercentages2);
    }
}
