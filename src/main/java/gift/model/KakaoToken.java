package gift.model;

import gift.dto.kakao.KakaoTokenResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "kakao_token")
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

    public void updateToken(KakaoTokenResponse kakaoTokenResponse) {
        this.accessToken = kakaoTokenResponse.accessToken();
        if (kakaoTokenResponse.refreshToken() != null) {
            this.refreshToken = kakaoTokenResponse.refreshToken();
        }
    }
}
