package com.fundoonotes.service;

import com.fundoonotes.dto.request.*;
import com.fundoonotes.dto.response.*;


public interface UserService {
    UserResponseDto register(UserRegisterRequestDto requestDto);
    LoginResponseDto login(LoginRequestDto requestDto);
    UserResponseDto getProfile(String token);

    void logout(String token);
}