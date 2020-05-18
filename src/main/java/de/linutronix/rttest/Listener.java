package de.linutronix.rttest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

/**
 *
 * @author bene
 */
public class Listener implements javax.servlet.ServletContextListener {

    /**
     *
     */
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     *
     */
    public static final String RNGNAME = "SHA1PRNG";

    /**
     *
     */
    public static final int SALTSIZE = 32;

    /**
     *
     */
    private static final Logger LOGGER = Logger.getLogger("de.linutronix.rttest");

    /**
     *
     * @param arg0
     */
    @Override
    public void contextDestroyed(final ServletContextEvent arg0) {
        try {
            Files.delete(Paths.get(TMP_DIR, "salt"));
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, "context destroy failed", e);
        }
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void contextInitialized(final ServletContextEvent arg0) {
        try {
            SecureRandom secureRandomGenerator = SecureRandom.getInstance(RNGNAME);

            byte[] salt = new byte[SALTSIZE];
            secureRandomGenerator.nextBytes(salt);
            Files.write(Paths.get(TMP_DIR, "salt"), salt);
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "context creation failed", e);
        }
    }
}
