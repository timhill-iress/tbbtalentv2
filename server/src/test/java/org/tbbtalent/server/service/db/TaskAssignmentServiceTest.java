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

package org.tbbtalent.server.service.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.tbbtalent.server.model.db.Candidate;
import org.tbbtalent.server.model.db.SavedList;
import org.tbbtalent.server.model.db.Status;
import org.tbbtalent.server.model.db.TaskAssignmentImpl;
import org.tbbtalent.server.model.db.TaskImpl;
import org.tbbtalent.server.model.db.task.TaskAssignment;
import org.tbbtalent.server.service.db.impl.TaskAssigmentServiceImpl;

/**
 * Tests TaskAssignmentService
 *
 * @author John Cameron
 */
class TaskAssignmentServiceTest {

    private Candidate candidate;
    private SavedList list;
    private TaskImpl task;
    private TaskAssignmentService taskAssignmentService;

    @BeforeEach
    void setUp() {
        // TODO: 22/1/22 Mock candidate attachment service
        taskAssignmentService = new TaskAssigmentServiceImpl(
            null, null);

        task = new TaskImpl();
    }


    //@Test
    void assignTaskToCandidate() {

        LocalDate dueDate = LocalDate.parse("2022-02-14");
        //Assign task to candidate, and remember the returned task assigment.
        TaskAssignment ta = taskAssignmentService.assignTaskToCandidate(null, task, candidate, dueDate);

        assertNotNull(ta);

        assertEquals(candidate, ta.getCandidate());
        assertEquals(task, ta.getTask());
        assertEquals(Status.active, ta.getStatus());
        // TODO: 15/1/22 More...
        // todo How is ActivatedBy assigned a User object when in test

        //Check that the task assignment is contained in the active assignments associated with the
        //candidate.
        List<TaskAssignmentImpl> tas =
            taskAssignmentService.getCandidateTaskAssignmentsByStatus(candidate, Status.active);
        assertNotNull(tas);
        assertTrue(tas.contains(ta));
    }

    //@Test
    void assignTaskToList() {
        taskAssignmentService.assignTaskToList(null, task, list, null);

        //Check that each candidate in the list has an assignment for the above task associated
        //with it.
        Set<Candidate> candidates = list.getCandidates();
        for (Candidate c : candidates) {
            //Get all this candidate's active task assignments
            List<TaskAssignmentImpl> tas =
                taskAssignmentService.getCandidateTaskAssignmentsByStatus(candidate, Status.active);

            //Check that one of the task assignments is associated with the task.
            boolean found = false;
            for (TaskAssignment ta : tas) {

                //Check that every task assignment is associated with the candidate.
                assertEquals(c, ta.getCandidate());

                //Stop looping if we have found a task assignment associated with the task.
                if (ta.getTask().equals(task)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("Task not found for candidate after list assignment");
            }
        }
    }

    //@Test
    void getCandidateTaskAssignments() {
        // TODO: 16/1/22 Set up some assignments, mixed active and inactive

        List<TaskAssignmentImpl> tas;

        //Check passing with null status - should return all assignments
        tas = taskAssignmentService.getCandidateTaskAssignmentsByStatus(candidate, null);
        assertNotNull(tas);
        //TODO Check that all assignments are returned

        //Check passing with active status - should only return active assignments
        tas = taskAssignmentService.getCandidateTaskAssignmentsByStatus(candidate, Status.active);
        assertNotNull(tas);
        //TODO Check that all active assignments are returned

        //Check passing with inactive status - should only return inactive assignments
        tas = taskAssignmentService.getCandidateTaskAssignmentsByStatus(candidate, Status.inactive);
        assertNotNull(tas);
        //TODO Check that all inactive assignments are returned

    }
}
