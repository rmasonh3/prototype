package com.bujisoft.mybuji.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity Scope Design
 */
@Schema(description = "Entity Scope Design")
@Entity
@Table(name = "scope_design")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "scopedesign")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ScopeDesign implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "design_estimate")
    private Float designEstimate;

    @Column(name = "code_estimate")
    private Float codeEstimate;

    @Column(name = "syst_1_estimate")
    private Float syst1Estimate;

    @Column(name = "syst_2_estimate")
    private Float syst2Estimate;

    @Column(name = "qual_estimate")
    private Float qualEstimate;

    @Column(name = "imp_estimate")
    private Float impEstimate;

    @Column(name = "post_imp_estimate")
    private Float postImpEstimate;

    @Column(name = "total_hours")
    private Float totalHours;

    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private WorkRequest workrequest;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ScopeDesign id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getDesignEstimate() {
        return this.designEstimate;
    }

    public ScopeDesign designEstimate(Float designEstimate) {
        this.setDesignEstimate(designEstimate);
        return this;
    }

    public void setDesignEstimate(Float designEstimate) {
        this.designEstimate = designEstimate;
    }

    public Float getCodeEstimate() {
        return this.codeEstimate;
    }

    public ScopeDesign codeEstimate(Float codeEstimate) {
        this.setCodeEstimate(codeEstimate);
        return this;
    }

    public void setCodeEstimate(Float codeEstimate) {
        this.codeEstimate = codeEstimate;
    }

    public Float getSyst1Estimate() {
        return this.syst1Estimate;
    }

    public ScopeDesign syst1Estimate(Float syst1Estimate) {
        this.setSyst1Estimate(syst1Estimate);
        return this;
    }

    public void setSyst1Estimate(Float syst1Estimate) {
        this.syst1Estimate = syst1Estimate;
    }

    public Float getSyst2Estimate() {
        return this.syst2Estimate;
    }

    public ScopeDesign syst2Estimate(Float syst2Estimate) {
        this.setSyst2Estimate(syst2Estimate);
        return this;
    }

    public void setSyst2Estimate(Float syst2Estimate) {
        this.syst2Estimate = syst2Estimate;
    }

    public Float getQualEstimate() {
        return this.qualEstimate;
    }

    public ScopeDesign qualEstimate(Float qualEstimate) {
        this.setQualEstimate(qualEstimate);
        return this;
    }

    public void setQualEstimate(Float qualEstimate) {
        this.qualEstimate = qualEstimate;
    }

    public Float getImpEstimate() {
        return this.impEstimate;
    }

    public ScopeDesign impEstimate(Float impEstimate) {
        this.setImpEstimate(impEstimate);
        return this;
    }

    public void setImpEstimate(Float impEstimate) {
        this.impEstimate = impEstimate;
    }

    public Float getPostImpEstimate() {
        return this.postImpEstimate;
    }

    public ScopeDesign postImpEstimate(Float postImpEstimate) {
        this.setPostImpEstimate(postImpEstimate);
        return this;
    }

    public void setPostImpEstimate(Float postImpEstimate) {
        this.postImpEstimate = postImpEstimate;
    }

    public Float getTotalHours() {
        return this.totalHours;
    }

    public ScopeDesign totalHours(Float totalHours) {
        this.setTotalHours(totalHours);
        return this;
    }

    public void setTotalHours(Float totalHours) {
        this.totalHours = totalHours;
    }

    public WorkRequest getWorkrequest() {
        return this.workrequest;
    }

    public void setWorkrequest(WorkRequest workRequest) {
        this.workrequest = workRequest;
    }

    public ScopeDesign workrequest(WorkRequest workRequest) {
        this.setWorkrequest(workRequest);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScopeDesign)) {
            return false;
        }
        return id != null && id.equals(((ScopeDesign) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ScopeDesign{" +
            "id=" + getId() +
            ", designEstimate=" + getDesignEstimate() +
            ", codeEstimate=" + getCodeEstimate() +
            ", syst1Estimate=" + getSyst1Estimate() +
            ", syst2Estimate=" + getSyst2Estimate() +
            ", qualEstimate=" + getQualEstimate() +
            ", impEstimate=" + getImpEstimate() +
            ", postImpEstimate=" + getPostImpEstimate() +
            ", totalHours=" + getTotalHours() +
            "}";
    }
}
