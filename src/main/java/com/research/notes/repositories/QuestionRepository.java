package com.research.notes.repositories;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.research.notes.models.entities.QuestionEntity;

public interface QuestionRepository extends JpaRepository<QuestionEntity, UUID> {

    // reads the created_by FK column directly, so no join to users is emitted
    @Query("select q from QuestionEntity q where q.createdBy.id = :userId")
    Page<QuestionEntity> findByCreatedById(@Param("userId") UUID userId, Pageable pageable);
}
