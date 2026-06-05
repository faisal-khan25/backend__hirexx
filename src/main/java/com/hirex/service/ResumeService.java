package com.hirex.service;

import com.hirex.dto.ResumeDto;
import com.hirex.entity.Resume;
import com.hirex.entity.User;
import com.hirex.repository.ResumeRepository;
import com.hirex.repository.UserRepository;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository   userRepository;

    @Value("${resume.upload.dir:uploads/resumes}")
    private String uploadDir;

    public ResumeService(ResumeRepository resumeRepository, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository   = userRepository;
    }

    // ─────────────────────── UPLOAD ────────────────────────────────────

    public Resume uploadResume(MultipartFile file, String userEmail) throws IOException {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Path dirPath = Paths.get(uploadDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String originalName = file.getOriginalFilename();
        String extension    = getExtension(originalName);
        String savedName    = UUID.randomUUID() + "." + extension;
        Path   targetPath   = dirPath.resolve(savedName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String resumeText = extractText(file, extension);

        Resume resume = resumeRepository.findByUser(user).orElse(new Resume());
        resume.setUser(user);
        resume.setFileName(originalName);
        resume.setFilePath(targetPath.toString());
        resume.setResumeText(resumeText);

        return resumeRepository.save(resume);
    }

    // ─────────────────────── QUERY ─────────────────────────────────────

    public Resume getByUserId(Long userId) {
        return resumeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Resume not found for user " + userId));
    }

    public Resume getById(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found: " + resumeId));
    }

    public Resume getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return resumeRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No resume uploaded yet"));
    }

    public List<ResumeDto> getAllResumes() {
        return resumeRepository.findAllWithUser().stream()
                .map(r -> {
                    ResumeDto dto = new ResumeDto();
                    dto.setResumeId(r.getId());
                    dto.setUserId(r.getUser().getId());
                    dto.setCandidateName(r.getUser().getName());
                    dto.setCandidateEmail(r.getUser().getEmail());
                    dto.setFileName(r.getFileName());
                    dto.setFilePath(r.getFilePath());
                    dto.setUploadedAt(r.getUploadedAt());
                    dto.setHasText(r.getResumeText() != null && !r.getResumeText().isBlank());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ─────────────────────── DOWNLOAD ──────────────────────────────────

    public byte[] downloadFile(Long resumeId) throws IOException {
        Resume resume = getById(resumeId);
        File file = new File(resume.getFilePath());
        if (!file.exists()) {
            throw new RuntimeException("Resume file not found on disk");
        }
        return Files.readAllBytes(file.toPath());
    }

    // ─────────────────────── TEXT EXTRACTION ───────────────────────────

    private String extractText(MultipartFile file, String extension) {
        try {
            return switch (extension.toLowerCase()) {
                case "pdf"  -> extractFromPdf(file);
                case "docx" -> extractFromDocx(file);
                default     -> new String(file.getBytes());
            };
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(file.getInputStream())) {
            StringBuilder text = new StringBuilder();
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
            return text.toString();
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "bin";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
