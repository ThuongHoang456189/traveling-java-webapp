package thuonghth.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.log4j.Logger;

/**
 * Referenced to https://www.baeldung.com/java-password-hashing
 */
public class PBKDF2PasswordHasher {
	/**
	 * Each token produced by this class uses this identifier as a prefix.
	 */
	private static final Logger LOGGER = Logger.getLogger(PBKDF2PasswordHasher.class);

	public static final String ID = "$31$";

	/**
	 * The minimum recommended cost, used by default
	 */
	public static final int DEFAULT_COST = 16;

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final int SIZE = 128;

	private static final Pattern LAYOUT = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");

	private final SecureRandom RANDOM;

	private final int COST;

	public PBKDF2PasswordHasher() {
		this(DEFAULT_COST);
	}

	/**
	 * Create a password manager with a specified cost
	 *
	 * @param cost the exponential computational cost of hashing a password, 0 to 30
	 */
	public PBKDF2PasswordHasher(int cost) {
		iterations(cost); /* Validate cost */
		this.COST = cost;
		this.RANDOM = new SecureRandom();
	}

	private static int iterations(int cost) {
		if ((cost < 0) || (cost > 30)) {
			LOGGER.error("Cost must be from 0 to 30. But the cost is " + cost);
			throw new IllegalArgumentException("cost: " + cost);
		}
		return 1 << cost;
	}

	/**
	 * Hash a password for storage.
	 *
	 * @return a secure authentication token to be stored for later authentication
	 */
	public String hash(String passwordStr) {
		char[] password = passwordStr.toCharArray();
		byte[] salt = new byte[SIZE / 8];
		RANDOM.nextBytes(salt);
		byte[] dk = pbkdf2(password, salt, 1 << COST);
		byte[] hash = new byte[salt.length + dk.length];
		System.arraycopy(salt, 0, hash, 0, salt.length);
		System.arraycopy(dk, 0, hash, salt.length, dk.length);
		Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
		return ID + COST + '$' + enc.encodeToString(hash);
	}

	/**
	 * Authenticate with a password and a stored password token.
	 *
	 * @return true if the password and token match
	 */
	public boolean checkPassword(String passwordStr, String token) {
		char[] password = passwordStr.toCharArray();
		Matcher m = LAYOUT.matcher(token);
		if (!m.matches()) {
			LOGGER.error("Invalid token format. The valid format is " + LAYOUT);
			throw new IllegalArgumentException("Invalid token format");
		}
		int iterations = iterations(Integer.parseInt(m.group(1)));
		byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
		byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
		byte[] check = pbkdf2(password, salt, iterations);
		int zero = 0;
		for (int idx = 0; idx < check.length; ++idx)
			zero |= hash[salt.length + idx] ^ check[idx];
		return zero == 0;
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
		KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
		try {
			SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
			return f.generateSecret(spec).getEncoded();
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("Missing algorithm: " + ALGORITHM, ex);
			throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
		} catch (InvalidKeySpecException ex) {
			LOGGER.error("Invalid SecretKeyFactory", ex);
			throw new IllegalStateException("Invalid SecretKeyFactory", ex);
		}
	}
}
