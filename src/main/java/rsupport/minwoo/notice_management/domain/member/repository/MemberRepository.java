package rsupport.minwoo.notice_management.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rsupport.minwoo.notice_management.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
