package com.fundoonotes.service;

import com.fundoonotes.dto.request.NoteRequestDto;
import com.fundoonotes.dto.response.NoteResponseDto;
import java.util.List;

public interface NoteService {
    NoteResponseDto createNote(NoteRequestDto requestDto, String token);
    List<NoteResponseDto> getAllNotes(String token);
    NoteResponseDto getNoteById(Long noteId, String token);
    NoteResponseDto updateNote(Long noteId, NoteRequestDto requestDto, String token);
    void deleteNote(Long noteId, String token);
    NoteResponseDto pinNote(Long noteId, String token);
    NoteResponseDto archiveNote(Long noteId, String token);
    NoteResponseDto trashNote(Long noteId, String token);
    NoteResponseDto restoreNote(Long noteId, String token);
    List<NoteResponseDto> getPinnedNotes(String token);
    List<NoteResponseDto> getArchivedNotes(String token);
    List<NoteResponseDto> getTrashedNotes(String token);
    List<NoteResponseDto> searchNotes(String keyword, String token);
    NoteResponseDto addLabelToNote(Long noteId, Long labelId, String token);
    NoteResponseDto removeLabelFromNote(Long noteId, Long labelId, String token);
}