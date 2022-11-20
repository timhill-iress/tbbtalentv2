#!/bin/bash
#
# Copyright (c) 2022 Talent Beyond Boundaries.
#
# This program is free software: you can redistribute it and/or modify it under
# the terms of the GNU Affero General Public License as published by the Free
# Software Foundation, either version 3 of the License, or any later version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
# for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program. If not, see https://www.gnu.org/licenses/.
#

SCRIPT_PATH=$(dirname "${BASH_SOURCE[0]}");

aws --endpoint-url=http://localhost:4566 s3api create-bucket --bucket dev.files.tbbtalent.org --region us-east-1
aws --endpoint-url=http://localhost:4566 s3 cp "${SCRIPT_PATH}/en.json" s3://dev.files.tbbtalent.org/translations/en.json