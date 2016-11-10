package de.linutronix.rttest.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.rtner.security.auth.spi.*;

public class CiRTLCred implements Serializable
{
	private static Logger logger = Logger.getLogger("de.linutronix.rttest");
	private String User;
	private String Passwd;
	private byte[] Salt;
	private static final long serialVersionUID = 1L;
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	
	public CiRTLCred(String user, String pwd) throws NoSuchAlgorithmException, IOException
	{
		byte[] salt = new byte[32];
		SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
		PBKDF2Parameters p = new PBKDF2Parameters("HmacSHA256", "UTF-8", salt, 10000);
		byte[] dk = new PBKDF2Engine(p).deriveKey(pwd);
		
		User = user;
		Salt = salt;
		Passwd = Base64.getEncoder().encodeToString(dk);
	}

	public CiRTLCred(String user, String pwd, byte[] salt)
	{
		PBKDF2Parameters p = new PBKDF2Parameters("HmacSHA256", "UTF-8", salt, 10000);
		byte[] dk = new PBKDF2Engine(p).deriveKey(pwd);
		
		User = user;
		Salt = salt;
		Passwd = Base64.getEncoder().encodeToString(dk);
	}
	
	public CiRTLCred(String cookie)throws IOException, ClassNotFoundException,
										  NoSuchAlgorithmException, NoSuchPaddingException,
										  InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
										  InvalidAlgorithmParameterException
	{
		Path path = Paths.get(TMP_DIR, "salt");
		byte[] salt = Files.readAllBytes(path);
		SecretKeySpec key = new SecretKeySpec(salt, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		
		byte[] encdata = Base64.getDecoder().decode(cookie);
		byte[] iv = Arrays.copyOfRange(encdata, 0, 16);
		byte[] cdata = Arrays.copyOfRange(encdata, 16, encdata.length);
		
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		byte[] data = cipher.doFinal(cdata);

		ObjectInputStream ois = new ObjectInputStream( 
				new ByteArrayInputStream(data));
		CiRTLCred c = (CiRTLCred) ois.readObject();
		ois.close();
		this.User = c.User;
		this.Passwd = c.Passwd;
		this.Salt = c.Salt;
	}

	public String GetUser()
	{
		return this.User;
	}
	
	public byte[] GetSalt()
	{
		return this.Salt;
	}
	
	public String GetPasswd()
	{
		return this.Passwd;
	}
	
	public String ToCookie() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
									InvalidKeyException, InvalidParameterSpecException,
									IllegalBlockSizeException, BadPaddingException {
		Path path = Paths.get(TMP_DIR, "salt");
		byte[] salt = Files.readAllBytes(path);
		SecretKeySpec key = new SecretKeySpec(salt, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		AlgorithmParameters params = cipher.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

		logger.log(Level.INFO, "IV length: " + iv.length);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(this);
		oos.close();
		
		byte[] data = baos.toByteArray();
		byte[] cdata = cipher.doFinal(data);
		
		byte[] out = new byte[iv.length + cdata.length];
		System.arraycopy(iv, 0, out, 0, iv.length);
		System.arraycopy(cdata, 0, out, iv.length, cdata.length);

		return Base64.getEncoder().encodeToString(out);
    }
}
