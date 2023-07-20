package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScopeDesignTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScopeDesign.class);
        ScopeDesign scopeDesign1 = new ScopeDesign();
        scopeDesign1.setId(1L);
        ScopeDesign scopeDesign2 = new ScopeDesign();
        scopeDesign2.setId(scopeDesign1.getId());
        assertThat(scopeDesign1).isEqualTo(scopeDesign2);
        scopeDesign2.setId(2L);
        assertThat(scopeDesign1).isNotEqualTo(scopeDesign2);
        scopeDesign1.setId(null);
        assertThat(scopeDesign1).isNotEqualTo(scopeDesign2);
    }
}
