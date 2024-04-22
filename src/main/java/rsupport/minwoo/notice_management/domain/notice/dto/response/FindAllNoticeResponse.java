package rsupport.minwoo.notice_management.domain.notice.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FindAllNoticeResponse {

    private final List<FindNoticeResponse> noticeResponseList;
    private final int totalPage;
    private final long totalCount;

    @Builder
    public FindAllNoticeResponse(List<FindNoticeResponse> noticeResponseList, int totalPage,
        long totalCount) {
        this.noticeResponseList = noticeResponseList;
        this.totalPage = totalPage;
        this.totalCount = totalCount;
    }
}
