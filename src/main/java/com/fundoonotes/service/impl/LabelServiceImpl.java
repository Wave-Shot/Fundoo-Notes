package com.fundoonotes.service.impl;

import com.fundoonotes.dto.request.LabelRequestDto;
import com.fundoonotes.dto.response.LabelResponseDto;
import com.fundoonotes.entity.Label;
import com.fundoonotes.entity.User;
import com.fundoonotes.exception.*;
import com.fundoonotes.repository.*;
import com.fundoonotes.service.LabelService;
import com.fundoonotes.util.TokenUtil;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    public LabelServiceImpl(LabelRepository labelRepository,
                            UserRepository userRepository,
                            TokenUtil tokenUtil) {
        this.labelRepository = labelRepository;
        this.userRepository = userRepository;
        this.tokenUtil = tokenUtil;
    }

    private User getUserFromToken(String token) {
        Long userId = tokenUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public LabelResponseDto createLabel(LabelRequestDto requestDto, String token) {
        User user = getUserFromToken(token);
        Label label = new Label();
        label.setName(requestDto.getName());
        label.setUser(user);
        Label saved = labelRepository.save(label);
        return new LabelResponseDto(saved.getId(), saved.getName());
    }

    @Override
    public List<LabelResponseDto> getAllLabels(String token) {
        User user = getUserFromToken(token);
        return labelRepository.findByUserId(user.getId())
                .stream()
                .map(l -> new LabelResponseDto(l.getId(), l.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public LabelResponseDto updateLabel(Long labelId, LabelRequestDto requestDto, String token) {
        User user = getUserFromToken(token);
        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
        label.setName(requestDto.getName());
        Label saved = labelRepository.save(label);
        return new LabelResponseDto(saved.getId(), saved.getName());
    }

    @Override
    public void deleteLabel(Long labelId, String token) {
        User user = getUserFromToken(token);
        Label label = labelRepository.findByIdAndUserId(labelId, user.getId())
                .orElseThrow(() -> new LabelNotFoundException("Label not found"));
        labelRepository.delete(label);
    }
}