package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.ScopeDesign;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ScopeDesign entity.
 */
@Repository
public interface ScopeDesignRepository extends JpaRepository<ScopeDesign, Long> {
    default Optional<ScopeDesign> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ScopeDesign> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ScopeDesign> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct scopeDesign from ScopeDesign scopeDesign left join fetch scopeDesign.workrequest",
        countQuery = "select count(distinct scopeDesign) from ScopeDesign scopeDesign"
    )
    Page<ScopeDesign> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct scopeDesign from ScopeDesign scopeDesign left join fetch scopeDesign.workrequest")
    List<ScopeDesign> findAllWithToOneRelationships();

    @Query("select scopeDesign from ScopeDesign scopeDesign left join fetch scopeDesign.workrequest where scopeDesign.id =:id")
    Optional<ScopeDesign> findOneWithToOneRelationships(@Param("id") Long id);
}
