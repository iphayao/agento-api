package com.bnpaper.agento.reseller;

import com.bnpaper.agento.reseller.dto.CreateResellerLeadRequest;
import com.bnpaper.agento.reseller.dto.CreateResellerMessageBriefRequest;
import com.bnpaper.agento.reseller.dto.GeneratedResellerMessageResponse;
import com.bnpaper.agento.reseller.dto.ResellerLeadResponse;
import com.bnpaper.agento.reseller.dto.ResellerMessageBriefResponse;
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
@RequestMapping("/api/v1/resellers")
@RequiredArgsConstructor
public class ResellerController {

    private final ResellerService resellerService;

    @PostMapping("/leads")
    @ResponseStatus(HttpStatus.CREATED)
    public ResellerLeadResponse createLead(@Valid @RequestBody CreateResellerLeadRequest request) {
        return resellerService.createLead(request);
    }

    @GetMapping("/leads")
    public List<ResellerLeadResponse> listLeads() {
        return resellerService.listLeads();
    }

    @GetMapping("/leads/{id}")
    public ResellerLeadResponse getLead(@PathVariable Long id) {
        return resellerService.getLead(id);
    }

    @PostMapping("/message-briefs")
    @ResponseStatus(HttpStatus.CREATED)
    public ResellerMessageBriefResponse createMessageBrief(
            @Valid @RequestBody CreateResellerMessageBriefRequest request) {
        return resellerService.createMessageBrief(request);
    }

    @PostMapping("/message-briefs/{id}/generate")
    public GeneratedResellerMessageResponse generate(@PathVariable Long id) {
        return resellerService.generate(id);
    }
}
