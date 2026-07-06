package com.bnpaper.agento.content;

import com.bnpaper.agento.content.dto.ContentBriefResponse;
import com.bnpaper.agento.content.dto.CreateContentBriefRequest;
import com.bnpaper.agento.content.dto.GeneratedContentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping("/briefs")
    @ResponseStatus(HttpStatus.CREATED)
    public ContentBriefResponse createBrief(@Valid @RequestBody CreateContentBriefRequest request) {
        return contentService.createBrief(request);
    }

    @GetMapping("/briefs")
    public List<ContentBriefResponse> listBriefs() {
        return contentService.listBriefs();
    }

    @GetMapping("/briefs/{id}")
    public ContentBriefResponse getBrief(@PathVariable Long id) {
        return contentService.getBrief(id);
    }

    @PostMapping("/briefs/{id}/generate")
    public GeneratedContentResponse generate(@PathVariable Long id) {
        return contentService.generate(id);
    }

    @GetMapping("/generated/{id}")
    public GeneratedContentResponse getGenerated(@PathVariable Long id) {
        return contentService.getGenerated(id);
    }
}
