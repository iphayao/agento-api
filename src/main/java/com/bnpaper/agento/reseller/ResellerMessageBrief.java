package com.bnpaper.agento.reseller;

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
@Table(name = "reseller_message_briefs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResellerMessageBrief {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "reseller_lead_id", nullable = false)
    private Long resellerLeadId;

    @Column(name = "message_goal", nullable = false)
    private String messageGoal;

    private String tone;

    private String product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BriefStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
