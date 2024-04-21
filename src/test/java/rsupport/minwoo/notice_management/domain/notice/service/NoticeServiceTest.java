package rsupport.minwoo.notice_management.domain.notice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import rsupport.minwoo.notice_management.domain.attachedFile.entity.AttachedFile;
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
import rsupport.minwoo.notice_management.domain.notice.repository.NoticeRepository;
import rsupport.minwoo.notice_management.global.exception.DataNotFoundException;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeService noticeService;
    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AttachedFileService attachedFileService;

    @Nested
    @DisplayName("공지 생성은 ")
    class Context_createNotice {

        @Test
        @DisplayName("성공 시 에러가 발생하지 않는다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            AttachedFile attachedFile = AttachedFile.builder()
                .notice(notice)
                .title(mockMultipartFile.getOriginalFilename())
                .filePath("test")
                .type(mockMultipartFile.getContentType())
                .build();

            CreateNoticeRequest request = CreateNoticeRequest.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .memberId(member.getId())
                .build();

            given(noticeRepository.findByTitle(any())).willReturn(Optional.empty());
            given(memberRepository.findById(any())).willReturn(Optional.of(member));
            given(noticeRepository.save(any())).willReturn(notice);
            given(attachedFileService.saveAttachedFile(any(), any())).willReturn(attachedFile);

            // when
            noticeService.createNotice(request, List.of(mockMultipartFile));

            // then
            verify(noticeRepository, atLeastOnce()).save(any());
            verify(attachedFileService, atLeastOnce()).saveAttachedFile(any(), any());
        }

        @Test
        @DisplayName("제목이 겹치면 에러가 발생한다.")
        void _willFailDuplicateTitle() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            Notice existNotice = Notice.builder()
                .id(2L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("저장되어있는 공지입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            CreateNoticeRequest request = CreateNoticeRequest.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .memberId(member.getId())
                .build();

            given(noticeRepository.findByTitle(any())).willReturn(Optional.of(existNotice));

            // when & then
            Assertions.assertThatThrownBy(
                () -> noticeService.createNotice(request, List.of(mockMultipartFile)))
                .isInstanceOf(NoticeTitleDuplicateException.class);
        }

        @Test
        @DisplayName("유저 데이터를 찾지못하면 에러가 발생한다.")
        void _willFailNoMember() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            CreateNoticeRequest request = CreateNoticeRequest.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .memberId(member.getId())
                .build();

            given(noticeRepository.findByTitle(any())).willReturn(Optional.empty());
            given(memberRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(
                    () -> noticeService.createNotice(request, List.of(mockMultipartFile)))
                .isInstanceOf(DataNotFoundException.class);
        }

        @Test
        @DisplayName("파일 이름이 겹치면 에러가 발생한다.")
        void _willFailDuplicateFileName() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile1 = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            MockMultipartFile mockMultipartFile2 = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            CreateNoticeRequest request = CreateNoticeRequest.builder()
                .title(notice.getTitle())
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .memberId(member.getId())
                .build();

            given(noticeRepository.findByTitle(any())).willReturn(Optional.empty());
            given(memberRepository.findById(any())).willReturn(Optional.of(member));
            given(noticeRepository.save(any())).willReturn(notice);

            // when & then
            Assertions.assertThatThrownBy(
                    () -> noticeService.createNotice(request, List.of(mockMultipartFile1, mockMultipartFile2)))
                .isInstanceOf(FileNameDuplicateException.class);
        }
    }

    @Nested
    @DisplayName("공지 전체 조회는 ")
    class Context_findAllNotice {

        @Test
        @DisplayName("성공 시 공지 정보를 반환한다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            PageRequest pageRequest = PageRequest.of(0, 5);
            Page<Notice> noticePage = new PageImpl<>(List.of(notice));

            given(noticeRepository.findAll(any(Pageable.class))).willReturn(noticePage);

            // when
            FindAllNoticeResponse result = noticeService.findAllNotice(pageRequest);

            // then
            Assertions.assertThat(result.getNoticeResponseList().size()).isEqualTo(1);
            Assertions.assertThat(result.getNoticeResponseList().get(0).getTitle())
                .isEqualTo(notice.getTitle());
        }
    }

    @Nested
    @DisplayName("공지 단건 조회는 ")
    class Context_findNotice {

        @Test
        @DisplayName("성공 시 공지 정보를 반환한다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            given(noticeRepository.findById(any())).willReturn(Optional.of(notice));

            // when
            FindNoticeResponse result = noticeService.findNotice(notice.getId());

            // then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.getTitle()).isEqualTo(notice.getTitle());
        }

        @Test
        @DisplayName("공지 데이터를 찾지못하면 에러가 발생한다.")
        void _willFailNoNotice() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            given(noticeRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(
                    () -> noticeService.findNotice(notice.getId()))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("공지 수정은 ")
    class Context_updateNotice {

        @Test
        @DisplayName("성공 시 에러가 발생하지 않는다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            AttachedFile attachedFile = AttachedFile.builder()
                .notice(notice)
                .title(mockMultipartFile.getOriginalFilename())
                .filePath("test")
                .type(mockMultipartFile.getContentType())
                .build();

            UpdateNoticeRequest request = UpdateNoticeRequest.builder()
                .title("새로운 제목입니다.")
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .build();

            given(noticeRepository.findByTitle(any())).willReturn(Optional.empty());
            given(noticeRepository.findById(any())).willReturn(Optional.of(notice));
            given(attachedFileService.saveAttachedFile(any(), any())).willReturn(attachedFile);

            // when
            noticeService.updateNotice(notice.getId(), request, List.of(mockMultipartFile));

            // then
            verify(noticeRepository, atLeastOnce()).findById(any());
            verify(attachedFileService, atLeastOnce()).saveAttachedFile(any(), any());
        }

        @Test
        @DisplayName("공지 데이터를 찾지못하면 에러가 발생한다.")
        void _willFailNoNotice() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            UpdateNoticeRequest request = UpdateNoticeRequest.builder()
                .title("새로운 제목입니다.")
                .content(notice.getContent())
                .startDateTime(notice.getStartDateTime())
                .endDateTime(notice.getEndDateTime())
                .build();

            given(noticeRepository.findByTitle(any())).willReturn(Optional.empty());
            given(noticeRepository.findById(any())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(
                    () -> noticeService.updateNotice(notice.getId(), request, List.of(mockMultipartFile)))
                .isInstanceOf(DataNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("공지 삭제는 ")
    class Context_deleteNotice {

        @Test
        @DisplayName("성공 시 에러가 발생하지 않는다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            given(noticeRepository.findById(any())).willReturn(Optional.of(notice));
            doNothing().when(noticeRepository).delete(any());
            doNothing().when(attachedFileService).deleteAttachedFile(any());

            // when
            noticeService.deleteNotice(notice.getId());

            // then
            verify(noticeRepository, atLeastOnce()).delete(any());
            verify(attachedFileService, atLeastOnce()).deleteAttachedFile(any());
        }
    }
}