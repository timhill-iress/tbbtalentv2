/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.repository.db;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tbbtalent.server.model.db.CandidateEducation;

public interface CandidateEducationRepository extends JpaRepository<CandidateEducation, Long> {

    // TO DO ADD EDUCATION LEVEL TO CANDIDATE EDUCATION TABLE
//    @Query(" select e from CandidateEducation e "
//            + " where e.educationLevel.id = :id ")
//    List<CandidateEducation> findByEducationLevelId(@Param("id") Long id);

    @Query(" select e from CandidateEducation e "
            + " left join e.candidate c "
            + " where e.id = :id")
    Optional<CandidateEducation> findByIdLoadCandidate(@Param("id") Long id);

    @Query(" select e from CandidateEducation e "
            + " left join e.country "
            + " left join e.educationMajor "
            + " where e.candidate.id = :candidateId ")
    List<CandidateEducation> findByCandidateId(@Param("candidateId") Long candidateId);

    @Query(" select e from CandidateEducation e "
            + " left join e.candidate c "
            + " where e.id = :id "
            + " and c.id = :candidateId ")
    CandidateEducation findByIdAndCandidateId(@Param("id") Long id,
                                              @Param("candidateId") Long candidateId);
}