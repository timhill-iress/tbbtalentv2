/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.repository.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbbtalent.server.model.db.Nationality;
import org.tbbtalent.server.model.db.Status;

public interface NationalityRepository extends JpaRepository<Nationality, Long>, JpaSpecificationExecutor<Nationality> {

    @Query(" select n from Nationality n "
            + " where n.status = :status order by n.name asc")
    List<Nationality> findByStatus(@Param("status") Status status);

    @Query(" select distinct n from Nationality n "
            + " where lower(n.name) = lower(:name)"
            + " and n.status != 'deleted' order by n.name asc" )
    Nationality findByNameIgnoreCase(@Param("name") String name);

    @Query(" select n.name from Nationality n "
            + " where id in (:ids) order by n.name asc" )
    List<String> getNamesForIds(@Param("ids") List<Long> ids);}