package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.EstimateBasis;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EstimateBasis entity.
 */
@Repository
public interface EstimateBasisRepository extends JpaRepository<EstimateBasis, Long> {
    default Optional<EstimateBasis> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EstimateBasis> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EstimateBasis> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct estimateBasis from EstimateBasis estimateBasis left join fetch estimateBasis.workrequest",
        countQuery = "select count(distinct estimateBasis) from EstimateBasis estimateBasis"
    )
    Page<EstimateBasis> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct estimateBasis from EstimateBasis estimateBasis left join fetch estimateBasis.workrequest")
    List<EstimateBasis> findAllWithToOneRelationships();

    @Query("select estimateBasis from EstimateBasis estimateBasis left join fetch estimateBasis.workrequest where estimateBasis.id =:id")
    Optional<EstimateBasis> findOneWithToOneRelationships(@Param("id") Long id);
}
