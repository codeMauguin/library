package app.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {
	
	private static final String cipherMode = "AES/ECB/PKCS5Padding";
	
	private static Cipher getCipher(byte[] key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		return cipher;
	}
	
	/**
	 * AES ECB 加密
	 *
	 * @param message 需要加密的字符串
	 * @param key     密匙
	 * @return 返回加密后密文，编码为base64
	 */
	public static byte[] encryptECB(byte[] message, String key) {
		try {
			byte[] content;
			byte[] keyByte = key.getBytes(StandardCharsets.UTF_8);
			SecretKeySpec keySpec = new SecretKeySpec(keyByte, "AES");
			
			Cipher cipher = Cipher.getInstance(cipherMode);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] data = cipher.doFinal(message);
			return Base64.getEncoder().encode(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
}
