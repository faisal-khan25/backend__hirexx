package com.hirex.repository;

import com.hirex.entity.Application;
import com.hirex.entity.Job;
import com.hirex.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByApplicant(User user);
    List<Application> findByJob(Job job);
    List<Application> findByJobIn(List<Job> jobs);
    boolean existsByJobAndApplicant(Job job, User user);
    Optional<Application> findByJobAndApplicant(Job job, User user);

    // for admin dashboard - count per company
    @Query("SELECT a.job.company.name, COUNT(a) FROM Application a GROUP BY a.job.company.name")
    List<Object[]> countApplicationsPerCompany();
}
