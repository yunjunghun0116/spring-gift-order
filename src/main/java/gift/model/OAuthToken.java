package gift.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "oauth_token")
public class OAuthToken extends BaseEntity {
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
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "oauth_type")
    private OAuthType oAuthType;
    @NotNull
    @Column(name = "access_token_expires_in")
    private Integer accessTokenExpiresIn;
    @NotNull
    @Column(name = "refresh_token_expires_in")
    private Integer refreshTokenExpiresIn;

    protected OAuthToken() {
    }

    public OAuthToken(Member member, String accessToken, String refreshToken, Integer accessTokenExpiresIn, Integer refreshTokenExpiresIn) {
        this.member = member;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
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

    public void updateToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        if (refreshToken != null) {
            this.refreshToken = refreshToken;
        }
    }
}
