package rsupport.minwoo.notice_management.domain.notice.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindNoticeResponse {

    private final String title;
    private final String content;
    private final LocalDateTime postingDateTime;
    private final long views;
    private final String author;

    @Builder
    public FindNoticeResponse(String title, String content, LocalDateTime postingDateTime,
        long views,
        String author) {
        this.title = title;
        this.content = content;
        this.postingDateTime = postingDateTime;
        this.views = views;
        this.author = author;
    }
}
