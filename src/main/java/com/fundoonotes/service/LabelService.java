package com.fundoonotes.service;

import com.fundoonotes.dto.request.LabelRequestDto;
import com.fundoonotes.dto.response.LabelResponseDto;
import java.util.List;

public interface LabelService {
    LabelResponseDto createLabel(LabelRequestDto requestDto, String token);
    List<LabelResponseDto> getAllLabels(String token);
    LabelResponseDto updateLabel(Long labelId, LabelRequestDto requestDto, String token);
    void deleteLabel(Long labelId, String token);
}