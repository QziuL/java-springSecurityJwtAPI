package api_auth.config.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;


public class JwtCreator {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ROLES_AUTHORITIES = "authorities";

    // método para gerar o token
    public static String create(String prefix,String key, JwtObject jwtObject) {
        String token = Jwts.builder()
                .setSubject(jwtObject.getSubject())
                .setIssuedAt(jwtObject.getIssuedAt())
                .setExpiration(jwtObject.getExpiresAt())
                .claim(ROLES_AUTHORITIES, checkRoles(jwtObject.getRoles()))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

        return prefix + " " + token;
    }

    // método para verificação da estrutura de token
    public static JwtObject create(String token, String prefix, String key) throws
            ExpiredJwtException,
            UnsupportedJwtException,
            MalformedJwtException,
            SignatureException {

        token = token.replace(prefix, "");

        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

        JwtObject object = new JwtObject();
        object.setSubject(claims.getSubject());
        object.setExpiresAt(claims.getExpiration());
        object.setIssuedAt(claims.getIssuedAt());
        object.setRoles((List) claims.get(ROLES_AUTHORITIES));
        return object;
    }

    // verifica tokens mapeados removendo prefixo, ex: "ROLE_ADMIN, ROLE_USER"
    private static List<String> checkRoles(List<String> roles) {
        return roles.stream()
                .map(s -> "ROLE_".concat(s.replaceAll("ROLE_","")))
                .collect(Collectors.toList());
    }
}
