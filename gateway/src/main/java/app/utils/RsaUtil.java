package app.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaUtil {
	public static final Base64 base64 =
			new Base64();
	private static final String RSA = "RSA/ECB/PKCS1Padding";
	private static final String KEY = "RSA";
	
	/**
	 * > 它接受一个纯文本字符串和一个公钥，并返回一个加密的字符串
	 *
	 * @param plain      要加密的纯文本。
	 * @param PUBLIC_KEY 您从服务器获得的公钥。
	 * @return 编码的字符串。
	 */
	public static byte[] encode(byte[] plain, String PUBLIC_KEY) {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64.decode(PUBLIC_KEY));
			KeyFactory keyFactory = KeyFactory.getInstance(KEY);
			Cipher encryptCipher = Cipher.getInstance(RSA);
			encryptCipher.init(Cipher.ENCRYPT_MODE, keyFactory.generatePublic(keySpec));
			ByteArrayOutputStream outputStream = send(plain, encryptCipher, 245);
			return base64.encode(outputStream
										 .toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	/**
	 * 它以“块”字节块的形式加密明文，并将加密数据作为“ByteArrayOutputStream”返回
	 *
	 * @param plain         要加密的明文
	 * @param encryptCipher 用于加密数据的密码。
	 * @return 字节数组输出流。
	 */
	private static ByteArrayOutputStream send(byte[] plain, Cipher encryptCipher, int block) throws
																							 Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		var offset = 0;
		while (offset < plain.length) {
			byte[] bytes = encryptCipher.doFinal(plain, offset, Math.min(plain.length - offset
					, block));
			outputStream.write(bytes);
			offset += block;
		}
		return outputStream;
	}
	
	
	/**
	 * 它使用私钥解密数据。
	 *
	 * @param data        要解密的数据。
	 * @param PRIVATE_KEY 您在上一步中生成的私钥。
	 * @return 解密后的数据。
	 */
	public static byte[] decrypt(byte[] data, String PRIVATE_KEY) {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64.decode(PRIVATE_KEY));
			KeyFactory keyFactory = KeyFactory.getInstance(KEY);
			Cipher cipher = Cipher.getInstance(RSA);
			cipher.init(Cipher.DECRYPT_MODE, keyFactory.generatePrivate(keySpec));
			return send(data, cipher, 256).toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}
	
	
}
