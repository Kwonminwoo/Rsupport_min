package rsupport.minwoo.notice_management.domain.attachedFile.service;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

@ExtendWith(MockitoExtension.class)
class AttachedFileServiceTest {

    private final String basePath = "/Users/qwert/test/";
    private AttachedFileService attachedFileService;

    @BeforeEach
    void setAttachedFileService() {
        attachedFileService = new AttachedFileService(basePath);
    }

    @Nested
    @DisplayName("첨부파일 저장은 ")
    class Context_attachedFileSave {

        @Test
        @DisplayName("성공 시 에러가 발생하지 않는다.")
        void _willSuccess() {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(99L)
                .member(member)
                .title("테스트 공지입니다.")
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            // when
            attachedFileService.saveAttachedFile(notice, List.of(mockMultipartFile));

            // then
            Assertions.assertThat(notice.getFileList().size()).isEqualTo(1);
            Assertions.assertThat(notice.getFileList().get(0).getTitle())
                .isEqualTo(mockMultipartFile.getOriginalFilename());
        }
    }

    @Nested
    @DisplayName("첨푸바일 삭제는 ")
    class Context_attachedFileDelete {

        @Test
        @DisplayName("성공 시 에러가 발생하지 않는다.")
        void _willSuccess() {
            // given
            String noticeTitle = "테스트 공지입니다.";

            Member member = Member.builder()
                .id(1L)
                .name("tester")
                .build();

            Notice notice = Notice.builder()
                .id(1L)
                .member(member)
                .title(noticeTitle)
                .content("테스트입니다.")
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now().plusDays(30))
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());

            attachedFileService.saveAttachedFile(notice, List.of(mockMultipartFile));

            // when & then
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                attachedFileService.deleteAttachedFile(noticeTitle));
        }
    }
}