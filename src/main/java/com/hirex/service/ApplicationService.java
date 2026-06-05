package com.hirex.service;

import com.hirex.entity.*;
import com.hirex.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository appRepo;
    private final JobRepository jobRepo;
    private final UserRepository userRepo;

    public ApplicationService(ApplicationRepository appRepo, JobRepository jobRepo, UserRepository userRepo) {
        this.appRepo = appRepo;
        this.jobRepo = jobRepo;
        this.userRepo = userRepo;
    }

    public String apply(Long jobId, String coverLetter, String applicantEmail) {
        User applicant = userRepo.findByEmail(applicantEmail).orElseThrow();
        Job job = jobRepo.findById(jobId).orElseThrow();

        if (appRepo.existsByJobAndApplicant(job, applicant)) {
            return "Already applied to this job";
        }

        Application app = new Application();
        app.setJob(job);
        app.setApplicant(applicant);
        app.setCoverLetter(coverLetter);
        appRepo.save(app);
        return "Applied successfully";
    }

    public List<AppResponse> getMyApplications(String email) {
        User user = userRepo.findByEmail(email).orElseThrow();
        return appRepo.findByApplicant(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<AppResponse> getApplicantsForJob(Long jobId, String managerEmail) {
        Job job = jobRepo.findById(jobId).orElseThrow();
        return appRepo.findByJob(job).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public AppResponse updateStatus(Long appId, String status, String managerEmail) {
        Application app = appRepo.findById(appId).orElseThrow();
        app.setStatus(ApplicationStatus.valueOf(status));
        return toResponse(appRepo.save(app));
    }

    private AppResponse toResponse(Application app) {
        AppResponse r = new AppResponse();
        r.setId(app.getId());
        r.setJobTitle(app.getJob().getTitle());
        r.setJobId(app.getJob().getId());
        r.setCompanyName(app.getJob().getCompany().getName());
        r.setApplicantName(app.getApplicant().getName());
        r.setApplicantEmail(app.getApplicant().getEmail());
        r.setStatus(app.getStatus().name());
        r.setCoverLetter(app.getCoverLetter());
        r.setAppliedAt(app.getAppliedAt() != null ? app.getAppliedAt().toString() : "");
        return r;
    }

    // simple response class - no lombok
    public static class AppResponse {
        private Long id;
        private Long jobId;
        private String jobTitle;
        private String companyName;
        private String applicantName;
        private String applicantEmail;
        private String status;
        private String coverLetter;
        private String appliedAt;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }

        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }

        public String getApplicantName() { return applicantName; }
        public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

        public String getApplicantEmail() { return applicantEmail; }
        public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getCoverLetter() { return coverLetter; }
        public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

        public String getAppliedAt() { return appliedAt; }
        public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }
    }
}
