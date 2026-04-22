package com.fundoonotes.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequestDto {

    @NotBlank(message = "Label name is required")
    private String name;
}