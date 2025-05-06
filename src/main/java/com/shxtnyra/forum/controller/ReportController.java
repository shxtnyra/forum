package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.report.ReportCreateDTO;
import com.shxtnyra.forum.dto.report.ReportDetailsDTO;
import com.shxtnyra.forum.dto.report.ReportShortDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PostMapping
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<ReportDetailsDTO> createReport(@RequestBody @Valid ReportCreateDTO dto,
                                                          @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.createReport(dto, user));
    }

    // Получить жалобу
    @GetMapping
    public ResponseEntity<ReportDetailsDTO> getReportById(@PathVariable Long id,
                                                          @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    // Получить все жалобы на комментарий
    @GetMapping
    public ResponseEntity<Page<ReportShortDTO>> getAllReports(@PathVariable Long id,
                                                              Pageable pageable,
                                                              @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.getAllReports(id, pageable));
    }

    // Получить все жалобы по статусу решения
    @GetMapping
    public ResponseEntity<Page<ReportShortDTO>> getAllReportsByStatus(boolean status,
                                                                      Pageable pageable,
                                                                      @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.getAllReportsByStatus(status, pageable));
    }

    // Отметить жалобу как рассмотренную
    @GetMapping
    public ResponseEntity<Void> setAsSolved(@PathVariable Long id,
                                            @AuthenticationPrincipal UserEntity user) {
        reportService.markAsSolved(id);
        return ResponseEntity.noContent().build();
    }

    // Удалить жалобу
    @DeleteMapping
    public ResponseEntity<Void> deleteReport(@PathVariable Long id,
                                             @AuthenticationPrincipal UserEntity user) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
