package ua.intelligence.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ua.intelligence.web.rest.TestUtil;

class TriangulationReportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TriangulationReport.class);
        TriangulationReport triangulationReport1 = new TriangulationReport();
        triangulationReport1.setId(1L);
        TriangulationReport triangulationReport2 = new TriangulationReport();
        triangulationReport2.setId(triangulationReport1.getId());
        assertThat(triangulationReport1).isEqualTo(triangulationReport2);
        triangulationReport2.setId(2L);
        assertThat(triangulationReport1).isNotEqualTo(triangulationReport2);
        triangulationReport1.setId(null);
        assertThat(triangulationReport1).isNotEqualTo(triangulationReport2);
    }
}
