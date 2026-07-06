package com.bnpaper.agento.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GeneratedContentRepository extends JpaRepository<GeneratedContent, Long> {

    long countByIsSafeFalse();

    @Query("select avg(g.qualityScore) from GeneratedContent g where g.qualityScore is not null")
    Double averageQualityScore();
}
