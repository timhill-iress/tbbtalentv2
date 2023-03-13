/*
 * Copyright (c) 2023 Talent Beyond Boundaries.
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

package org.tbbtalent.server.util.html;

/**
 * Abstracts HTML Sanitization from a specific implementation
 * @author Tim Hill
 */
public interface Sanitizer {
    /**
     * Given an untrusted HTML string, remove any tags that might contribute to a cross-site scripting (XSS) attack
     * Ref: https://owasp.org/www-community/attacks/xss/
     * @param html an untrusted HTML string
     * @return an HTML string with any potentially Cross Site Scripting tags removed
     */
    String sanitize(String html);
}
