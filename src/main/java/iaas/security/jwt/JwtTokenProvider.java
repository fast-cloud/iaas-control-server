package iaas.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationInMs;

    // (application.yml에서 설정값 주입)
    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secret,
            @Value("${jwt.expiration-in-ms}") long expirationInMs) {

        // Base64 인코딩된 시크릿 키를 디코딩하여 SecretKey 객체 생성
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationInMs = expirationInMs;
    }

    /**
     * (로그인 시 사용) 사용자 ID를 받아 JWT를 생성합니다.
     */
    public String generateToken(String ownerUserId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);

        return Jwts.builder()
                .subject(ownerUserId) // 토큰의 '주인' (owner_user_id)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * (필터에서 사용) JWT 토큰을 검증하고 '주인'(owner_user_id)을 반환합니다.
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject(); // .subject(ownerUserId)로 넣었던 값을 꺼냄
    }

    /**
     * (필터에서 사용) 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // (실제로는 로그를 남겨야 함)
            // MalformedJwtException, ExpiredJwtException 등
            return false;
        }
    }
}