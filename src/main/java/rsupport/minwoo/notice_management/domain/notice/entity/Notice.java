package rsupport.minwoo.notice_management.domain.notice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Comment;
import rsupport.minwoo.notice_management.domain.attachedFile.entity.AttachedFile;
import rsupport.minwoo.notice_management.domain.member.entity.Member;
import rsupport.minwoo.notice_management.domain.notice.dto.request.UpdateNoticeRequest;
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

    @Column(nullable = false, updatable = false)
    @Comment("조회수")
    private long views;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @OneToMany(mappedBy = "notice", orphanRemoval = true)
    @Cascade(CascadeType.PERSIST)
    private List<AttachedFile> fileList = new ArrayList<>();

    @Builder
    private Notice(Long id, String title, String content, LocalDateTime startDateTime,
        LocalDateTime endDateTime, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.views = 0;
        this.member = member;
    }

    public void addAttachedFile(AttachedFile attachedFile) {
        fileList.add(attachedFile);
    }

    public void removeAllAttachedFile() {
        this.fileList.clear();
    }

    public void addView() {
        this.views++;
    }

    public void update(UpdateNoticeRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.startDateTime = request.getStartDateTime();
        this.endDateTime = request.getEndDateTime();
    }
}
