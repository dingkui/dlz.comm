package com.dlz.comm.util.encry;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Set;

public class RSAUtil {
	private static String KEY_ALGORITHM_RSA="RSA";
	/**
	 * 从流中读取密钥到字符串
	 * @param inputStream
	 * @return
	 */
	private static String getKeyFromStream(InputStream inputStream){
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String readLine = null;
			while ((readLine = br.readLine()) != null) {
				if (readLine.charAt(0) == '-') {
					continue;
				} else {
					sb.append(readLine);
					sb.append('\r');
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("READ KEY ERROR:", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				inputStream = null;
				throw new RuntimeException("INPUT STREAM CLOSE ERROR:", e);
			}
		}
		return sb.toString();
	}

	public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize,
			String cipherAlgorithm) throws Exception {
		int keyByteSize = keyLength / 8;
		int decryptBlockSize = keyByteSize - reserveSize;
		int nBlock = encryptedBytes.length / keyByteSize;
		ByteArrayOutputStream outbuf = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
			for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
				int inputLen = encryptedBytes.length - offset;
				if (inputLen > keyByteSize) {
					inputLen = keyByteSize;
				}
				byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
				outbuf.write(decryptedBlock);
			}
			outbuf.flush();
			return outbuf.toByteArray();
		} catch (Exception e) {
			throw new Exception("DEENCRYPT ERROR:", e);
		} finally {
			try {
				if (outbuf != null) {
					outbuf.close();
				}
			} catch (Exception e) {
				outbuf = null;
				throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
			}
		}
	}

	public static byte[] encrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize,
			String cipherAlgorithm) throws Exception {
		int keyByteSize = keyLength / 8;
		int encryptBlockSize = keyByteSize - reserveSize;
		int nBlock = plainBytes.length / encryptBlockSize;
		if ((plainBytes.length % encryptBlockSize) != 0) {
			nBlock += 1;
		}
		ByteArrayOutputStream outbuf = null;
		try {
			Cipher cipher = Cipher.getInstance(cipherAlgorithm);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
			for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
				int inputLen = plainBytes.length - offset;
				if (inputLen > encryptBlockSize) {
					inputLen = encryptBlockSize;
				}
				byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
				outbuf.write(encryptedBlock);
			}
			outbuf.flush();
			return outbuf.toByteArray();
		} catch (Exception e) {
			throw new Exception("ENCRYPT ERROR:", e);
		} finally {
			try {
				if (outbuf != null) {
					outbuf.close();
				}
			} catch (Exception e) {
				outbuf = null;
				throw new Exception("CLOSE ByteArrayOutputStream ERROR:", e);
			}
		}
	}
	
	/**
	 * 取得公钥
	 * 
	 * @param publicKeyStr 私钥字符串
	 * @param keyAlgorithm  请求密钥算法的名称 如：RSA
	 * @return
	 * @throws Exception
	 */
	public static PublicKey getPublicKey(String publicKeyStr, String keyAlgorithm) throws Exception {
		try {
			X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decode(publicKeyStr));
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
			// 下行出错 java.security.spec.InvalidKeySpecException:
			// java.security.InvalidKeyException: IOException:
			// DerInputStream.getLength(): lengthTag=127, too big.
			PublicKey publicKey = keyFactory.generatePublic(pubX509);
			return publicKey;
		} catch (Exception e) {
			throw new Exception("get PrivateKey ERROR:", e);
		}
	}
	public static PublicKey getPublicKeyByFile(String publicKeyPath, String keyAlgorithm) throws Exception {
		return getPublicKey(getKeyFromStream(new FileInputStream(publicKeyPath)), keyAlgorithm);
	}
	public static PublicKey getPublicKeyByFile(String publicKeyPath) throws Exception {
		return getPublicKey(getKeyFromStream(new FileInputStream(publicKeyPath)));
	}
	public static PublicKey getPublicKey(InputStream inputStream, String keyAlgorithm) throws Exception {
		return getPublicKey(getKeyFromStream(inputStream), keyAlgorithm);
	}
	public static PublicKey getPublicKey(InputStream inputStream) throws Exception {
		return getPublicKey(getKeyFromStream(inputStream));
	}
	public static PublicKey getPublicKey(String publicKeyStr) throws Exception {
		return getPublicKey(publicKeyStr, KEY_ALGORITHM_RSA);
	}
	
	
	/**
	 * 取得私钥
	 * 
	 * @param privateKeyStr 私钥字符串
	 * @param keyAlgorithm  请求密钥算法的名称 如：RSA
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String privateKeyStr, String keyAlgorithm) throws Exception {
		try {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKeyStr));
			KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
			return keyFactory.generatePrivate(priPKCS8);
		} catch (Exception e) {
			throw new Exception("get PrivateKey ERROR:", e);
		}
	}
	public static PrivateKey getPrivateKeyByFile(String privateKeyPath, String keyAlgorithm) throws Exception {
		return getPrivateKey(getKeyFromStream(new FileInputStream(privateKeyPath)), keyAlgorithm);
	}
	public static PrivateKey getPrivateKeyByFile(String privateKeyPath) throws Exception {
		return getPrivateKey(getKeyFromStream(new FileInputStream(privateKeyPath)));
	}
	public static PrivateKey getPrivateKey(InputStream inputStream, String keyAlgorithm) throws Exception {
		return getPrivateKey(getKeyFromStream(inputStream), keyAlgorithm);
	}
	public static PrivateKey getPrivateKey(InputStream inputStream) throws Exception {
		return getPrivateKey(getKeyFromStream(inputStream));
	}
	public static PrivateKey getPrivateKey(String privateKeyStr) throws Exception {
		return getPrivateKey(privateKeyStr, KEY_ALGORITHM_RSA);
	}


	//输出系统所有支持的加密相关算法
	public static void main(String[] args) {
		Provider[] arr = Security.getProviders();
		for (int i = 0; i < arr.length; i++) {
			Set keys = arr[i].keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String keyss = (String) it.next();
				//每个keys的属性项可能由一个字符串组成，或是有一个空格分隔的字符串组成，带空格的字符串有两种情况（命名：空格前为属性项，空格后为属性名）
				//1、相同属性项下的不同属性名（如：“Provider.id name” 和 “Provider.id version”）
				//2、不同属性项下的相同属性名（如：“TransformService.http://www.w3.org/2001/10/xml-exc-c14n# MechanismType” 和 “TransformService.http://www.w3.org/TR/1999/REC-xpath-19991116 MechanismType”）
				String key = keyss.split(" ")[0];
				
				if (keyss.startsWith("MessageDigest.")) {
					System.out.println(keyss+"\t\t" + keyss.substring("MessageDigest.".length()));
				}
				if (keyss.startsWith("Alg.Alias.MessageDigest.")) {
					System.out.println(keyss+"\t\t" + keyss.substring("Alg.Alias.MessageDigest.".length()));
				}
				//System.out.println(keyss+"\t\t" + key + "\t" + arr[i].get(key)+"\t" + arr[i].get(keyss));
			}
		}
	}
}
