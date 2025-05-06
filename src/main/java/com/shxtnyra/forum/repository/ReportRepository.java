package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.ReportEntity;
import com.shxtnyra.forum.enums.ReportReason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    // Все жалобы на комментарий
    Page<ReportEntity> findByCommentId(Long commentId, Pageable pageable);

    // Все жалобы от пользователя
    Page<ReportEntity> findByReporterId(Long reporterId, Pageable pageable);

    // Поиск по статусу (решён/не решён)
    Page<ReportEntity> findByIsSolved(boolean isSolved, Pageable pageable);

    // Найти все жалобы с определённой причиной
    Page<ReportEntity> findByReason(ReportReason reason, Pageable pageable);

    // Количество нерешённых жалоб
    int countByIsSolvedFalse();

    // Пометить жалобу как рассмотренную
    @Modifying
    @Query("UPDATE ReportEntity r SET r.isSolved = true WHERE r.id = :id")
    void markAsSolved(@Param("id") Long id);
}
