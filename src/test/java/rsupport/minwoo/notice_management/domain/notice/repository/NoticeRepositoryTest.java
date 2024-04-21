package rsupport.minwoo.notice_management.domain.notice.repository;


import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.domain.member.repository.MemberRepository;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("공지 페이지 조회 findAll()은 공지 페이지를 반환한다.")
    void _willSuccess_findAll() {
        // given
        Member member = Member.builder()
            .name("tester")
            .build();
        Member savedMember = memberRepository.save(member);

        Notice notice = Notice.builder()
            .title("테스트 공지입니다.")
            .content("테스트입니다.")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.now().plusDays(30))
            .member(savedMember)
            .build();

        Notice savedNotice = noticeRepository.save(notice);

        PageRequest pageRequest = PageRequest.of(0, 5);

        // when
        Page<Notice> noticePage = noticeRepository.findAll(pageRequest);

        //then
        Assertions.assertThat(noticePage).isNotNull();
        Assertions.assertThat(noticePage.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(noticePage.getContent().get(0).getTitle())
            .isEqualTo(savedNotice.getTitle());
    }

    @Test
    @DisplayName("findByTitle()은 제목으로 공지사항을 조회하여 반환한다.")
    void _willSuccess_findByTitle() {
        // given
        Member member = Member.builder()
            .name("tester")
            .build();
        Member savedMember = memberRepository.save(member);

        Notice notice = Notice.builder()
            .title("테스트 공지입니다.")
            .content("테스트입니다.")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.now().plusDays(30))
            .member(savedMember)
            .build();

        Notice savedNotice = noticeRepository.save(notice);

        // when
        Notice foundNotice = noticeRepository.findByTitle(notice.getTitle())
            .orElseThrow(RuntimeException::new);

        //then
        Assertions.assertThat(foundNotice.getTitle()).isEqualTo(savedNotice.getTitle());
    }
}