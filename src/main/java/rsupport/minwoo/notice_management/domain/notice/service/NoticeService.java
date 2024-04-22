package rsupport.minwoo.notice_management.domain.notice.service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import rsupport.minwoo.notice_management.domain.notice.dto.request.UpdateNoticeRequest;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindAllNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;
import rsupport.minwoo.notice_management.domain.notice.exception.FileNameDuplicateException;
import rsupport.minwoo.notice_management.domain.notice.exception.NoticeTitleDuplicateException;
import rsupport.minwoo.notice_management.domain.notice.repository.NoticeRedisRepository;
import rsupport.minwoo.notice_management.domain.notice.repository.NoticeRepository;
import rsupport.minwoo.notice_management.global.exception.DataNotFoundException;
import rsupport.minwoo.notice_management.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;
    private final AttachedFileService attachedFileService;
    private final NoticeRedisRepository noticeRedisRepository;

    @Transactional
    public void createNotice(CreateNoticeRequest createNoticeRequest,
        List<MultipartFile> attachedFileList) {

        validateDuplicateTitle(createNoticeRequest.getTitle());

        Member currentMember = memberRepository.findById(createNoticeRequest.getMemberId())
            .orElseThrow(DataNotFoundException::new);

        Notice notice = Notice.builder()
            .title(createNoticeRequest.getTitle())
            .content(createNoticeRequest.getContent())
            .startDateTime(createNoticeRequest.getStartDateTime())
            .endDateTime(createNoticeRequest.getEndDateTime())
            .member(currentMember)
            .build();

        noticeRepository.save(notice);

        validateDuplicateFileName(attachedFileList);

        attachedFileService.saveAttachedFile(notice, attachedFileList);
    }

    private void validateDuplicateTitle(String noticeTitle) {
        if (noticeRepository.findByTitle(noticeTitle).isPresent()) {
            throw new NoticeTitleDuplicateException(ErrorCode.DUPLICATE_NOTICE_TITLE);
        }
    }

    private void validateDuplicateTitle(String noticeTitle, Long noticeId) {
        if (noticeRepository.findByTitleNotThisNotice(noticeTitle, noticeId).isPresent()) {
            throw new NoticeTitleDuplicateException(ErrorCode.DUPLICATE_NOTICE_TITLE);
        }
    }

    private void validateDuplicateFileName(List<MultipartFile> fileList) {
        Set<String> fileNames = new HashSet<>();

        for (MultipartFile file : fileList) {
            if (fileNames.contains(file.getOriginalFilename())) {
                throw new FileNameDuplicateException(ErrorCode.DUPLICATE_FILE_NAME);
            }
            fileNames.add(file.getOriginalFilename());
        }
    }

    public FindAllNoticeResponse findAllNotice(Pageable pageable) {
        Page<Notice> findNoticePage = noticeRepository.findAll(pageable);

        List<FindNoticeResponse> noticeResponseList = findNoticePage.stream()
            .map(n -> {

                Optional<FindNoticeResponse> savedNoticeOptional = noticeRedisRepository.getNoticeResponse(
                    n.getId());
                long views = n.getViews();
                if(savedNoticeOptional.isPresent()){
                    views = savedNoticeOptional.get().getViews();
                }

                return FindNoticeResponse.builder()
                    .title(n.getTitle())
                    .content(n.getContent())
                    .postingDateTime(n.getCreatedAt())
                    .views(views)
                    .author(n.getMember().getName())
                    .build();
                }
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
        FindNoticeResponse noticeResponse = noticeRedisRepository.getNoticeResponse(noticeId)
            .orElseGet(() -> {
                FindNoticeResponse response = noticeRepository.findNoticeResponseById(noticeId)
                    .orElseThrow(DataNotFoundException::new);
                noticeRedisRepository.setNoticeResponse(response, noticeId);

                return response;
            });

        noticeResponse.addViews();
        noticeRedisRepository.incrementView(noticeId);

        return noticeResponse;
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice targetNotice = noticeRepository.findByIdWithFile(noticeId)
            .orElseThrow(DataNotFoundException::new);
        noticeRepository.delete(targetNotice);
        attachedFileService.deleteAttachedFile(targetNotice.getTitle());
        noticeRedisRepository.deleteNoticeResponse(noticeId);
    }

    @Transactional
    public void updateNotice(Long noticeId, UpdateNoticeRequest updateNoticeRequest,
        List<MultipartFile> attachedFileList) {
        validateDuplicateTitle(updateNoticeRequest.getTitle(), noticeId);

        Notice targetNotice = noticeRepository.findByIdWithFile(noticeId)
            .orElseThrow(DataNotFoundException::new);
        String beforeNoticeTitle = targetNotice.getTitle();

        targetNotice.update(updateNoticeRequest);

        FindNoticeResponse savedNoticeResponse = noticeRedisRepository.getNoticeResponse(noticeId)
            .orElseThrow(DataNotFoundException::new);
        noticeRepository.setViewById(noticeId, savedNoticeResponse.getViews());

        attachedFileService.updateAttachedFile(beforeNoticeTitle, targetNotice, attachedFileList);
        noticeRedisRepository.deleteNoticeResponse(noticeId);
    }
}
