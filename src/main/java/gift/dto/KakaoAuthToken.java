package gift.dto;

public record KakaoAuthToken(String accessToken, String refreshToken) {
    public static KakaoAuthToken of(String accessToken, String refreshToken) {
        return new KakaoAuthToken(accessToken, refreshToken);
    }
}
