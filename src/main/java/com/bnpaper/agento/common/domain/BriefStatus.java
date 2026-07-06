package com.bnpaper.agento.common.domain;

/**
 * Lifecycle of any generation brief (content, campaign, reseller message).
 */
public enum BriefStatus {
    NEW,
    GENERATING,
    GENERATED,
    FAILED
}
