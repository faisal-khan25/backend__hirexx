//package com.hirex.repository;
//
//import com.hirex.entity.Application;
//import com.hirex.entity.Job;
//import com.hirex.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import java.util.List;
//import java.util.Optional;
//
//public interface ApplicationRepository extends JpaRepository<Application, Long> {
//    List<Application> findByApplicant(User user);
//    List<Application> findByJob(Job job);
//    List<Application> findByJobIn(List<Job> jobs);
//    boolean existsByJobAndApplicant(Job job, User user);
//    Optional<Application> findByJobAndApplicant(Job job, User user);
//
//    // for admin dashboard - count per company
//    @Query("SELECT a.job.company.name, COUNT(a) FROM Application a GROUP BY a.job.company.name")
//    List<Object[]> countApplicationsPerCompany();
//}
package com.hirex.repository;

import com.hirex.entity.Application;
import com.hirex.entity.ApplicationStatus;
import com.hirex.entity.Job;
import com.hirex.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByApplicant(User user);
    List<Application> findByJob(Job job);
    List<Application> findByJobIn(List<Job> jobs);
    boolean existsByJobAndApplicant(Job job, User user);
    Optional<Application> findByJobAndApplicant(Job job, User user);

    // for admin dashboard – count per company
    @Query("SELECT a.job.company.name, COUNT(a) FROM Application a GROUP BY a.job.company.name")
    List<Object[]> countApplicationsPerCompany();

    // ── Bulk ATS additions ──────────────────────────────────────────────

    /** Count all applications with a given status */
    long countByStatus(ApplicationStatus status);

    /** Count total applications across all jobs for a manager's company */
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.company.id = :companyId")
    long countByCompanyId(@Param("companyId") Long companyId);

    /** Count by status for a specific company */
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.company.id = :companyId AND a.status = :status")
    long countByCompanyIdAndStatus(@Param("companyId") Long companyId,
                                   @Param("status") ApplicationStatus status);

    /** Find all applications for jobs belonging to a company */
    @Query("SELECT a FROM Application a JOIN FETCH a.applicant JOIN FETCH a.job WHERE a.job.company.id = :companyId")
    List<Application> findByCompanyId(@Param("companyId") Long companyId);

    /** Find a candidate's latest application (any job) by user id */
    @Query("SELECT a FROM Application a WHERE a.applicant.id = :userId ORDER BY a.appliedAt DESC")
    List<Application> findByApplicantId(@Param("userId") Long userId);

    /** Bulk-update status by applicant user id list (for a specific job) */
    @Modifying
    @Query("UPDATE Application a SET a.status = :status WHERE a.applicant.id = :userId AND a.job.id = :jobId")
    int updateStatusByUserAndJob(@Param("userId") Long userId,
                                 @Param("jobId")  Long jobId,
                                 @Param("status") ApplicationStatus status);
}

