package de.linutronix.rttest.util;

import de.linutronix.rttest.Listener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CiRTLCred implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private static final String PBKDFALGORITHM = "PBKDF2WithHmacSHA256";

    /**
     *
     */
    private static final int KEYLENGTH = 256;

    /**
     *
     */
    private static final int KEYITERATIONS = 20000;

    /**
     *
     */
    private static final Logger LOGGER = Logger.getLogger("de.linutronix.rttest");

    /**
     *
     */
    private final String user;

    /**
     *
     */
    private final String passwd;

    /**
     *
     */
    private final byte[] salt;

    /**
     *
     */
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    private byte[] getEncryptedPassword(final String password,
                                        final byte[] newsalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        String algorithm = PBKDFALGORITHM;
        int derivedKeyLength = KEYLENGTH;
        int iterations = KEYITERATIONS;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), newsalt, iterations,
                                      derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);

        return f.generateSecret(spec).getEncoded();
    }

    /**
     *
     * @param newuser
     * @param pwd
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws InvalidKeySpecException
     */
    public CiRTLCred(final String newuser, final String pwd)
            throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] newsalt = new byte[Listener.SALTSIZE];
        SecureRandom.getInstance(Listener.RNGNAME).nextBytes(newsalt);
        byte[] dk = getEncryptedPassword(pwd, newsalt);

        this.user = newuser;
        this.salt = newsalt;
        this.passwd = Base64.getEncoder().encodeToString(dk);

        LOGGER.log(Level.INFO, "user,pwd: Passwd: {0} Salt: {1}",
                new Object[]{passwd, Base64.getEncoder().encodeToString(this.salt)});
    }

    /**
     *
     * @param actuser
     * @param pwd
     * @param actsalt
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public CiRTLCred(final String actuser, final String pwd, final byte[] actsalt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] dk = getEncryptedPassword(pwd, actsalt);

        this.user = actuser;
        this.salt = actsalt.clone();
        this.passwd = Base64.getEncoder().encodeToString(dk);
        LOGGER.log(Level.INFO, "user,pwd,salt: Passwd: {0} Salt: {1}",
                new Object[]{passwd, Base64.getEncoder().encodeToString(this.salt)});
    }

    /**
     *
     * @param cookie
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     */
    public CiRTLCred(final String cookie)
            throws IOException, ClassNotFoundException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {
        Path path = Paths.get(TMP_DIR, "salt");
        byte[] fsalt = Files.readAllBytes(path);
        SecretKeySpec key = new SecretKeySpec(fsalt, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] encdata = Base64.getDecoder().decode(cookie);
        byte[] iv = Arrays.copyOfRange(encdata, 0, 16);
        byte[] cdata = Arrays.copyOfRange(encdata, 16, encdata.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] data = cipher.doFinal(cdata);

        CiRTLCred c;
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data))) {
            c = (CiRTLCred) ois.readObject();
        }
        this.user = c.user;
        this.passwd = c.passwd;
        this.salt = c.salt;
        LOGGER.log(Level.INFO, "cookie: Passwd: {0} Salt: {1}",
                new Object[]{this.passwd,
                    Base64.getEncoder().encodeToString(this.salt)});
    }

    /**
     *
     * @param request
     * @return CiRTLCred
     * @throws ServletException
     * @throws IOException
     */
    public static CiRTLCred getCred(final HttpServletRequest request)
            throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                String name = c.getName();

                if (name.equals("CI-RTL")) {
                    try {
                        return new CiRTLCred(c.getValue());
                    } catch (IOException | ClassNotFoundException
                            | InvalidAlgorithmParameterException
                            | InvalidKeyException | NoSuchAlgorithmException
                            | BadPaddingException | IllegalBlockSizeException
                            | NoSuchPaddingException ex) {
                        c.setValue("");
                        c.setPath("/");
                        c.setMaxAge(0);
                        request.setAttribute("CI-RTL", c);
                        throw new ServletException(ex);
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @return actual user
     */
    public String getUser() {
        return this.user;
    }

    /**
     *
     * @return actual salt
     */
    public byte[] getSalt() {
        return this.salt.clone();
    }

    /**
     *
     * @return actual password
     */
    public String getPasswd() {
        return this.passwd;
    }

    /**
     *
     * @return cookie string
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidParameterSpecException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public String toCookie() throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            InvalidParameterSpecException, IllegalBlockSizeException,
            BadPaddingException {
        Path path = Paths.get(TMP_DIR, "salt");
        byte[] xsalt = Files.readAllBytes(path);
        SecretKeySpec key = new SecretKeySpec(xsalt, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        AlgorithmParameters params = cipher.getParameters();
        byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();

        LOGGER.log(Level.INFO, "IV length: {0}", iv.length);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
        }

        byte[] data = baos.toByteArray();
        byte[] cdata = cipher.doFinal(data);

        byte[] out = new byte[iv.length + cdata.length];
        System.arraycopy(iv, 0, out, 0, iv.length);
        System.arraycopy(cdata, 0, out, iv.length, cdata.length);

        return Base64.getEncoder().encodeToString(out);
    }

    /**
     *
     * @param entry
     * @param keystoreLocation
     * @param keyStorePassword
     * @return actual password
     * @throws Exception
     */
    public static String getPasswordFromKeystore(final String entry,
                                                 final String keystoreLocation,
                                                 final String keyStorePassword)
            throws Exception {

        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(null, keyStorePassword.toCharArray());
        KeyStore.PasswordProtection keyStorePP =
                new KeyStore.PasswordProtection(keyStorePassword.toCharArray());

        try (FileInputStream fin = new FileInputStream(keystoreLocation)) {
            ks.load(fin, keyStorePassword.toCharArray());
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");

        KeyStore.SecretKeyEntry ske
                = (KeyStore.SecretKeyEntry) ks.getEntry(entry, keyStorePP);

        PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(
                ske.getSecretKey(),
                PBEKeySpec.class);

        char[] password = keySpec.getPassword();

        return new String(password);

    }

    /**
     *
     * @param entry
     * @param entryPassword
     * @param keyStoreLocation
     * @param keyStorePassword
     * @throws Exception
     */
    public static void makeNewKeystoreEntry(final String entry,
                                            final String entryPassword,
                                            final String keyStoreLocation,
                                            final String keyStorePassword)
            throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBE");
        SecretKey generatedSecret
                = factory.generateSecret(new PBEKeySpec(
                        entryPassword.toCharArray()));

        KeyStore ks = KeyStore.getInstance("JCEKS");
        ks.load(null, keyStorePassword.toCharArray());
        KeyStore.PasswordProtection keyStorePP =
                new KeyStore.PasswordProtection(keyStorePassword.toCharArray());

        ks.setEntry(entry, new KeyStore.SecretKeyEntry(
                generatedSecret), keyStorePP);

        try (FileOutputStream fos = new FileOutputStream(keyStoreLocation)) {
            ks.store(fos, keyStorePassword.toCharArray());
        }
    }
}
