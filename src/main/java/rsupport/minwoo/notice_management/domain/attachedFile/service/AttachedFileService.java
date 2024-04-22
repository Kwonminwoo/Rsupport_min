package rsupport.minwoo.notice_management.domain.attachedFile.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.minwoo.notice_management.domain.attachedFile.entity.AttachedFile;
import rsupport.minwoo.notice_management.domain.attachedFile.exception.FileDeleteFailException;
import rsupport.minwoo.notice_management.domain.attachedFile.exception.FileSaveFailException;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;
import rsupport.minwoo.notice_management.global.exception.ErrorCode;

@Service
public class AttachedFileService {

    private final String basePath;

    public AttachedFileService(@Value("${spring.file.save.path}") String basePath) {
        this.basePath = basePath;
    }

    @Transactional
    public void saveAttachedFile(Notice notice, List<MultipartFile> fileList) {
        String saveFilePath = basePath + notice.getTitle() + "/";
        File saveDirectory = getDirectoryFile(notice.getTitle());
        try {
            saveDirectory.mkdirs();
            for (MultipartFile file : fileList) {

                File saveFile = new File(saveDirectory, file.getOriginalFilename());
                file.transferTo(saveFile);

                notice.addAttachedFile(
                    AttachedFile.builder()
                        .title(file.getOriginalFilename())
                        .type(file.getContentType())
                        .notice(notice)
                        .filePath(saveFilePath)
                        .build()
                );
            }
        } catch (IOException e) {
            throw new FileSaveFailException(ErrorCode.FILE_SAVE_FAIL);
        }
    }

    public void deleteAttachedFile(String title) {
        File targetFileDirectory = getDirectoryFile(title);

        if (targetFileDirectory.exists()) {
            try {
                FileUtils.cleanDirectory(targetFileDirectory);
                targetFileDirectory.delete();
            } catch (IOException e) {
                throw new FileDeleteFailException(ErrorCode.FILE_DELETE_FAIL);
            }
        }
    }

    private File getDirectoryFile(String title) {
        String targetFilePath = basePath + title + "/";
        return new File(targetFilePath);
    }

    @Transactional
    public void updateAttachedFile(String beforeNoticeTitle, Notice notice, List<MultipartFile> fileList) {
        deleteAttachedFile(beforeNoticeTitle);
        notice.removeAllAttachedFile();

        saveAttachedFile(notice, fileList);
    }
}
