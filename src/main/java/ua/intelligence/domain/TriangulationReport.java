package ua.intelligence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TriangulationReport.
 */
@Entity
@Table(name = "triangulation_report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TriangulationReport implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private ZonedDateTime date;

    @Column(name = "name")
    private String name;

    @Column(name = "conclusion")
    private String conclusion;

    @OneToMany(mappedBy = "triangulationReport")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "triangulationReport" }, allowSetters = true)
    private Set<TriangulationPoint> points = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TriangulationReport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public TriangulationReport date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getName() {
        return this.name;
    }

    public TriangulationReport name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConclusion() {
        return this.conclusion;
    }

    public TriangulationReport conclusion(String conclusion) {
        this.setConclusion(conclusion);
        return this;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public Set<TriangulationPoint> getPoints() {
        return this.points;
    }

    public void setPoints(Set<TriangulationPoint> triangulationPoints) {
        if (this.points != null) {
            this.points.forEach(i -> i.setTriangulationReport(null));
        }
        if (triangulationPoints != null) {
            triangulationPoints.forEach(i -> i.setTriangulationReport(this));
        }
        this.points = triangulationPoints;
    }

    public TriangulationReport points(Set<TriangulationPoint> triangulationPoints) {
        this.setPoints(triangulationPoints);
        return this;
    }

    public TriangulationReport addPoints(TriangulationPoint triangulationPoint) {
        this.points.add(triangulationPoint);
        triangulationPoint.setTriangulationReport(this);
        return this;
    }

    public TriangulationReport removePoints(TriangulationPoint triangulationPoint) {
        this.points.remove(triangulationPoint);
        triangulationPoint.setTriangulationReport(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TriangulationReport)) {
            return false;
        }
        return id != null && id.equals(((TriangulationReport) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TriangulationReport{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", name='" + getName() + "'" +
            ", conclusion='" + getConclusion() + "'" +
            "}";
    }
}
