package com.Blog.Util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.junit.Test;

import sun.misc.BASE64Encoder; 
import sun.misc.BASE64Decoder; 

/**
 * 加密解密相关工具类
 * @author Mr-hang
 *
 */
public class CiphertextUitlTest {
	@Test
	public void encryptionTest() throws Exception{
		String ciphertext=CiphertextUitl.encryption("8847656771");
		System.out.println("加密后的密文为："+ciphertext);
		String plaintext=CiphertextUitl.decrypted(ciphertext);
		System.out.println("解谜完毕后的明文为："+plaintext);
	}
}
