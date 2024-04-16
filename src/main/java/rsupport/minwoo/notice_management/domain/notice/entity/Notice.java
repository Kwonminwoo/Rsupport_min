package rsupport.minwoo.notice_management.domain.notice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.global.base.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("공지사항 식별자")
    private Long id;

    @Column(nullable = false, length = 50)
    @Comment("제목")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Comment("내용")
    private String content;

    @Column(nullable = false)
    @Comment("시작 일시")
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    @Comment("종료 일시")
    private LocalDateTime endDateTime;

    @Column(nullable = false)
    @Comment("조회수")
    private long views;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
