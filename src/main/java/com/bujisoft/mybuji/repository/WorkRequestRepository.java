package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.WorkRequest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the WorkRequest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WorkRequestRepository extends JpaRepository<WorkRequest, Long> {}
