package rsupport.minwoo.notice_management.domain.notice.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAll(Pageable pageable);

    Optional<Notice> findByTitle(String title);
}
