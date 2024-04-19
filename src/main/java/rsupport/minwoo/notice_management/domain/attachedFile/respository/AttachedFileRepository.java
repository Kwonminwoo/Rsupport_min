package rsupport.minwoo.notice_management.domain.attachedFile.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import rsupport.minwoo.notice_management.domain.attachedFile.entity.AttachedFile;

public interface AttachedFileRepository extends JpaRepository<AttachedFile, Long> {

}
