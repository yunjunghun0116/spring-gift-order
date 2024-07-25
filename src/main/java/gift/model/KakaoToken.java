package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "kakao_token")
@SQLDelete(sql = "update kakao_token set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class KakaoToken extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
    @NotNull
    @Column(name = "access_token")
    private String accessToken;
    @NotNull
    @Column(name = "refresh_token")
    private String refreshToken;

    protected KakaoToken() {
    }

    public KakaoToken(Member member, String accessToken, String refreshToken) {
        this.member = member;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;

    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Member getMember() {
        return member;
    }
}
