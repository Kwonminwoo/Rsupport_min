package rsupport.minwoo.notice_management.domain.attachedFile.service;

import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.minwoo.notice_management.domain.attachedFile.entity.AttachedFile;
import rsupport.minwoo.notice_management.domain.attachedFile.respository.AttachedFileRepository;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

@RequiredArgsConstructor
@Service
public class AttachedFileService {

    @Value("${spring.file.save.path}")
    private String basePath;

    private final AttachedFileRepository attachedFileRepository;

    @Transactional
    public AttachedFile saveAttachedFile(Notice notice, MultipartFile file) {
        String saveFilePath = basePath + notice.getTitle() + "/";
        File saveDirectory = new File(saveFilePath);

        try {
            regenerateDirectory(saveDirectory);

            File saveFile = new File(saveDirectory, file.getOriginalFilename());
            file.transferTo(saveFile);
        } catch (IOException e) {
            System.err.println(e);
            throw new RuntimeException("저장 실패");
        }

        AttachedFile saveFile = AttachedFile.builder()
            .title(file.getOriginalFilename())
            .type(file.getContentType())
            .notice(notice)
            .filePath(saveFilePath)
            .build();

        notice.addAttachedFile(saveFile);

        return saveFile;
    }

    private void regenerateDirectory(File directory) throws IOException {
        FileUtils.cleanDirectory(directory);
        directory.mkdirs();
    }
}
