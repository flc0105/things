package flc.things.controller;

import flc.things.entity.Attachment;
import flc.things.entity.Item;
import flc.things.service.AttachmentService;
import flc.things.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;


    @GetMapping
    public List<Attachment> getAllAttachments() {
        return attachmentService.getAllAttachments();
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadAttachment(@PathVariable Long id) throws IOException {
        return attachmentService.downloadAttachment(id);
    }


    /**
     * 上传附件
     * @param file 上传的文件
     * @return ResponseEntity
     */
    @PostMapping
    public ResponseEntity<?> uploadAttachment(@RequestParam("file") MultipartFile file) {
        try {
            Attachment attachment = attachmentService.saveAttachment(file);
            return ResponseEntity.ok().body(attachment);
        } catch (Exception e) {
            // 处理异常，比如文件保存失败等
            return ResponseEntity.badRequest().body("Failed to upload attachment: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attachment> getAttachmentById(@PathVariable Long id) {
        Optional<Attachment> attachment = Optional.ofNullable(attachmentService.getAttachmentById(id));
        return attachment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
