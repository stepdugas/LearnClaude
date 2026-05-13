package com.learnclaudeai.app.dto;

import jakarta.validation.constraints.*;

public record AdminLessonRequest(
        @NotNull @Min(1) Integer number,
        @NotBlank @Size(max = 200) String title,
        @NotNull @Min(1) @Max(10) Integer tier,
        @NotBlank @Pattern(regexp = "^(FREE|PRO)$") String plan,
        @NotBlank @Size(max = 500) String description,
        @NotBlank @Size(max = 200) String youtubeSearchQuery,
        @NotBlank @Size(max = 500) String challenge
) {}
