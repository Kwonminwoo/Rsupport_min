package rsupport.minwoo.notice_management.domain.notice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FindNoticeResponse {

    private String title;
    private String content;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime postingDateTime;
    private long views;
    private String author;

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

    public void addViews() {
        this.views++;
    }
}
