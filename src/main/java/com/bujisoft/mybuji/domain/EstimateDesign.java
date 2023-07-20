package com.bujisoft.mybuji.domain;

import com.bujisoft.mybuji.domain.enumeration.Complexity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity Estimate Design
 */
@Schema(description = "Entity Estimate Design")
@Entity
@Table(name = "estimate_design")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "estimatedesign")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EstimateDesign implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "qpproach_number", nullable = false)
    private Float qpproachNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "complexity")
    private Complexity complexity;

    @JsonIgnoreProperties(value = { "employees" }, allowSetters = true)
    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private WorkRequest workrequest;

    @OneToOne(optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private ElementTypes elementtypes;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EstimateDesign id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getQpproachNumber() {
        return this.qpproachNumber;
    }

    public EstimateDesign qpproachNumber(Float qpproachNumber) {
        this.setQpproachNumber(qpproachNumber);
        return this;
    }

    public void setQpproachNumber(Float qpproachNumber) {
        this.qpproachNumber = qpproachNumber;
    }

    public Complexity getComplexity() {
        return this.complexity;
    }

    public EstimateDesign complexity(Complexity complexity) {
        this.setComplexity(complexity);
        return this;
    }

    public void setComplexity(Complexity complexity) {
        this.complexity = complexity;
    }

    public WorkRequest getWorkrequest() {
        return this.workrequest;
    }

    public void setWorkrequest(WorkRequest workRequest) {
        this.workrequest = workRequest;
    }

    public EstimateDesign workrequest(WorkRequest workRequest) {
        this.setWorkrequest(workRequest);
        return this;
    }

    public ElementTypes getElementtypes() {
        return this.elementtypes;
    }

    public void setElementtypes(ElementTypes elementTypes) {
        this.elementtypes = elementTypes;
    }

    public EstimateDesign elementtypes(ElementTypes elementTypes) {
        this.setElementtypes(elementTypes);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EstimateDesign)) {
            return false;
        }
        return id != null && id.equals(((EstimateDesign) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EstimateDesign{" +
            "id=" + getId() +
            ", qpproachNumber=" + getQpproachNumber() +
            ", complexity='" + getComplexity() + "'" +
            "}";
    }
}
