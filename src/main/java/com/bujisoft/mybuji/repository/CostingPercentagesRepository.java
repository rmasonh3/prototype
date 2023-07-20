package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.CostingPercentages;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CostingPercentages entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CostingPercentagesRepository extends JpaRepository<CostingPercentages, Long> {}
