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

package org.tbbtalent.server.model.db;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import org.tbbtalent.server.model.db.task.TaskType;
import org.tbbtalent.server.model.db.task.YesNoQuestionTask;

/**
 * Default Implementation
 *
 * @author John Cameron
 */
@Entity(name="YesNoQuestionTask")
@DiscriminatorValue("YesNoQuestionTask")
public class YesNoQuestionTaskImpl extends QuestionTaskImpl implements YesNoQuestionTask {
    public TaskType getTaskType() {
        return TaskType.YesNoQuestion;
    }

}