/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.repository.db;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbbtalent.server.model.db.Country;
import org.tbbtalent.server.model.db.Status;

public interface CountryRepository extends JpaRepository<Country, Long>, JpaSpecificationExecutor<Country> {


    @Query(" select c from Country c "
            + " where c.status = :status order by c.name asc")
    List<Country> findByStatus(@Param("status") Status status);

    @Query(" select distinct c from Country c "
            + " where lower(c.name) = lower(:name)"
            + " and c.status != 'deleted' order by c.name asc" )
    Country findByNameIgnoreCase(@Param("name") String name);

    @Query(" select c.name from Country c "
            + " where id in (:ids) order by c.name asc" )
    List<String> getNamesForIds(@Param("ids") List<Long> ids);

    @Query(" select c from Country c "
            + " where c.status = :status"
            + " and c in (:countries)" )
    List<Country> findByStatusAndSourceCountries(@Param("status") Status status, @Param("countries") Set<Country> countries);

}