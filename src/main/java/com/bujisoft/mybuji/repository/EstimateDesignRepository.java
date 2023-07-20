package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.EstimateDesign;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EstimateDesign entity.
 */
@Repository
public interface EstimateDesignRepository extends JpaRepository<EstimateDesign, Long> {
    default Optional<EstimateDesign> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EstimateDesign> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EstimateDesign> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct estimateDesign from EstimateDesign estimateDesign left join fetch estimateDesign.workrequest left join fetch estimateDesign.elementtypes",
        countQuery = "select count(distinct estimateDesign) from EstimateDesign estimateDesign"
    )
    Page<EstimateDesign> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct estimateDesign from EstimateDesign estimateDesign left join fetch estimateDesign.workrequest left join fetch estimateDesign.elementtypes"
    )
    List<EstimateDesign> findAllWithToOneRelationships();

    @Query(
        "select estimateDesign from EstimateDesign estimateDesign left join fetch estimateDesign.workrequest left join fetch estimateDesign.elementtypes where estimateDesign.id =:id"
    )
    Optional<EstimateDesign> findOneWithToOneRelationships(@Param("id") Long id);
}
