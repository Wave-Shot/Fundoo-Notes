package com.fundoonotes.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}