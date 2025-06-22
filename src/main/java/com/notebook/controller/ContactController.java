package com.notebook.controller;

import com.notebook.ContactSubmission;
import com.notebook.ContactSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Optional;

@Controller
public class ContactController {
    
    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);
    
    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;

    // Test endpoint to verify database functionality
    @GetMapping("/test-db")
    @ResponseBody
    public String testDatabase() {
        try {
            logger.info("Testing database connectivity...");
            
            // Create a test submission
            ContactSubmission testSubmission = new ContactSubmission();
            testSubmission.setName("Test User");
            testSubmission.setEmail("test@example.com");
            testSubmission.setPhone("1234567890");
            testSubmission.setSubject("Database Test");
            testSubmission.setMessage("This is a test message to verify database functionality");
            
            logger.info("Saving test submission to database...");
            ContactSubmission saved = contactSubmissionRepository.save(testSubmission);
            logger.info("Test submission saved with ID: {}", saved.getId());
            
            // Count total submissions
            long count = contactSubmissionRepository.count();
            logger.info("Total submissions in database: {}", count);
            
            return String.format("Database test successful! Test submission saved with ID: %d. Total submissions: %d", 
                               saved.getId(), count);
                               
        } catch (Exception e) {
            logger.error("Database test failed: {}", e.getMessage(), e);
            return "Database test failed: " + e.getMessage();
        }
    }

    // File download endpoint
    @GetMapping("/download-file/{submissionId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long submissionId) {
        try {
            Optional<ContactSubmission> submissionOpt = contactSubmissionRepository.findById(submissionId);
            
            if (submissionOpt.isEmpty()) {
                logger.warn("File download requested for non-existent submission: {}", submissionId);
                return ResponseEntity.notFound().build();
            }
            
            ContactSubmission submission = submissionOpt.get();
            
            if (submission.getFileData() == null || submission.getFileData().length == 0) {
                logger.warn("File download requested for submission without file data: {}", submissionId);
                return ResponseEntity.notFound().build();
            }
            
            // Set up headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(submission.getFileType()));
            headers.setContentDispositionFormData("attachment", submission.getFileName());
            headers.setContentLength(submission.getFileData().length);
            
            logger.info("File download successful for submission {}: {} ({} bytes)", 
                       submissionId, submission.getFileName(), submission.getFileData().length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(submission.getFileData());
                    
        } catch (Exception e) {
            logger.error("File download failed for submission {}: {}", submissionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // List all submissions with file info
    @GetMapping("/submissions")
    @ResponseBody
    public String listSubmissions() {
        try {
            StringBuilder result = new StringBuilder();
            result.append("<h2>All Contact Submissions</h2>");
            result.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            result.append("<tr><th>ID</th><th>Name</th><th>Email</th><th>Subject</th><th>File Name</th><th>File Size</th><th>Download</th></tr>");
            
            for (ContactSubmission submission : contactSubmissionRepository.findAll()) {
                result.append("<tr>");
                result.append("<td>").append(submission.getId()).append("</td>");
                result.append("<td>").append(submission.getName()).append("</td>");
                result.append("<td>").append(submission.getEmail()).append("</td>");
                result.append("<td>").append(submission.getSubject()).append("</td>");
                
                if (submission.getFileData() != null && submission.getFileData().length > 0) {
                    result.append("<td>").append(submission.getFileName()).append("</td>");
                    result.append("<td>").append(submission.getFileData().length).append(" bytes</td>");
                    result.append("<td><a href='/download-file/").append(submission.getId()).append("'>Download</a></td>");
                } else {
                    result.append("<td>No file</td>");
                    result.append("<td>-</td>");
                    result.append("<td>-</td>");
                }
                
                result.append("</tr>");
            }
            
            result.append("</table>");
            return result.toString();
            
        } catch (Exception e) {
            logger.error("Failed to list submissions: {}", e.getMessage(), e);
            return "Error listing submissions: " + e.getMessage();
        }
    }

    @PostMapping("/contact/submit")
    public String handleContactForm(
        @RequestParam("fullName") String fullName,
        @RequestParam("email") String email,
        @RequestParam("phone") String phone,
        @RequestParam("subject") String subject,
        @RequestParam("message") String message,
        @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        logger.info("=== Starting contact form submission ===");
        logger.info("Received contact form submission from: {}", email);
        logger.info("Form data - Name: {}, Phone: {}, Subject: {}", fullName, phone, subject);
        
        try {
            // Step 1: Basic validation
            logger.info("Step 1: Validating basic form fields...");
            if (fullName == null || fullName.trim().isEmpty()) {
                logger.warn("Contact form submission failed: Full name is required");
                return "redirect:/?error=Full+Name+is+required.#contact";
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                logger.warn("Contact form submission failed: Invalid email - {}", email);
                return "redirect:/?error=A+valid+email+is+required.#contact";
            }
            if (phone == null || phone.trim().isEmpty()) {
                logger.warn("Contact form submission failed: Phone number is required");
                return "redirect:/?error=Phone+number+is+required.#contact";
            }
            if (subject == null || subject.trim().isEmpty()) {
                logger.warn("Contact form submission failed: Subject is required");
                return "redirect:/?error=Subject+is+required.#contact";
        }
        if (message == null || message.trim().isEmpty()) {
                logger.warn("Contact form submission failed: Message is required");
                return "redirect:/?error=Message+is+required.#contact";
        }
            logger.info("Step 1: Basic validation passed");
            
            // Step 2: File validation (if present)
        if (file != null && !file.isEmpty()) {
                logger.info("Step 2: Processing file upload...");
                logger.info("File details - Name: {}, Size: {} bytes, Content-Type: {}", 
                           file.getOriginalFilename(), file.getSize(), file.getContentType());
                
            String fileType = file.getContentType();
            long fileSize = file.getSize();
                
                // Check file type
                if (fileType == null) {
                    logger.warn("File upload failed: Unknown file type for {}", file.getOriginalFilename());
                    return "redirect:/?error=Unable+to+determine+file+type.+Please+try+again.#contact";
                }
                
                if (!(fileType.startsWith("image/") || fileType.equals("application/pdf"))) {
                    logger.warn("File upload failed: Invalid file type {} for {}", fileType, file.getOriginalFilename());
                    return "redirect:/?error=Only+image+or+PDF+files+are+allowed.+Received:+type.#contact".replace("type", fileType);
                }
                
                // Check file size (10MB limit)
                if (fileSize > 10 * 1024 * 1024) {
                    logger.warn("File upload failed: File too large {} bytes for {}", fileSize, file.getOriginalFilename());
                    return "redirect:/?error=File+size+must+be+less+than+10MB.+Received:+size+MB.#contact"
                           .replace("size", String.valueOf(fileSize / (1024 * 1024)));
                }
                
                // Check if file is actually readable
                try {
                    byte[] fileBytes = file.getBytes();
                    if (fileBytes.length == 0) {
                        logger.warn("File upload failed: Empty file {}", file.getOriginalFilename());
                        return "redirect:/?error=The+uploaded+file+appears+to+be+empty.+Please+try+again.#contact";
                    }
                    logger.info("File content read successfully: {} bytes", fileBytes.length);
                } catch (IOException e) {
                    logger.error("Failed to read file content: {}", e.getMessage());
                    return "redirect:/?error=Unable+to+read+file+content.+Please+try+again.#contact";
                }
                logger.info("Step 2: File validation passed");
            } else {
                logger.info("Step 2: No file uploaded, skipping file validation");
            }
            
            // Step 3: Create submission object
            logger.info("Step 3: Creating submission object...");
            ContactSubmission submission = new ContactSubmission();
            submission.setName(fullName.trim());
            submission.setEmail(email.trim());
            submission.setPhone(phone.trim());
            submission.setSubject(subject.trim());
            submission.setMessage(message.trim());
            logger.info("Step 3: Submission object created successfully");
            
            // Step 4: Handle file data (if present)
            if (file != null && !file.isEmpty()) {
                logger.info("Step 4: Processing file data...");
                try {
                submission.setFileName(file.getOriginalFilename());
                submission.setFileType(file.getContentType());
                    
                    // Read file bytes with error handling
                    byte[] fileBytes = file.getBytes();
                    submission.setFileData(fileBytes);
                    
                    logger.info("File successfully attached: {} ({} bytes)", file.getOriginalFilename(), fileBytes.length);
                    logger.info("Step 4: File data processed successfully");
                } catch (IOException e) {
                    logger.error("Failed to process file data: {}", e.getMessage(), e);
                    return "redirect:/?error=Failed+to+process+file+data.+Please+try+again.#contact";
                } catch (Exception e) {
                    logger.error("Unexpected error processing file: {}", e.getMessage(), e);
                    return "redirect:/?error=Unexpected+error+processing+file.+Please+try+again.#contact";
                }
            } else {
                logger.info("Step 4: No file data to process");
            }
            
            // Step 5: Save to database
            logger.info("Step 5: Saving submission to database...");
            try {
                contactSubmissionRepository.save(submission);
                logger.info("Contact form submission saved successfully for: {}", email);
                logger.info("=== Contact form submission completed successfully ===");
                return "redirect:/?success#contact";
            } catch (Exception e) {
                logger.error("Database save failed: {}", e.getMessage(), e);
                
                // Try to save without file data if database save fails
                if (file != null && !file.isEmpty()) {
                    logger.info("Attempting to save without file data...");
                    try {
                        submission.setFileName(null);
                        submission.setFileType(null);
                        submission.setFileData(null);
            contactSubmissionRepository.save(submission);
                        logger.info("Submission saved successfully without file data");
                        return "redirect:/?error=Message+sent+but+file+upload+failed.+We+received+your+message+without+the+attachment.#contact";
                    } catch (Exception e2) {
                        logger.error("Failed to save even without file data: {}", e2.getMessage());
                        return "redirect:/?error=Database+error.+Please+try+again+later+or+contact+us+directly.#contact";
                    }
                } else {
                    return "redirect:/?error=Database+error.+Please+try+again+later+or+contact+us+directly.#contact";
                }
            }
            
        } catch (Exception e) {
            logger.error("=== Contact form submission failed ===");
            logger.error("Error processing contact form submission from {}: {}", email, e.getMessage(), e);
            
            // Provide more specific error messages based on the exception type
            String errorMessage;
            if (e instanceof org.springframework.web.multipart.MaxUploadSizeExceededException) {
                errorMessage = "File+size+exceeds+the+maximum+allowed+limit+of+10MB.";
            } else if (e instanceof org.springframework.web.multipart.MultipartException) {
                errorMessage = "File+upload+failed.+Please+check+the+file+and+try+again.";
            } else if (e instanceof IOException) {
                errorMessage = "File+reading+error.+Please+try+again+with+a+different+file.";
            } else if (e.getMessage() != null && e.getMessage().contains("database")) {
                errorMessage = "Database+error.+Please+try+again+later+or+contact+us+directly.";
            } else {
                errorMessage = "There+was+a+problem+submitting+your+form.+Please+try+again+or+contact+us+by+email.";
            }
            
            return "redirect:/?error=" + errorMessage + "#contact";
        }
    }
} 