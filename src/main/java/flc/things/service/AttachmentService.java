package flc.things.service;

import flc.things.entity.Attachment;
import flc.things.exceptions.BusinessException;
import flc.things.mapper.AttachmentMapper;
import flc.things.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Value("${file.upload.path}")
    private String uploadPath;

    public Attachment uploadAttachment(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file.");
        }
        String originalFileName = file.getOriginalFilename(); // 获取原始文件名
        String randomFileName = String.valueOf(UUID.randomUUID()); // 生成随机文件名
        Path destinationFile = Paths.get(uploadPath, randomFileName); // 定义文件存储路径
        // 保存文件到服务器
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        // 创建Attachment实体并保存到数据库
        Attachment attachment = new Attachment();
        attachment.setFilePath(FileUtil.relativize(uploadPath, destinationFile.toFile())); // 设置文件存储路径
        attachment.setOriginalFileName(originalFileName); // 设置原始文件名
        attachmentMapper.insert(attachment);
        return attachment;
    }

    public void deleteAttachment(Long id) {
        Attachment attachment = getAttachmentById(id);
        Path path = Paths.get(uploadPath, attachment.getFilePath());
        try {
            boolean isDeleted = Files.deleteIfExists(path);
            if (isDeleted) {
                attachmentMapper.deleteById(id);
            }
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public ResponseEntity<InputStreamResource> downloadAttachment(Long id) throws IOException {
        // 根据文件ID获取Attachment实体
        Attachment attachment = getAttachmentById(id);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }
        // 构建文件路径
        Path path = Paths.get(uploadPath, attachment.getFilePath());
        // 检查文件是否存在
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }
        // 创建InputStreamResource
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
        // 设置响应头信息
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getOriginalFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 返回ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(Files.size(path))
                .body(resource);
    }

    public Attachment getAttachmentById(Long id) {
        return attachmentMapper.selectById(id);
    }
}


