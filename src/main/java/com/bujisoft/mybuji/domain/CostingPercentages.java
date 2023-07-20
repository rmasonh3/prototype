package com.bujisoft.mybuji.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity Costing Percentages
 */
@Schema(description = "Entity Costing Percentages")
@Entity
@Table(name = "costing_percentages")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "costingpercentages")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CostingPercentages implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "costing_system", nullable = false)
    private Integer costingSystem;

    @NotNull
    @Column(name = "costing_qual", nullable = false)
    private Integer costingQual;

    @NotNull
    @Column(name = "costing_imp", nullable = false)
    private Integer costingImp;

    @NotNull
    @Column(name = "costing_post_imp", nullable = false)
    private Integer costingPostImp;

    @Column(name = "active")
    private Boolean active;

    @NotNull
    @Column(name = "date_added", nullable = false)
    private Instant dateAdded;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CostingPercentages id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCostingSystem() {
        return this.costingSystem;
    }

    public CostingPercentages costingSystem(Integer costingSystem) {
        this.setCostingSystem(costingSystem);
        return this;
    }

    public void setCostingSystem(Integer costingSystem) {
        this.costingSystem = costingSystem;
    }

    public Integer getCostingQual() {
        return this.costingQual;
    }

    public CostingPercentages costingQual(Integer costingQual) {
        this.setCostingQual(costingQual);
        return this;
    }

    public void setCostingQual(Integer costingQual) {
        this.costingQual = costingQual;
    }

    public Integer getCostingImp() {
        return this.costingImp;
    }

    public CostingPercentages costingImp(Integer costingImp) {
        this.setCostingImp(costingImp);
        return this;
    }

    public void setCostingImp(Integer costingImp) {
        this.costingImp = costingImp;
    }

    public Integer getCostingPostImp() {
        return this.costingPostImp;
    }

    public CostingPercentages costingPostImp(Integer costingPostImp) {
        this.setCostingPostImp(costingPostImp);
        return this;
    }

    public void setCostingPostImp(Integer costingPostImp) {
        this.costingPostImp = costingPostImp;
    }

    public Boolean getActive() {
        return this.active;
    }

    public CostingPercentages active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Instant getDateAdded() {
        return this.dateAdded;
    }

    public CostingPercentages dateAdded(Instant dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(Instant dateAdded) {
        this.dateAdded = dateAdded;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CostingPercentages)) {
            return false;
        }
        return id != null && id.equals(((CostingPercentages) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CostingPercentages{" +
            "id=" + getId() +
            ", costingSystem=" + getCostingSystem() +
            ", costingQual=" + getCostingQual() +
            ", costingImp=" + getCostingImp() +
            ", costingPostImp=" + getCostingPostImp() +
            ", active='" + getActive() + "'" +
            ", dateAdded='" + getDateAdded() + "'" +
            "}";
    }
}
