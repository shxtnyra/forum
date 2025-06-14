package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.ReportEntity;
import com.shxtnyra.forum.enums.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    @Query("SELECT r FROM ReportEntity r WHERE r.author.id = :userId")
    List<ReportEntity> getAllReportsByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM ReportEntity r WHERE r.post.author.id = :userId")
    List<ReportEntity> getPostReportsOnUserById(@Param("userId") Long userId);

    @Query("SELECT r FROM ReportEntity r WHERE r.comment.author.id = :userId")
    List<ReportEntity> getCommentReportsOnUserById(@Param("userId") Long userId);

    @Query("SELECT r FROM ReportEntity r WHERE r.post.id = :postId")
    List<ReportEntity> getAllPostReportsByPost(@Param("postId") Long postId);

    @Query("SELECT r FROM ReportEntity r WHERE r.comment.post.id = :postId")
    List<ReportEntity> getAllCommentReportsByPost(@Param("postId") Long postId);

    @Query("SELECT r FROM ReportEntity r WHERE r.comment IS NULL")
    List<ReportEntity> getAllPostReports();

    @Query("SELECT r FROM ReportEntity r WHERE r.post IS NULL")
    List<ReportEntity> getAllCommentReports();

    @Query("SELECT r FROM ReportEntity r WHERE r.author.id = :userId AND r.reason = :reason AND r.post.id = :postId")
    Optional<ReportEntity> getExistingPostReportByUserAndReason(@Param("userId") Long userId,
                                                                @Param("reason") ReportReason reason,
                                                                @Param("postId") Long postId);

    @Query("""
            SELECT r FROM ReportEntity r
            WHERE r.author.id = :userId
            AND r.reason = :reason
            AND r.comment.id = :commentId
            """)
    Optional<ReportEntity> getExistingCommentReportByUserAndReason(@Param("userId") Long userId,
                                                                   @Param("reason") ReportReason reason,
                                                                   @Param("commentId") Long commentId);

    @Modifying
    @Query("""
            UPDATE ReportEntity r
            SET r.solved = :newStatus
            WHERE r.post.id = :postId
            AND r.reason = :reason
            """)
    void setSolvedByPostByReason(@Param("postId") Long postId,
                                 @Param("reason") ReportReason reason,
                                 @Param("newStatus") boolean newStatus);

    @Modifying
    @Query("""
            UPDATE ReportEntity r
            SET r.solved = :newStatus
            WHERE r.comment.id = :commentId
            AND r.reason = :reason
            """)
    void setSolvedByCommentByReason(@Param("commentId") Long commentId,
                                    @Param("reason") ReportReason reason,
                                    @Param("newStatus") boolean newStatus);
}
