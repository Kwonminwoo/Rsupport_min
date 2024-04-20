package rsupport.minwoo.notice_management.domain.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateNoticeRequest {

    @NotBlank(message = "제목은 빈 값일 수 없습니다.")
    private String title;
    @NotEmpty(message = "내용에 값을 입력하세요")
    private String content;
    @NotNull(message = "시작일을 입력하세요")
    private LocalDateTime startDateTime;
    @NotNull(message = "종료일을 입력하세요")
    private LocalDateTime endDateTime;
    @NotNull(message = "회원 id가 필요합니다")
    private Long memberId;
}
