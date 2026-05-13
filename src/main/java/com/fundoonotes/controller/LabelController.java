package com.fundoonotes.controller;

import com.fundoonotes.dto.request.LabelRequestDto;
import com.fundoonotes.dto.response.LabelResponseDto;
import com.fundoonotes.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping
    public ResponseEntity<LabelResponseDto> createLabel(
            @Valid @RequestBody LabelRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labelService.createLabel(requestDto, token));
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseDto>> getAllLabels(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(labelService.getAllLabels(token));
    }

    @PutMapping("/{labelId}")
    public ResponseEntity<LabelResponseDto> updateLabel(
            @PathVariable Long labelId,
            @Valid @RequestBody LabelRequestDto requestDto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(labelService.updateLabel(labelId, requestDto, token));
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<String> deleteLabel(
            @PathVariable Long labelId,
            @RequestHeader("Authorization") String token) {
        labelService.deleteLabel(labelId, token);
        return ResponseEntity.ok("Label deleted successfully");
    }
}