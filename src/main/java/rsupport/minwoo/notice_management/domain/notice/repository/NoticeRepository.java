package rsupport.minwoo.notice_management.domain.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
