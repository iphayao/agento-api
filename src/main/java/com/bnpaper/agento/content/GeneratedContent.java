package com.bnpaper.agento.content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "generated_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_brief_id", nullable = false)
    private Long contentBriefId;

    @Column(columnDefinition = "text")
    private String output;

    @Column(name = "quality_score")
    private Double qualityScore;

    @Column(name = "is_safe")
    private Boolean isSafe;

    private String provider;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
