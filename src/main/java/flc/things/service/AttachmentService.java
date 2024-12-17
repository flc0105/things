package flc.things.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import flc.things.entity.Attachment;
import flc.things.entity.Item;
import flc.things.mapper.AttachmentMapper;
import flc.things.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;


    @Value("${file.upload.path}")
    private String uploadPath;


    public Attachment saveAttachment(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file.");
        }

        // 获取原始文件名
        String originalFileName = file.getOriginalFilename();
        // 获取文件扩展名
        String fileExtension = Optional.ofNullable(originalFileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFileName.lastIndexOf(".")))
                .orElse("");

        // 生成随机文件名
        String randomFileName = UUID.randomUUID() + fileExtension;

        // 定义文件存储路径
        Path destinationFile = Paths.get(uploadPath, randomFileName);

        // 保存文件到服务器
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }

        // 创建Attachment实体并保存到数据库
        Attachment attachment = new Attachment();
//        attachment.setItemId(itemId); // 设置关联的物品ID
        attachment.setFilePath(destinationFile.toString()); // 设置文件存储路径
        attachment.setOriginalFileName(originalFileName); // 设置原始文件名
        // ... 省略其他属性设置和数据库保存操作
        attachmentMapper.insert(attachment);

        return attachment;
    }

    public ResponseEntity<InputStreamResource> downloadAttachment(Long id) throws IOException {
        // 根据文件ID获取Attachment实体
        Attachment attachment = getAttachmentById(id);
        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        // 构建文件路径
        Path path = Paths.get(attachment.getFilePath());

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
//        QueryWrapper<Attachment> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("item_id", itemId); // 查询条件：item_id等于传入的itemId
//        return attachmentMapper.selectList(queryWrapper); // 使用MyBatis-Plus的list方法执行查询
    }

    public List<Attachment> getAllAttachments() {
        return attachmentMapper.selectList(null);
    }
}


