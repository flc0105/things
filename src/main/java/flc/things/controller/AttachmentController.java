package flc.things.controller;

import flc.things.entity.Attachment;
import flc.things.exceptions.BusinessException;
import flc.things.service.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {
    @Autowired
    private AttachmentService attachmentService;

    @PostMapping
    public ResponseEntity<?> uploadAttachment(@RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = attachmentService.uploadAttachment(file);
            return ResponseEntity.ok().body(attachment);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload attachment: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
        try {
        attachmentService.deleteAttachment(id);
            return ResponseEntity.ok().build();
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body("Failed to delete attachment: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> downloadAttachment(@PathVariable Long id) throws IOException {
        return attachmentService.downloadAttachment(id);
    }
}
