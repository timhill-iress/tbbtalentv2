/*
 * Copyright (c) 2021 Talent Beyond Boundaries.
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package org.tbbtalent.server.model.db;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Name/Value associated with a candidate.
 * <p/>
 * The property name is unique for the candidate. ie a candidate can't have two properties with
 * the same name.
 */
@Entity
@Table(name = "candidate_property")
@IdClass(CandidatePropertyKey.class)
@Getter
@Setter
public class CandidateProperty {

    /**
     * Candidate associated with this property
     */
    @Id
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    /**
     * The name of the property
     */
    @Id
    @NonNull
    private String name;

    /**
     * The value of the property - can be null
     */
    @Nullable
    private String value;

    /**
     * Task associated with this property - if any. May be null.
     */
    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_task_id")
    private TaskImpl relatedTask;
}
