package com.fundoonotes.controller;

import com.fundoonotes.dto.request.NoteRequestDto;
import com.fundoonotes.dto.response.NoteResponseDto;
import com.fundoonotes.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<NoteResponseDto> createNote(
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noteService.createNote(requestDto, token));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponseDto>> getAllNotes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getAllNotes(token));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> getNoteById(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getNoteById(noteId, token));
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteResponseDto> updateNote(
            @PathVariable Long noteId,
            @Valid @RequestBody NoteRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.updateNote(noteId, requestDto, token));
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<String> deleteNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        noteService.deleteNote(noteId, token);
        return ResponseEntity.ok("Note deleted successfully");
    }

    @PatchMapping("/{noteId}/pin")
    public ResponseEntity<NoteResponseDto> pinNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.pinNote(noteId, token));
    }

    @PatchMapping("/{noteId}/archive")
    public ResponseEntity<NoteResponseDto> archiveNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.archiveNote(noteId, token));
    }

    @PatchMapping("/{noteId}/trash")
    public ResponseEntity<NoteResponseDto> trashNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.trashNote(noteId, token));
    }

    @PatchMapping("/{noteId}/restore")
    public ResponseEntity<NoteResponseDto> restoreNote(
            @PathVariable Long noteId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.restoreNote(noteId, token));
    }

    @GetMapping("/pinned")
    public ResponseEntity<List<NoteResponseDto>> getPinnedNotes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getPinnedNotes(token));
    }

    @GetMapping("/archived")
    public ResponseEntity<List<NoteResponseDto>> getArchivedNotes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getArchivedNotes(token));
    }

    @GetMapping("/trashed")
    public ResponseEntity<List<NoteResponseDto>> getTrashedNotes(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.getTrashedNotes(token));
    }

    @GetMapping("/search")
    public ResponseEntity<List<NoteResponseDto>> searchNotes(
            @RequestParam String keyword,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.searchNotes(keyword, token));
    }

    @PatchMapping("/{noteId}/labels/{labelId}")
    public ResponseEntity<NoteResponseDto> addLabel(
            @PathVariable Long noteId,
            @PathVariable Long labelId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.addLabelToNote(noteId, labelId, token));
    }

    @DeleteMapping("/{noteId}/labels/{labelId}")
    public ResponseEntity<NoteResponseDto> removeLabel(
            @PathVariable Long noteId,
            @PathVariable Long labelId,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(noteService.removeLabelFromNote(noteId, labelId, token));
    }
}