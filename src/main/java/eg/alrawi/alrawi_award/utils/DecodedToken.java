package eg.alrawi.alrawi_award.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class DecodedToken {

    private static Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .build()
                .parseClaimsJwt(token.split("\\.")[0] + "." + token.split("\\.")[1] + ".")
                .getBody();
    }

    public static String getEmailFromToken(String token) {
        return decodeToken(token).get("sub", String.class);
    }

}
