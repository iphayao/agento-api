package com.bnpaper.agento.campaign;

import com.bnpaper.agento.common.domain.BriefStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "campaign_briefs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignBrief {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(nullable = false)
    private String objective;

    private String channel;

    private String audience;

    private String product;

    @Column(name = "duration_weeks", nullable = false)
    private Integer durationWeeks;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BriefStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
