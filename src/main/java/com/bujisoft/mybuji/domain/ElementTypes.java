package com.bujisoft.mybuji.domain;

import com.bujisoft.mybuji.domain.enumeration.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Entity Element Type
 */
@Schema(description = "Entity Element Type")
@Entity
@Table(name = "element_types")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "elementtypes")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ElementTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "element", nullable = false, unique = true)
    private UUID element;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ElementTypes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getElement() {
        return this.element;
    }

    public ElementTypes element(UUID element) {
        this.setElement(element);
        return this;
    }

    public void setElement(UUID element) {
        this.element = element;
    }

    public Type getType() {
        return this.type;
    }

    public ElementTypes type(Type type) {
        this.setType(type);
        return this;
    }

    public void setType(Type type) {
        this.type = type;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElementTypes)) {
            return false;
        }
        return id != null && id.equals(((ElementTypes) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ElementTypes{" +
            "id=" + getId() +
            ", element='" + getElement() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
