package com.fundoonotes.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDto {
    private Long id;
    private String title;
    private String description;
    private boolean pinned;
    private boolean archived;
    private boolean trashed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LabelResponseDto> labels;
}