package com.Blog.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import sun.misc.BASE64Encoder; 
import sun.misc.BASE64Decoder; 

/**
 * 加密解密相关工具类
 * @author Mr-hang
 *
 */
public class CiphertextUitl {
	/**
	 * 加密
	 * @param plaintext 明文
	 * @return 密文
	 */
      
    //对称密钥    
    private static SecretKey key;    
      
    //加解密时的初始化向量must be 8 bytes long  
    private static IvParameterSpec iv;  
      
    //Cipher,加解密主体实例  
    private  static Cipher c;    
	static {
		try{
        getSecretKey();
        iv = new IvParameterSpec("12345678".getBytes("UTF-8"));  
        c = Cipher.getInstance("DES/CBC/PKCS5Padding","SunJCE");//DES加密算法，CBC的反馈模式，PKCS5Padding的填充方案 ，SunJCE：Provider 
		}catch(Exception e){
			//TODO 待log4j记录
		}
	}
	/**
	 * 加密
	 * @param plaintext 明文
	 * @return 密文
	 * @throws Exception
	 */
	public static String encryption(String plaintext)  {
		try{
			//初始化-- ENCRYPT_MODE：加密模式， key：密钥，iv：初始化向量  
	        c.init(Cipher.ENCRYPT_MODE, key, iv);    
	        byte[] srcByte = plaintext.getBytes();    
	        //加密  
	        byte[] targetByte = c.doFinal(srcByte);   
	        //Base64编码  
	        String targetString = new BASE64Encoder().encode(targetByte);  
	        return targetString;
		}catch(Exception e){
			
		}
		return null;
		
		
	}
	/**
	 * 解密 
	 * @param ciphertext 密文
	 * @return 明文
	 */
	public static String decrypted(String ciphertext)  throws Exception{
		//初始化-- DECRYPT_MODE：解密模式， key：密钥，iv：初始化向量  
        c.init(Cipher.DECRYPT_MODE, key, iv);   
        //Base64解码  
        byte[] srcByte = new BASE64Decoder().decodeBuffer(ciphertext);  
        //解密  
        byte[] targetByte = c.doFinal(srcByte);    
        return new String(targetByte);    
	}
	/**
	 * 获取密荫，第一次运行时生成密荫，保存到一个对象文件中。
	 * 第二次运行时直接加载密钥，不需要再次生成。
	 * 保证多次运行后加密解密密码一致
	 * 安全性：注意密荫文件要存储好，不可丢失！
	 * 值的小心的是，如果有一天服务器要迁移，要还原用户信息，则密荫文件不可丢失，否则。。。
	 * @return
	 */
	public static void getSecretKey() throws Exception{
		//TODO 密荫文件自己找一个文件夹去放，现在当当只放在d盘下
		File file=new File("d://key");
		if(file.exists()){
			ObjectInputStream in=new ObjectInputStream(new FileInputStream(file));
			key=(SecretKey) in.readObject();
			in.close();
		}else{
			KeyGenerator kg = KeyGenerator.getInstance("DES","SunJCE");    
	        key = kg.generateKey();
	        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file));
	        out.writeObject(key);
	        out.close();
		}
		
	}

}
