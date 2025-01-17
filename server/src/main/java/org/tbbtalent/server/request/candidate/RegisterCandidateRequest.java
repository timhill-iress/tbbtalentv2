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

package org.tbbtalent.server.request.candidate;

import lombok.Getter;
import lombok.Setter;
import org.tbbtalent.server.model.db.HasTcQueryParameters;

@Getter
@Setter
public class RegisterCandidateRequest extends BaseCandidateContactRequest
    implements HasTcQueryParameters {
    private String username;
    private String password;
    private String passwordConfirmation;
    private String reCaptchaV3Token;

    /**
     * If not null, can be used to look up partner to which the candidate belongs.
     */
    private String partnerAbbreviation;

    private String referrerParam;
    private String utmCampaign;
    private String utmContent;
    private String utmMedium;
    private String utmSource;
    private String utmTerm;

}
