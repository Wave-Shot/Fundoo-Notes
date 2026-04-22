package com.fundoonotes.service.impl;

import com.fundoonotes.dto.request.NoteRequestDto;
import com.fundoonotes.dto.response.LabelResponseDto;
import com.fundoonotes.dto.response.NoteResponseDto;
import com.fundoonotes.entity.*;
import com.fundoonotes.exception.*;
import com.fundoonotes.repository.*;
import com.fundoonotes.service.NoteService;
import com.fundoonotes.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final TokenUtil tokenUtil;

    public NoteServiceImpl(NoteRepository noteRepository,
                           UserRepository userRepository,
                           LabelRepository labelRepository,
                           TokenUtil tokenUtil) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.labelRepository = labelRepository;
        this.tokenUtil = tokenUtil;
    }

    private User getUserFromToken(String token) {
        Long userId = tokenUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Note getNoteForUser(Long noteId, Long userId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
        if (!note.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have access to this note");
        }
        return note;
    }

    @Override
    public NoteResponseDto createNote(NoteRequestDto requestDto, String token) {
        log.info("Creating note");
        User user = getUserFromToken(token);
        Note note = new Note();
        note.setTitle(requestDto.getTitle());
        note.setDescription(requestDto.getDescription());
        note.setUser(user);
        Note saved = noteRepository.save(note);
        return mapToResponse(saved);
    }

    @Override
    public List<NoteResponseDto> getAllNotes(String token) {
        User user = getUserFromToken(token);
        return noteRepository.findByUserIdAndArchivedFalseAndTrashedFalse(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /*
     * @Cacheable — first call hits MySQL and stores result in Redis under key "notes::5"
     * (where 5 is the noteId). Every subsequent call with same noteId returns
     * from Redis instantly without touching the database.
     */
    @Override
    @Cacheable(value = "notes", key = "#noteId")
    public NoteResponseDto getNoteById(Long noteId, String token) {
        log.info("Fetching note {} from database (cache miss)", noteId);
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        return mapToResponse(note);
    }

    /*
     * @CachePut — always runs the method AND updates Redis with the new result.
     * This keeps the cache fresh after an update so stale data is never served.
     */
    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto updateNote(Long noteId, NoteRequestDto requestDto, String token) {
        log.info("Updating note {} and refreshing cache", noteId);
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setTitle(requestDto.getTitle());
        note.setDescription(requestDto.getDescription());
        return mapToResponse(noteRepository.save(note));
    }

    /*
     * @CacheEvict — removes this note's entry from Redis when deleted.
     * Without this, Redis would still serve the deleted note for up to 10 minutes.
     */
    @Override
    @CacheEvict(value = "notes", key = "#noteId")
    public void deleteNote(Long noteId, String token) {
        log.info("Deleting note {} and evicting from cache", noteId);
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        noteRepository.delete(note);
    }

    /*
     * Pin/archive/trash/restore also use @CachePut so if someone calls
     * getNoteById after pinning, they get the updated state from cache,
     * not the old unpinned version.
     */
    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto pinNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setPinned(!note.isPinned());
        return mapToResponse(noteRepository.save(note));
    }

    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto archiveNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setArchived(!note.isArchived());
        return mapToResponse(noteRepository.save(note));
    }

    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto trashNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setTrashed(true);
        return mapToResponse(noteRepository.save(note));
    }

    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto restoreNote(Long noteId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.setTrashed(false);
        return mapToResponse(noteRepository.save(note));
    }

    @Override
    public List<NoteResponseDto> getPinnedNotes(String token) {
        User user = getUserFromToken(token);
        return noteRepository.findByUserIdAndPinnedTrue(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<NoteResponseDto> getArchivedNotes(String token) {
        User user = getUserFromToken(token);
        return noteRepository.findByUserIdAndArchivedTrue(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<NoteResponseDto> getTrashedNotes(String token) {
        User user = getUserFromToken(token);
        return noteRepository.findByUserIdAndTrashedTrue(user.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<NoteResponseDto> searchNotes(String keyword, String token) {
        User user = getUserFromToken(token);
        return noteRepository.findByUserIdAndTitleContainingIgnoreCase(user.getId(), keyword)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto addLabelToNote(Long noteId, Long labelId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
        if (!note.getLabels().contains(label)) {
            note.getLabels().add(label);
        }
        return mapToResponse(noteRepository.save(note));
    }

    @Override
    @CachePut(value = "notes", key = "#noteId")
    public NoteResponseDto removeLabelFromNote(Long noteId, Long labelId, String token) {
        User user = getUserFromToken(token);
        Note note = getNoteForUser(noteId, user.getId());
        note.getLabels().removeIf(l -> l.getId().equals(labelId));
        return mapToResponse(noteRepository.save(note));
    }

    private NoteResponseDto mapToResponse(Note note) {
        NoteResponseDto dto = new NoteResponseDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setDescription(note.getDescription());
        dto.setPinned(note.isPinned());
        dto.setArchived(note.isArchived());
        dto.setTrashed(note.isTrashed());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setUpdatedAt(note.getUpdatedAt());
        dto.setLabels(note.getLabels().stream()
                .map(l -> new LabelResponseDto(l.getId(), l.getName()))
                .collect(Collectors.toList()));
        return dto;
    }
}