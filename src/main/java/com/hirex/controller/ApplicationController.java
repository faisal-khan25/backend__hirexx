package com.hirex.controller;

import com.hirex.service.ApplicationService;
import com.hirex.service.ApplicationService.AppResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService appService;

    public ApplicationController(ApplicationService appService) {
        this.appService = appService;
    }

    @PostMapping("/jobseeker/apply/{jobId}")
    public ResponseEntity<String> apply(@PathVariable Long jobId,
                                         @RequestBody ApplyRequest req,
                                         Principal principal) {
        return ResponseEntity.ok(appService.apply(jobId, req.getCoverLetter(), principal.getName()));
    }

    @GetMapping("/jobseeker/applications")
    public ResponseEntity<List<AppResponse>> myApplications(Principal principal) {
        return ResponseEntity.ok(appService.getMyApplications(principal.getName()));
    }

    @GetMapping("/manager/jobs/{jobId}/applicants")
    public ResponseEntity<List<AppResponse>> applicants(@PathVariable Long jobId, Principal principal) {
        return ResponseEntity.ok(appService.getApplicantsForJob(jobId, principal.getName()));
    }

    @PutMapping("/manager/applications/{appId}/status")
    public ResponseEntity<AppResponse> updateStatus(@PathVariable Long appId,
                                                     @RequestBody StatusRequest req,
                                                     Principal principal) {
        return ResponseEntity.ok(appService.updateStatus(appId, req.getStatus(), principal.getName()));
    }

    // inner request classes without lombok
    public static class ApplyRequest {
        private String coverLetter;
        public String getCoverLetter() { return coverLetter; }
        public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    }

    public static class StatusRequest {
        private String status;
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
