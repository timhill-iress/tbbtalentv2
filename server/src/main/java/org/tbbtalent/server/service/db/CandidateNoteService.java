/*
 * Copyright (c) 2020 Talent Beyond Boundaries. All rights reserved.
 */

package org.tbbtalent.server.service.db;

import org.springframework.data.domain.Page;
import org.tbbtalent.server.model.db.CandidateNote;
import org.tbbtalent.server.request.note.CreateCandidateNoteRequest;
import org.tbbtalent.server.request.note.SearchCandidateNotesRequest;
import org.tbbtalent.server.request.note.UpdateCandidateNoteRequest;

public interface CandidateNoteService {

    Page<CandidateNote> searchCandidateNotes(SearchCandidateNotesRequest request);

    CandidateNote createCandidateNote(CreateCandidateNoteRequest request);

    CandidateNote updateCandidateNote(long id, UpdateCandidateNoteRequest request);


}