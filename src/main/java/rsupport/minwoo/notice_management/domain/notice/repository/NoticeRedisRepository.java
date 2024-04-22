package rsupport.minwoo.notice_management.domain.notice.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;

@Repository
@RequiredArgsConstructor
public class NoticeRedisRepository {

    private final String KEY_PREFIX = "NOTICE_RESPONSE_ID:";
    private final Duration NOTICE_CACHE_TTL = Duration.ofDays(7);
    private final RedisTemplate<String, FindNoticeResponse> redisTemplate;

    public void setNoticeResponse(FindNoticeResponse noticeResponse, Long noticeId) {
        redisTemplate.opsForValue().set(getKey(noticeId), noticeResponse, NOTICE_CACHE_TTL);
    }

    public Optional<FindNoticeResponse> getNoticeResponse(Long noticeId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(getKey(noticeId)));
    }

    public void deleteNoticeResponse(Long noticeId) {
        redisTemplate.delete(getKey(noticeId));
    }

    public void incrementView(Long noticeId) {
        FindNoticeResponse noticeResponse = redisTemplate.opsForValue().get(getKey(noticeId));
        noticeResponse.addViews();
        redisTemplate.opsForValue().set(getKey(noticeId), noticeResponse, NOTICE_CACHE_TTL);
    }

    private String getKey(Long noticeId) {
        return KEY_PREFIX + noticeId;
    }
}
