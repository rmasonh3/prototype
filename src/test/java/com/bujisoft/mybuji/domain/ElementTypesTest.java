package com.bujisoft.mybuji.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.bujisoft.mybuji.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ElementTypesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ElementTypes.class);
        ElementTypes elementTypes1 = new ElementTypes();
        elementTypes1.setId(1L);
        ElementTypes elementTypes2 = new ElementTypes();
        elementTypes2.setId(elementTypes1.getId());
        assertThat(elementTypes1).isEqualTo(elementTypes2);
        elementTypes2.setId(2L);
        assertThat(elementTypes1).isNotEqualTo(elementTypes2);
        elementTypes1.setId(null);
        assertThat(elementTypes1).isNotEqualTo(elementTypes2);
    }
}
