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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "oauth_token")
@SQLDelete(sql = "update oauth_token set deleted = true where id = ?")
@SQLRestriction("deleted is false")
public class OAuthToken extends BaseEntity {
    @NotNull
    @Column(name = "access_token")
    private String accessToken;
    @NotNull
    @Column(name = "refresh_token")
    private String refreshToken;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;
    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "oauth_domain")
    private OAuthDomain oauthDomain;

    protected OAuthToken() {
    }

    public OAuthToken(String accessToken, String refreshToken, Member member, OAuthDomain oauthDomain) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.member = member;
        this.oauthDomain = oauthDomain;
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

    public OAuthDomain getOauthDomain() {
        return oauthDomain;
    }
}
