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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "occupation")
@SequenceGenerator(name = "seq_gen", sequenceName = "occupation_id_seq", allocationSize = 1)
public class Occupation extends AbstractTranslatableDomainObject<Long> {

    @Column(name = "isco08_code")
    private String isco08Code;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Occupation() {
    }

    public Occupation(String name, Status status) {
        setName(name);
        this.status = status;
    }

    public String getIsco08Code() {
        return isco08Code;
    }

    public void setIsco08Code(String isco08Code) {
        this.isco08Code = isco08Code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
