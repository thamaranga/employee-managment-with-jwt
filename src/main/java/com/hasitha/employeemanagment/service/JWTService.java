package com.hasitha.employeemanagment.service;

import com.hasitha.employeemanagment.dto.TokenResponseDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    public TokenResponseDTO generateToken(Authentication authentication){
        TokenResponseDTO tokenResponseDTO= new TokenResponseDTO();
        EmployeeUserDetails user= (EmployeeUserDetails) authentication.getPrincipal();
        Map<String , Object> claims= new HashMap<>();
        claims.put("roles",user.getAuthorities());
        tokenResponseDTO.setAccessToken(createAccessToken(claims, user.getUsername()));
        tokenResponseDTO.setRefreshToken(createRefreshToken( user.getUsername()));
        return tokenResponseDTO;

    }

    public String createAccessToken(Map<String, Object> claims, String userName) {
        return Jwts.builder().
                setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*5)).
                signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /*While generating the refresh token, claims no need to set */
    private String createRefreshToken( String userName) {
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30)).
                signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    /*We can use https://www.allkeysgenerator.com/ website for generating
    * below encryption key*/
    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode("26452948404D635166546A576E5A7234753778214125442A472D4A614E645267");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //extract username from JWT
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        //return extractClaim(token,claim->claim.getExpiration());
        return extractClaim(token, Claims::getExpiration);

    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }



    /*In below method <T> specifies that method parameter uses generic types
    * Next T means this method return type*/
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //Claims means all the key value pairs inside JWT  payload.(JWT token has three fields as
    // header, payload and verify signature)
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }






}
