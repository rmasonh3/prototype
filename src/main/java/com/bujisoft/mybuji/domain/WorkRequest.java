package com.bujisoft.mybuji.domain;

import com.bujisoft.mybuji.domain.enumeration.DesignStatus;
import com.bujisoft.mybuji.domain.enumeration.ProjectStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity Work Request
 */
@Schema(description = "Entity Work Request")
@Entity
@Table(name = "work_request")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "workrequest")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "project_id", nullable = false, unique = true)
    private String projectId;

    @NotNull
    @Column(name = "work_request", nullable = false)
    private String workRequest;

    @NotNull
    @Column(name = "work_request_description", nullable = false)
    private String workRequestDescription;

    @NotNull
    @Column(name = "work_rwquest_phase", nullable = false)
    private String workRwquestPhase;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "design")
    private DesignStatus design;

    @OneToMany(mappedBy = "workrequest")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "user", "workrequest" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectId() {
        return this.projectId;
    }

    public WorkRequest projectId(String projectId) {
        this.setProjectId(projectId);
        return this;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getWorkRequest() {
        return this.workRequest;
    }

    public WorkRequest workRequest(String workRequest) {
        this.setWorkRequest(workRequest);
        return this;
    }

    public void setWorkRequest(String workRequest) {
        this.workRequest = workRequest;
    }

    public String getWorkRequestDescription() {
        return this.workRequestDescription;
    }

    public WorkRequest workRequestDescription(String workRequestDescription) {
        this.setWorkRequestDescription(workRequestDescription);
        return this;
    }

    public void setWorkRequestDescription(String workRequestDescription) {
        this.workRequestDescription = workRequestDescription;
    }

    public String getWorkRwquestPhase() {
        return this.workRwquestPhase;
    }

    public WorkRequest workRwquestPhase(String workRwquestPhase) {
        this.setWorkRwquestPhase(workRwquestPhase);
        return this;
    }

    public void setWorkRwquestPhase(String workRwquestPhase) {
        this.workRwquestPhase = workRwquestPhase;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public WorkRequest startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public WorkRequest endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public ProjectStatus getStatus() {
        return this.status;
    }

    public WorkRequest status(ProjectStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public DesignStatus getDesign() {
        return this.design;
    }

    public WorkRequest design(DesignStatus design) {
        this.setDesign(design);
        return this;
    }

    public void setDesign(DesignStatus design) {
        this.design = design;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setWorkrequest(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setWorkrequest(this));
        }
        this.employees = employees;
    }

    public WorkRequest employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public WorkRequest addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.setWorkrequest(this);
        return this;
    }

    public WorkRequest removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.setWorkrequest(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkRequest)) {
            return false;
        }
        return id != null && id.equals(((WorkRequest) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkRequest{" +
            "id=" + getId() +
            ", projectId='" + getProjectId() + "'" +
            ", workRequest='" + getWorkRequest() + "'" +
            ", workRequestDescription='" + getWorkRequestDescription() + "'" +
            ", workRwquestPhase='" + getWorkRwquestPhase() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", design='" + getDesign() + "'" +
            "}";
    }
}
