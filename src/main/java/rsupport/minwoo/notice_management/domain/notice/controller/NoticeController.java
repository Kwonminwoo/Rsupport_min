package rsupport.minwoo.notice_management.domain.notice.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rsupport.minwoo.notice_management.domain.notice.dto.request.CreateNoticeRequest;
import rsupport.minwoo.notice_management.domain.notice.dto.request.UpdateNoticeRequest;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindAllNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.service.NoticeService;
import rsupport.minwoo.notice_management.global.base.ResponseAPI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseAPI<Void>> createNotice(
        @Valid @RequestPart CreateNoticeRequest createNoticeRequest,
        @RequestPart(required = false) List<MultipartFile> attachedFileList) {
        noticeService.createNotice(createNoticeRequest, attachedFileList);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseAPI.response("공지 생성 완료"));
    }

    @GetMapping
    public ResponseEntity<ResponseAPI<FindAllNoticeResponse>> findAllNotice(
        @PageableDefault(size = 5) Pageable pageable) {

        return ResponseEntity.ok(
            ResponseAPI.response(noticeService.findAllNotice(pageable), "공지 전체 조회 완료"));
    }

    @GetMapping("/{notice_id}")
    public ResponseEntity<ResponseAPI<FindNoticeResponse>> findNotice(
        @PathVariable("notice_id") Long noticeId) {

        return ResponseEntity.ok(
            ResponseAPI.response(noticeService.findNotice(noticeId), "공지 조회 완료"));
    }

    @DeleteMapping("/{notice_id}")
    public ResponseEntity<ResponseAPI<Void>> deleteNotice(
        @PathVariable("notice_id") Long noticeId) {

        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ResponseAPI.response("공지 삭제 완료"));
    }

    @PutMapping(
        value = "/{notice_id}",
        consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public ResponseEntity<ResponseAPI<Void>> updateNotice(
        @PathVariable("notice_id") Long noticeId,
        @Valid @RequestPart UpdateNoticeRequest updateNoticeRequest,
        @RequestPart(required = false) List<MultipartFile> attachedFileList) {

        noticeService.updateNotice(noticeId, updateNoticeRequest, attachedFileList);

        return ResponseEntity.ok(ResponseAPI.response("수정에 성공했습니다."));
    }

}
