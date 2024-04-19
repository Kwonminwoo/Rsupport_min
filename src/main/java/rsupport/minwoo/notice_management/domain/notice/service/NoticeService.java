package rsupport.minwoo.notice_management.domain.notice.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.minwoo.notice_management.domain.attachedFile.service.AttachedFileService;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.domain.member.repository.MemberRepository;
import rsupport.minwoo.notice_management.domain.notice.dto.CreateNoticeRequest;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;
import rsupport.minwoo.notice_management.domain.notice.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final AttachedFileService attachedFileService;

    @Transactional
    public void createNotice(CreateNoticeRequest createNoticeRequest, List<MultipartFile> attachedFileList) {
        Member currentMember = memberRepository.findById(createNoticeRequest.getMemberId())
            .orElseThrow(RuntimeException::new);

        Notice notice = Notice.builder()
            .title(createNoticeRequest.getTitle())
            .content(createNoticeRequest.getContent())
            .startDateTime(createNoticeRequest.getStartDateTime())
            .endDateTime(createNoticeRequest.getEndDateTime())
            .member(currentMember)
            .build();

        noticeRepository.save(notice);

        validateDuplicateFileName(attachedFileList);

        for (MultipartFile file : attachedFileList) {
            attachedFileService.saveAttachedFile(notice, file);
        }
    }

    private void validateDuplicateFileName(List<MultipartFile> fileList) {
        Set<String> fileNames = new HashSet<>();

        for (MultipartFile file : fileList) {
            if(fileNames.contains(file.getOriginalFilename())) {
                throw new RuntimeException("중복된 파일 이름입니다.");
            }
            fileNames.add(file.getOriginalFilename());
        }
    }
}
