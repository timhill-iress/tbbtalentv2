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

package org.tbbtalent.server.api.admin;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tbbtalent.server.model.db.BrandingInfo;
import org.tbbtalent.server.service.db.BrandingService;
import org.tbbtalent.server.util.dto.DtoBuilder;

/**
 * Access to branding information for the admin portal.
 */
@RestController()
@RequestMapping("/api/admin/branding")
public class BrandingAdminApi {
    private static final Logger log = LoggerFactory.getLogger(BrandingAdminApi.class);

    private final BrandingService brandingService;

    @Autowired
    public BrandingAdminApi(BrandingService brandingService) {
        this.brandingService = brandingService;
    }

    /**
     * Retrieve the branding information for the admin portal.
     * @return branding information
     */
    @GetMapping()
    public Map<String, Object> getBrandingInfo() {

        BrandingInfo info = brandingService.getBrandingInfo(null);
        return brandingInfoDto().build(info);
    }

    DtoBuilder brandingInfoDto() {
        return new DtoBuilder()
                .add("logo")
                .add("partnerName")
                .add("websiteUrl")
                ;
    }
}
