package org.tbbtalent.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbbtalent.server.model.SavedList;

public interface SavedListRepository extends JpaRepository<SavedList, Long>, JpaSpecificationExecutor<SavedList> {

    @Query(" select distinct s from SavedList s "
            + " where lower(s.name) = lower(:name)" )
    Optional<SavedList> findByNameIgnoreCase(@Param("name") String name);

    @Query(" select distinct s from SavedList s left join fetch s.users"
            + " where s.id = :id" )
    Optional<SavedList> findByIdLoadUsers(@Param("id") long id);

    @Query(" select distinct s from SavedList s left join fetch s.candidates"
            + " where s.id = :id" )
    Optional<SavedList> findByIdLoadCandidates(@Param("id") long id);
}