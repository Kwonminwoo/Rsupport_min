package rsupport.minwoo.notice_management.domain.notice.repository;


import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import rsupport.minwoo.notice_management.domain.attachedFile.entity.AttachedFile;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.domain.member.repository.MemberRepository;
import rsupport.minwoo.notice_management.domain.notice.dto.response.FindNoticeResponse;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {"spring.config.location = classpath:application-test.yml"})
class NoticeRepositoryTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestEntityManager entityManager;

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

    @Test
    @DisplayName("findByTitleNotThisNotice() 는 현재 공지를 제외하고 제목을 기준으로 조회 후 반환한다.")
    void _willSuccess_findByTitleNotThisNotice() {
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

        noticeRepository.save(notice);

        Notice notice2 = Notice.builder()
            .title("테스트 공지입니다.")
            .content("두번 째 공지입니다.")
            .startDateTime(LocalDateTime.now())
            .endDateTime(LocalDateTime.now().plusDays(30))
            .member(savedMember)
            .build();

        noticeRepository.save(notice2);

        // when
        Notice foundNotice = noticeRepository.findByTitleNotThisNotice(notice.getTitle(), notice.getId())
            .orElseThrow(RuntimeException::new);

        //then
        Assertions.assertThat(foundNotice.getContent()).isEqualTo(notice2.getContent());
    }

    @Test
    @DisplayName("findNoticeResponseById() 는 공지를 responseDto 로 반환한다.")
    void _willSuccess_findNoticeResponseById() {
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
        FindNoticeResponse noticeResponse = noticeRepository.findNoticeResponseById(notice.getId())
            .orElseThrow(RuntimeException::new);

        //then
        Assertions.assertThat(noticeResponse.getContent()).isEqualTo(savedNotice.getContent());
    }

    @Test
    @DisplayName("setViewById() 는 공지의 views 를 지정한다.")
    void _willSuccess_setViewById() {
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

        noticeRepository.save(notice);

        // when
        noticeRepository.setViewById(notice.getId(), 1L);

        entityManager.clear();
        Notice foundNotice = noticeRepository.findById(notice.getId())
            .orElseThrow(RuntimeException::new);

        //then
        Assertions.assertThat(foundNotice.getViews()).isEqualTo(1);
    }

    @Test
    @DisplayName("findByIdWithFile() 는 공지와 파일을 반환한다.")
    void _willSuccess_findByIdWithFile() {
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

        AttachedFile file = AttachedFile.builder()
            .notice(notice)
            .title("test.png")
            .filePath("/test")
            .type("image/png")
            .build();

        notice.addAttachedFile(file);

        noticeRepository.save(notice);

        // when
        Notice foundNotice = noticeRepository.findByIdWithFile(notice.getId())
            .orElseThrow(RuntimeException::new);

        //then
        Assertions.assertThat(foundNotice.getFileList().size()).isEqualTo(1);
        Assertions.assertThat(foundNotice.getFileList().get(0).getTitle()).isEqualTo(file.getTitle());
    }
}