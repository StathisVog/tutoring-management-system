package com.education.tutoring.management.rest.outbound;

import com.education.tutoring.management.application.dto.user.UserDTO;
import com.education.tutoring.management.application.port.out.JwtTokenGenerator;
import com.education.tutoring.management.domain.configuration.JwtConfigurationProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Implementation of JWT generation using the JJWT library. Responsible for creating
 * signed JSON Web Tokens for authenticated users.
 */
@Service
public class JwtTokenGeneratorImpl implements JwtTokenGenerator {

	private final JwtConfigurationProperties jwtConfig;

	public JwtTokenGeneratorImpl(JwtConfigurationProperties jwtConfig) {
		this.jwtConfig = jwtConfig;
	}

	/**
	 * Generates a signed JWT for the provided user.
	 * @param userDTO the authenticated user data
	 * @return a serialized JWT string
	 */
	@Override
	public String generateToken(UserDTO userDTO) {
		return Jwts.builder()
			.claim("role", "ROLE_" + userDTO.getRole().name())
			.subject(userDTO.getUsername())
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration()))
			.signWith(getSigningKey(), Jwts.SIG.HS256)
			.compact();
	}

	/**
	 * Retrieves the cryptographic key used for JWT signing.
	 * @return the HMAC SHA secret key
	 */
	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}

}
