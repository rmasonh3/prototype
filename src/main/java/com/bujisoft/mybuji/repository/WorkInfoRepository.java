package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.WorkInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkInfo entity.
 */
@Repository
public interface WorkInfoRepository extends JpaRepository<WorkInfo, Long> {
    default Optional<WorkInfo> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<WorkInfo> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<WorkInfo> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct workInfo from WorkInfo workInfo left join fetch workInfo.workrequest",
        countQuery = "select count(distinct workInfo) from WorkInfo workInfo"
    )
    Page<WorkInfo> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct workInfo from WorkInfo workInfo left join fetch workInfo.workrequest")
    List<WorkInfo> findAllWithToOneRelationships();

    @Query("select workInfo from WorkInfo workInfo left join fetch workInfo.workrequest where workInfo.id =:id")
    Optional<WorkInfo> findOneWithToOneRelationships(@Param("id") Long id);
}
