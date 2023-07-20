package com.bujisoft.mybuji.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity Work Request Info --> note possible chart future iteration
 */
@Schema(description = "Entity Work Request Info --> note possible chart future iteration")
@Entity
@Table(name = "work_info")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "workinfo")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "scope_act", nullable = false)
    private Float scopeAct;

    @NotNull
    @Column(name = "design_act", nullable = false)
    private Float designAct;

    @NotNull
    @Column(name = "code_act", nullable = false)
    private Float codeAct;

    @NotNull
    @Column(name = "syst_1_act", nullable = false)
    private Float syst1Act;

    @NotNull
    @Column(name = "syst_2_act", nullable = false)
    private Float syst2Act;

    @NotNull
    @Column(name = "qual_act", nullable = false)
    private Float qualAct;

    @NotNull
    @Column(name = "imp_act", nullable = false)
    private Float impAct;

    @NotNull
    @Column(name = "post_imp_act", nullable = false)
    private Float postImpAct;

    @NotNull
    @Column(name = "total_act", nullable = false)
    private Float totalAct;

    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private WorkRequest workrequest;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkInfo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getScopeAct() {
        return this.scopeAct;
    }

    public WorkInfo scopeAct(Float scopeAct) {
        this.setScopeAct(scopeAct);
        return this;
    }

    public void setScopeAct(Float scopeAct) {
        this.scopeAct = scopeAct;
    }

    public Float getDesignAct() {
        return this.designAct;
    }

    public WorkInfo designAct(Float designAct) {
        this.setDesignAct(designAct);
        return this;
    }

    public void setDesignAct(Float designAct) {
        this.designAct = designAct;
    }

    public Float getCodeAct() {
        return this.codeAct;
    }

    public WorkInfo codeAct(Float codeAct) {
        this.setCodeAct(codeAct);
        return this;
    }

    public void setCodeAct(Float codeAct) {
        this.codeAct = codeAct;
    }

    public Float getSyst1Act() {
        return this.syst1Act;
    }

    public WorkInfo syst1Act(Float syst1Act) {
        this.setSyst1Act(syst1Act);
        return this;
    }

    public void setSyst1Act(Float syst1Act) {
        this.syst1Act = syst1Act;
    }

    public Float getSyst2Act() {
        return this.syst2Act;
    }

    public WorkInfo syst2Act(Float syst2Act) {
        this.setSyst2Act(syst2Act);
        return this;
    }

    public void setSyst2Act(Float syst2Act) {
        this.syst2Act = syst2Act;
    }

    public Float getQualAct() {
        return this.qualAct;
    }

    public WorkInfo qualAct(Float qualAct) {
        this.setQualAct(qualAct);
        return this;
    }

    public void setQualAct(Float qualAct) {
        this.qualAct = qualAct;
    }

    public Float getImpAct() {
        return this.impAct;
    }

    public WorkInfo impAct(Float impAct) {
        this.setImpAct(impAct);
        return this;
    }

    public void setImpAct(Float impAct) {
        this.impAct = impAct;
    }

    public Float getPostImpAct() {
        return this.postImpAct;
    }

    public WorkInfo postImpAct(Float postImpAct) {
        this.setPostImpAct(postImpAct);
        return this;
    }

    public void setPostImpAct(Float postImpAct) {
        this.postImpAct = postImpAct;
    }

    public Float getTotalAct() {
        return this.totalAct;
    }

    public WorkInfo totalAct(Float totalAct) {
        this.setTotalAct(totalAct);
        return this;
    }

    public void setTotalAct(Float totalAct) {
        this.totalAct = totalAct;
    }

    public WorkRequest getWorkrequest() {
        return this.workrequest;
    }

    public void setWorkrequest(WorkRequest workRequest) {
        this.workrequest = workRequest;
    }

    public WorkInfo workrequest(WorkRequest workRequest) {
        this.setWorkrequest(workRequest);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkInfo)) {
            return false;
        }
        return id != null && id.equals(((WorkInfo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkInfo{" +
            "id=" + getId() +
            ", scopeAct=" + getScopeAct() +
            ", designAct=" + getDesignAct() +
            ", codeAct=" + getCodeAct() +
            ", syst1Act=" + getSyst1Act() +
            ", syst2Act=" + getSyst2Act() +
            ", qualAct=" + getQualAct() +
            ", impAct=" + getImpAct() +
            ", postImpAct=" + getPostImpAct() +
            ", totalAct=" + getTotalAct() +
            "}";
    }
}
