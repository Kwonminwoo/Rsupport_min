package rsupport.minwoo.notice_management.domain.notice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import rsupport.minwoo.notice_management.domain.notice.dto.request.CreateNoticeRequest;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindAllNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.service.NoticeService;

@WebMvcTest(controllers = NoticeController.class)
class NoticeControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private NoticeService noticeService;

    @Nested
    @DisplayName("공지 생성은")
    class Context_createNotice {

        @Test
        @DisplayName("성공 시 상테 메시지 201을 반환한다.")
        void _willSuccess() throws Exception {
            // given
            Long memberId = 1L;
            CreateNoticeRequest createRequest = CreateNoticeRequest.builder()
                .title("2024년 12월 행사 안내")
                .content("12월달 행사를 안내합니다.")
                .startDateTime(LocalDateTime.of(2024, 12, 01, 00, 00))
                .endDateTime(LocalDateTime.of(2024, 12, 31, 11, 59))
                .memberId(memberId)
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
            MockMultipartFile requestDto = new MockMultipartFile("createNoticeRequest",
                "createNoticeRequest",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(createRequest));

            doNothing().when(noticeService).createNotice(any(), any());

            // when
            ResultActions resultActions = mvc.perform(multipart("/api/notices")
                .file(mockMultipartFile)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

            // then
            resultActions
                .andExpect(status().isCreated())
                .andDo(print());
        }

        @Test
        @DisplayName("제목이 없으면 badRequest 에러가 발생한다")
        void _failNoNoticeTitle() throws Exception {
            // given
            Long memberId = 1L;
            CreateNoticeRequest createRequest = CreateNoticeRequest.builder()
                .content("12월달 행사를 안내합니다.")
                .startDateTime(LocalDateTime.of(2024, 12, 01, 00, 00))
                .endDateTime(LocalDateTime.of(2024, 12, 31, 11, 59))
                .memberId(memberId)
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
            MockMultipartFile requestDto = new MockMultipartFile("createNoticeRequest",
                "createNoticeRequest",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(createRequest));
            doNothing().when(noticeService).createNotice(any(), any());

            // when
            ResultActions resultActions = mvc.perform(multipart("/api/notices")
                .file(mockMultipartFile)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

            // then
            resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", "제목은 빈 값일 수 없습니다.").exists())
                .andDo(print());
        }

        @Test
        @DisplayName("내용이 없으면 badRequest 에러가 발생한다")
        void _failNoContent() throws Exception {
            // given
            Long memberId = 1L;
            CreateNoticeRequest createRequest = CreateNoticeRequest.builder()
                .title("12월 행사 안내")
                .startDateTime(LocalDateTime.of(2024, 12, 01, 00, 00))
                .endDateTime(LocalDateTime.of(2024, 12, 31, 11, 59))
                .memberId(memberId)
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
            MockMultipartFile requestDto = new MockMultipartFile("createNoticeRequest",
                "createNoticeRequest",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(createRequest));
            doNothing().when(noticeService).createNotice(any(), any());

            // when
            ResultActions resultActions = mvc.perform(multipart("/api/notices")
                .file(mockMultipartFile)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

            // then
            resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", "내용에 값을 입력하세요").exists())
                .andDo(print());
        }

        @Test
        @DisplayName("시작 일이 없으면 badRequest 에러가 발생한다")
        void _failNoStartDate() throws Exception {
            // given
            Long memberId = 1L;
            CreateNoticeRequest createRequest = CreateNoticeRequest.builder()
                .title("12월 행사 안내")
                .content("12월달 행사를 안내합니다.")
                .endDateTime(LocalDateTime.of(2024, 12, 31, 11, 59))
                .memberId(memberId)
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
            MockMultipartFile requestDto = new MockMultipartFile("createNoticeRequest",
                "createNoticeRequest",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(createRequest));
            doNothing().when(noticeService).createNotice(any(), any());

            // when
            ResultActions resultActions = mvc.perform(multipart("/api/notices")
                .file(mockMultipartFile)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

            // then
            resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", "시작일을 입력하세요").exists())
                .andDo(print());
        }

        @Test
        @DisplayName("종료 일이 없으면 badRequest 에러가 발생한다")
        void _failNoEndDate() throws Exception {
            // given
            Long memberId = 1L;
            CreateNoticeRequest createRequest = CreateNoticeRequest.builder()
                .title("12월 행사 안내")
                .content("12월달 행사를 안내합니다.")
                .startDateTime(LocalDateTime.of(2024, 12, 01, 00, 00))
                .memberId(memberId)
                .build();

            MockMultipartFile mockMultipartFile = new MockMultipartFile("attachedFileList",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
            MockMultipartFile requestDto = new MockMultipartFile("createNoticeRequest",
                "createNoticeRequest",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(createRequest));
            doNothing().when(noticeService).createNotice(any(), any());

            // when
            ResultActions resultActions = mvc.perform(multipart("/api/notices")
                .file(mockMultipartFile)
                .file(requestDto)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

            // then
            resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", "시작일을 입력하세요").exists())
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("공지 전체 조회는")
    class Context_findAllNotice {

        @Test
        @DisplayName("성공 시 공지 정보를 반환한다")
        void _willSuccess() throws Exception {
            // given
            int pageSize = 5;
            PageRequest pageRequest = PageRequest.of(0, pageSize);

            List<FindNoticeResponse> noticeResponseList = List.of(FindNoticeResponse.builder()
                .title("12월 행사 안내입니다.")
                .content("12월달 행사를 안내합니다.")
                .postingDateTime(LocalDateTime.now())
                .views(200)
                .author("tester")
                .build());

            int totalCount = noticeResponseList.size();

            FindAllNoticeResponse findAllResponse = FindAllNoticeResponse.builder()
                .noticeResponseList(noticeResponseList)
                .totalPage(totalCount % pageSize)
                .totalCount(totalCount)
                .build();

            when(noticeService.findAllNotice(any())).thenReturn(findAllResponse);

            // when
            ResultActions resultActions = mvc.perform(get("/api/notices")
                .content(objectMapper.writeValueAsString(pageRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

            // then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCount", totalCount).exists())
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("공지 단건 조회는")
    class Context_findNotice {

        @Test
        @DisplayName("성공 시 공지 정보를 반환한다.")
        void _willSuccess() throws Exception {
            // given
            FindNoticeResponse response = FindNoticeResponse.builder()
                .title("12월 행사 안내입니다.")
                .content("12월달 행사를 안내합니다.")
                .postingDateTime(LocalDateTime.now())
                .views(200)
                .author("tester")
                .build();

            when(noticeService.findNotice(any())).thenReturn(response);

            // when
            ResultActions resultActions = mvc.perform(get("/api/notices/{notice_id}", 1L));

            // then
            resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", response.getTitle()).exists())
                .andDo(print());
        }
    }

    @Nested
    @DisplayName("공지 삭제는")
    class Context_updateNotice {

        @Test
        @DisplayName("성공 시 상태 메시지 200을 반환한다")
        void _willSuccess() throws Exception {
            // given
            doNothing().when(noticeService).deleteNotice(any());

            // when
            ResultActions resultActions = mvc.perform(delete("/api/notices/{notice_id}", 1L));

            // then
            resultActions
                .andExpect(status().isOk())
                .andDo(print());
        }
    }
}