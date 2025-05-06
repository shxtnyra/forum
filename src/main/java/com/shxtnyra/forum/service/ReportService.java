package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.report.ReportCreateDTO;
import com.shxtnyra.forum.dto.report.ReportDetailsDTO;
import com.shxtnyra.forum.dto.report.ReportShortDTO;
import com.shxtnyra.forum.entity.ReportEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.ReportReason;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.ReportMapper;
import com.shxtnyra.forum.repository.CommentRepository;
import com.shxtnyra.forum.repository.ReportRepository;
import com.shxtnyra.forum.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // Создание
    @Transactional
    public ReportDetailsDTO createReport(ReportCreateDTO createDTO, UserEntity user) {

        ReportEntity report = ReportEntity.builder()
                .reason(createDTO.getReason())
                .reporter(user)
                .build();

        report = reportRepository.save(report);
        return ReportMapper.toDetailsDTO(report);
    }

    // Получение жалобы по id
    public ReportDetailsDTO getReportById(Long id) {
        ReportEntity report = reportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Report not found"));

        return ReportMapper.toDetailsDTO(report);
    }

    // Получение всех жалоб на комментарий
    public Page<ReportShortDTO> getAllReports(Long id, Pageable pageable) {
        if (!commentRepository.existsById(id)) {
            throw new EntityNotFoundException("Comment not found");
        }

        return reportRepository.findByCommentId(id, pageable)
                .map(ReportMapper::toShortDTO);
    }

    // Получение всех жалоб от одного пользователя
    public Page<ReportShortDTO> getAllReportsFromUser(Long id, Pageable pageable) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }

        return reportRepository.findByReporterId(id, pageable)
                .map(ReportMapper::toShortDTO);
    }

    // Получение всех жалоб по статусу решения (решена/нерешена)
    public Page<ReportShortDTO> getAllReportsByStatus(boolean status, Pageable pageable) {
        return reportRepository.findByIsSolved(status, pageable)
                .map(ReportMapper::toShortDTO);
    }

    // Получение количества нерешенных жалоб
    public int getUnresolvedReportsNumber() {
        return reportRepository.countByIsSolvedFalse();
    }

    // Получение всех жалоб с определенной причиной
    public Page<ReportShortDTO> getAllReportsByReason(ReportReason reason, Pageable pageable) {
        return reportRepository.findByReason(reason, pageable)
                .map(ReportMapper::toShortDTO);
    }

    // Пометить жалобу как рассмотренную
    public void markAsSolved(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new EntityNotFoundException("Report not found");
        }

        reportRepository.markAsSolved(id);
    }

    // Удаление жалобы
    public void deleteReport(Long id) {
        if (!reportRepository.existsById(id)) {
            throw new EntityNotFoundException("Report not found");
        }

        reportRepository.deleteById(id);
    }
}
