package com.fundoonotes.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
}