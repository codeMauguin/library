package app.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

public class SignUtil {
	public static final Random RANDOM = new Random();
	private static final byte[] HEX_ = new byte[]{'1', '2', '3', 'a', 's', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'd', 'f', 'g', 'q', 'w', 'e', 'r', 't', 'h', '4', '6', '5', 'y', '7', '8', '9', '0', 'j', 'k', 'u', 'i', 'l', 'o', 'p'};
	
	public static String obfuscate(Integer len) {
		byte[] res = new byte[len];
		for (int i = len - 1; i >= 0; --i) {
			res[i] = HEX_[RANDOM.nextInt(HEX_.length)];
		}
		return new String(res,
				StandardCharsets.UTF_8);
	}
	
	public static String signing() {
		return UUID.randomUUID().toString();
	}
	
	/**
	 * 它需要一个密码和一个盐，并返回密码的哈希值
	 *
	 * @param password 要散列的密码。
	 * @param salt     用于使密码更安全的随机字符串。
	 * @return 一串十六进制字符。
	 */
	public static String signing(String password,
								 String salt) {
		final MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA1");
			messageDigest.update(base(salt));
			final byte[] digest = messageDigest.digest(base(password));
			StringBuilder builder = new StringBuilder();
			for (byte b : digest) {
				final String hexString = Integer.toHexString(b & 0xff);
				if (hexString.length() == 1) builder.append("0");
				builder.append(hexString);
			}
			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static byte[] base(String target) {
		return Base64.getEncoder().encode(target.getBytes(StandardCharsets.UTF_8));
	}
	
}
