package com.bujisoft.mybuji.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * Entity Estimate Basis
 */
@Schema(description = "Entity Estimate Basis")
@Entity
@Table(name = "estimate_basis")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "estimatebasis")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EstimateBasis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "subsystem_id", nullable = false)
    private Integer subsystemId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "basis_of_estimate", nullable = false)
    private String basisOfEstimate;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "assumptions", nullable = false)
    private String assumptions;

    @NotNull
    @Column(name = "last_update", nullable = false)
    private Instant lastUpdate;

    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private WorkRequest workrequest;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EstimateBasis id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSubsystemId() {
        return this.subsystemId;
    }

    public EstimateBasis subsystemId(Integer subsystemId) {
        this.setSubsystemId(subsystemId);
        return this;
    }

    public void setSubsystemId(Integer subsystemId) {
        this.subsystemId = subsystemId;
    }

    public String getBasisOfEstimate() {
        return this.basisOfEstimate;
    }

    public EstimateBasis basisOfEstimate(String basisOfEstimate) {
        this.setBasisOfEstimate(basisOfEstimate);
        return this;
    }

    public void setBasisOfEstimate(String basisOfEstimate) {
        this.basisOfEstimate = basisOfEstimate;
    }

    public String getAssumptions() {
        return this.assumptions;
    }

    public EstimateBasis assumptions(String assumptions) {
        this.setAssumptions(assumptions);
        return this;
    }

    public void setAssumptions(String assumptions) {
        this.assumptions = assumptions;
    }

    public Instant getLastUpdate() {
        return this.lastUpdate;
    }

    public EstimateBasis lastUpdate(Instant lastUpdate) {
        this.setLastUpdate(lastUpdate);
        return this;
    }

    public void setLastUpdate(Instant lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public WorkRequest getWorkrequest() {
        return this.workrequest;
    }

    public void setWorkrequest(WorkRequest workRequest) {
        this.workrequest = workRequest;
    }

    public EstimateBasis workrequest(WorkRequest workRequest) {
        this.setWorkrequest(workRequest);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EstimateBasis)) {
            return false;
        }
        return id != null && id.equals(((EstimateBasis) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EstimateBasis{" +
            "id=" + getId() +
            ", subsystemId=" + getSubsystemId() +
            ", basisOfEstimate='" + getBasisOfEstimate() + "'" +
            ", assumptions='" + getAssumptions() + "'" +
            ", lastUpdate='" + getLastUpdate() + "'" +
            "}";
    }
}
