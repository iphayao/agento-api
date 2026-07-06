package com.bnpaper.agento.content;

import com.bnpaper.agento.content.dto.ContentBriefResponse;
import com.bnpaper.agento.content.dto.GeneratedContentResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ContentMapper {

    ContentBriefResponse toResponse(ContentBrief brief);

    GeneratedContentResponse toResponse(GeneratedContent content);
}
