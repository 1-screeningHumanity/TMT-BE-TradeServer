package ScreeningHumanity.TradeServer.global.common.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DecodingToken {

    private final Environment environment;
    public static final String START_BEARER = "Bearer ";

    public String getUuid(String jwt) {
        Claims claims = decodeJWT(jwt);
        if (claims == null) {
            return "Token Error";
        }
        return claims.getSubject();
    }

    private Claims decodeJWT(String jwt) {

        if (jwt == null || !jwt.startsWith(START_BEARER)) {
            log.error("Token 형식 오류, 확인 필요");
            return null;
        }

        jwt = jwt.substring(7);

        return Jwts.parser().setSigningKey(environment.getProperty("JWT.SECRET_KEY"))
                .parseClaimsJws(jwt).getBody();
    }
}
