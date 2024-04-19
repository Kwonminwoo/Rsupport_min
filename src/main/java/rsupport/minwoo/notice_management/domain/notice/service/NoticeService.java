package rsupport.minwoo.notice_management.domain.notice.service;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rsupport.minwoo.notice_management.domain.attachedFile.service.AttachedFileService;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.domain.member.repository.MemberRepository;
import rsupport.minwoo.notice_management.domain.notice.dto.request.CreateNoticeRequest;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindAllNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;
import rsupport.minwoo.notice_management.domain.notice.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final AttachedFileService attachedFileService;

    @Transactional
    public void createNotice(CreateNoticeRequest createNoticeRequest,
        List<MultipartFile> attachedFileList) {
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
            if (fileNames.contains(file.getOriginalFilename())) {
                throw new RuntimeException("중복된 파일 이름입니다.");
            }
            fileNames.add(file.getOriginalFilename());
        }
    }

    public FindAllNoticeResponse findAllNotice(Pageable pageable) {
        Page<Notice> findNoticePage = noticeRepository.findAll(pageable);

        List<FindNoticeResponse> noticeResponseList = findNoticePage.stream()
            .map(n ->
                FindNoticeResponse.builder()
                    .title(n.getTitle())
                    .content(n.getContent())
                    .postingDateTime(n.getCreatedAt())
                    .views(n.getViews())
                    .author(n.getMember().getName())
                    .build()
            )
            .collect(Collectors.toList());

        return FindAllNoticeResponse.builder()
            .noticeResponseList(noticeResponseList)
            .totalPage(findNoticePage.getTotalPages())
            .totalCount(findNoticePage.getTotalElements())
            .build();
    }

    @Transactional
    public FindNoticeResponse findNotice(Long noticeId) {
        Notice targetNotice = noticeRepository.findById(noticeId).orElseThrow(RuntimeException::new);

        targetNotice.addView();

        return FindNoticeResponse.builder()
            .title(targetNotice.getTitle())
            .content(targetNotice.getContent())
            .postingDateTime(targetNotice.getCreatedAt())
            .views(targetNotice.getViews())
            .author(targetNotice.getMember().getName())
            .build();
    }
}
