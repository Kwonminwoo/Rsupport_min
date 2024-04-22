package rsupport.minwoo.notice_management.domain.notice.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("select n from Notice n join fetch n.member")
    Page<Notice> findAll(Pageable pageable);

    Optional<Notice> findByTitle(String title);

    @Query("select n from Notice n where n.title = :title and n.id != :noticeId")
    Optional<Notice> findByTitleNotThisNotice(String title, Long noticeId);

    @Query("select new rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse("
        + "n.title, n.content, n.createdAt, n.views, n.member.name) from Notice n where n.id = :id")
    Optional<FindNoticeResponse> findNoticeResponseById(Long id);

    @Modifying
    @Query("update Notice n set n.views = :views where n.id = :id")
    void plusViewById(Long id, Long views);


}
