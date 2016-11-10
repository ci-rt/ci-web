package de.linutronix.rttest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;

public class Listener implements javax.servlet.ServletContextListener {
	public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private static Logger logger = Logger.getLogger("de.linutronix.rttest");
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			Files.delete(Paths.get(TMP_DIR, "salt"));
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.WARNING, "context destroy failed", e);
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			SecureRandom secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
			
			byte[] salt = new byte[32];
			secureRandomGenerator.nextBytes(salt);
			Files.write(Paths.get(TMP_DIR, "salt"), salt);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "context creation failed", e);
		}
	}
}
