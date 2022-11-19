/*
* Copyright (c) 2022 Talent Beyond Boundaries.
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

do
$$
begin
if not exists (select * from pg_user where usename = 'tbbtalent') then
create role tbbtalent password 'tbbtalent';
end if;
end
$$
;
ALTER ROLE "tbbtalent" WITH LOGIN;

SELECT 'CREATE DATABASE tbbtalent'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'tbbtalent')\gexec


GRANT CONNECT ON DATABASE tbbtalent TO tbbtalent;