package profect.eatcloud.Security.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Specification

import java.security.Key

class JwtUtilSpec extends Specification {

    JwtUtil jwtUtil

    // HS256 기준 최소 256비트(32바이트) 이상의 길이가 필요합니다.
    String secretKey = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF"

    def setup() {
        jwtUtil = new JwtUtil()
        // @Value로 주입되는 private 필드 강제 세팅
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", secretKey)
    }

    def "generateToken should produce token containing subject and role"() {
        given:
        String username = "user1"
        String role = "ROLE_USER"

        when:
        String token = jwtUtil.generateToken(username, role)

        then:
        token != null

        and: "Subject(username) 검증"
        def claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
        claims.getSubject() == username

        and: "role 클레임 검증"
        claims.get("role", String) == role
    }

    def "extractUsername should return correct username"() {
        given:
        String username = "anotherUser"
        String role = "ROLE_ADMIN"

        when:
        String token = jwtUtil.generateToken(username, role)

        then:
        jwtUtil.extractUsername(token) == username
    }

    def "extractExpiration should return a date in the future"() {
        given:
        String token = jwtUtil.generateToken("any", "role")

        when:
        Date expiration = jwtUtil.extractExpiration(token)

        then:
        expiration.after(new Date())
    }

    def "validateToken should return true for valid token"() {
        given:
        String token = jwtUtil.generateToken("validUser", "role")

        expect:
        jwtUtil.validateToken(token)
    }

    def "validateToken should return false for expired token"() {
        given: "의도적으로 만료된(expired) 토큰 생성"
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes())
        String expiredToken = Jwts.builder()
                .setSubject("expiredUser")
                .claim("role", "ROLE_USER")
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * 3600_000))   // 2시간 전
                .setExpiration(new Date(System.currentTimeMillis() - 1_000))      // 1초 전에 만료
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()

        expect:
        !jwtUtil.validateToken(expiredToken)
    }

    def "validateToken should return false for invalid token format"() {
        expect:
        !jwtUtil.validateToken("this.is.not.a.valid.token")
    }

}
