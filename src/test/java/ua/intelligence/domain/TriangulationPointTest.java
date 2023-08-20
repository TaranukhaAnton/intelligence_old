package ua.intelligence.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ua.intelligence.web.rest.TestUtil;

class TriangulationPointTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TriangulationPoint.class);
        TriangulationPoint triangulationPoint1 = new TriangulationPoint();
        triangulationPoint1.setId(1L);
        TriangulationPoint triangulationPoint2 = new TriangulationPoint();
        triangulationPoint2.setId(triangulationPoint1.getId());
        assertThat(triangulationPoint1).isEqualTo(triangulationPoint2);
        triangulationPoint2.setId(2L);
        assertThat(triangulationPoint1).isNotEqualTo(triangulationPoint2);
        triangulationPoint1.setId(null);
        assertThat(triangulationPoint1).isNotEqualTo(triangulationPoint2);
    }
}
