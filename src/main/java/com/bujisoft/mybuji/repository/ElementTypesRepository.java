package com.bujisoft.mybuji.repository;

import com.bujisoft.mybuji.domain.ElementTypes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ElementTypes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ElementTypesRepository extends JpaRepository<ElementTypes, Long> {}
