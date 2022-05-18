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

package org.tbbtalent.server.security;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.tbbtalent.server.model.db.Candidate;
import org.tbbtalent.server.model.db.Role;
import org.tbbtalent.server.model.db.User;

import java.util.Optional;

@Service
public class AuthService {

    /**
     * Return logged in user. Optional empty id not logged in.
     * <p/>
     * Note that this User object is not fetched from the database - so if you need a live
     * entity associated with a session, you need to fetch user entity object from the DB using
     * the id of the object returned from this call.
     * For example:
     * userService.getUser(getLoggedInUser().get().getId());
     *
     * @return Logged in user or empty if not logged in.
     */
    public Optional<User> getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof TbbUserDetails) {
            return Optional.of(((TbbUserDetails) auth.getPrincipal()).getUser());
        }
        return Optional.empty();
    }

    /**
     * Returns id of logged in candidate or null if not logged in
     * @return id of logged in candidate
     */
    public @Nullable Long getLoggedInCandidateId() {
        Long ret = null;
        User user = getLoggedInUser().orElse(null);
        if (user != null) {
            Candidate candidate = user.getCandidate();
            if (candidate != null) {
                ret = candidate.getId();
            }
        }
        return ret;
    }

    /**
     * Returns candidate retrieved from the user context.
     * Note that this Candidate object is not retrieved from the database and
     * therefore will not be part of the JPA persistence context
     * so lazily loaded attributes will not be fetched from the database and
     * will be absent.
     * <p/>
     * It should therefore should only be used as a base for adding new
     * attributes and saving to the database.
     * @return Candidate with no associated attributes
     */
    public @Nullable Candidate getLoggedInCandidate(){
        User user = getLoggedInUser().orElse(null);
        return user == null ? null : user.getCandidate();
    }

    public @Nullable String getUserLanguage() {
        User user = getLoggedInUser().orElse(null);
        return user == null ? null : user.getSelectedLanguage();
    }

    public boolean authoriseLoggedInUser(Candidate owner) {
        User user = getLoggedInUser().orElse(null);
        if (user != null) {
            if (user.getReadOnly()) {
                return false;
            } else if (user.getRole().equals(Role.admin)) {
                return true;
            } else if (user.getRole().equals(Role.sourcepartneradmin)) {
                if (!user.getSourceCountries().isEmpty()) {
                    return user.getSourceCountries().contains(owner.getCountry());
                } else {
                    return true;
                }
            } else if (user.getRole().equals(Role.user)){
                return owner.getId().equals(user.getCandidate().getId());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
