package rsupport.minwoo.notice_management.domain.notice.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNoticeRequest {
    private String title;
    private String content;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
