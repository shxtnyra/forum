package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.report.ReportCreateDTO;
import com.shxtnyra.forum.dto.report.ReportDetailsDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    /**
     * Создать жалобу на пост.
     *
     * @param postId    идентификатор поста
     * @param createDTO данные жалобы
     * @param user      текущий пользователь
     * @return ReportDetailsDTO созданная жалоба
     */
    @PostMapping("/posts/{postId}/reports")
    public ResponseEntity<ReportDetailsDTO> createReportPost(@PathVariable Long postId,
                                                            @RequestBody @Valid ReportCreateDTO createDTO,
                                                            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.createReportOnPost(postId, createDTO, user));
    }

    /**
     * Создать жалобу на комментарий.
     *
     * @param commentId идентификатор комментария
     * @param createDTO данные жалобы
     * @param user      текущий пользователь
     * @return ReportDetailsDTO созданная жалоба
     */
    @PostMapping("/comments/{commentId}/reports")
    public ResponseEntity<ReportDetailsDTO> createReportComment(@PathVariable Long commentId,
                                                               @RequestBody @Valid ReportCreateDTO createDTO,
                                                               @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.createReportOnComment(commentId, createDTO, user));
    }
}
