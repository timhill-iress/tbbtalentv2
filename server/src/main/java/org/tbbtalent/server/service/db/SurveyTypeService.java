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

package org.tbbtalent.server.service.db;

import org.springframework.data.domain.Page;
import org.tbbtalent.server.model.db.SurveyType;
import org.tbbtalent.server.request.survey.SearchSurveyTypeRequest;

import java.util.List;

public interface SurveyTypeService {
    List<SurveyType> listActiveSurveyTypes();
    Page<SurveyType> searchActiveSurveyTypes(SearchSurveyTypeRequest request);
    List<SurveyType> listSurveyTypes();
}

