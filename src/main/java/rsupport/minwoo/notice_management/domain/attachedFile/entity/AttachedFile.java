package rsupport.minwoo.notice_management.domain.attachedFile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import rsupport.minwoo.notice_management.domain.notice.entity.Notice;
import rsupport.minwoo.notice_management.global.base.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachedFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @Column(nullable = false, length = 50)
    @Comment("제목")
    private String title;

    @Column(nullable = false, length = 100)
    @Comment("파일 경로")
    private String filePath;

    @Column(nullable = false, length = 30)
    @Comment("파일 타입")
    private String type;

    @Builder
    private AttachedFile(Long id, Notice notice, String title, String filePath, String type) {
        this.id = id;
        this.notice = notice;
        this.title = title;
        this.filePath = filePath;
        this.type = type;
    }
}
