package com.research.notes.models.entities;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.Set;
import java.util.UUID;
import java.sql.Types;
import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue
    @JdbcTypeCode(Types.VARCHAR)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdTime;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<QuestionEntity> createdQuestions;

}