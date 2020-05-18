package de.linutronix.rttest;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;

import de.linutronix.rttest.db.Passwd;
import de.linutronix.rttest.util.CiRTLCred;
import de.linutronix.rttest.util.DbConf;
import de.linutronix.rttest.util.HibernateHttpServlet;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 *
 * @author bene
 */
@WebServlet("/login/Login")
public class Login extends HibernateHttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private static final Logger LOGGER = Logger.getLogger("de.linutronix.rttest");

    /**
     *
     */
    private static final int SEC_PER_HOUR = 3600;

    /**
     *
     */
    private String url;

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        addAnnotatedClass(Passwd.class);

        super.init(config);
        DbConf conf = new DbConf(config);

        url = conf.getURL();
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        // get request parameters for userID and password
        String user = request.getParameter("j_username");
        String pwd = request.getParameter("j_password");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Passwd passwd;
        try (Session hsession = openSession()) {
            hsession.beginTransaction();
            passwd = hsession
                    .byNaturalId(Passwd.class)
                    .using("username", user)
                    .load();
            hsession.getTransaction().commit();
        } catch (Exception ex) {
            response.sendError(SC_SERVICE_UNAVAILABLE);
            return;
        }

        CiRTLCred userCred;
        Cookie rtUser;

        try {
            userCred = new CiRTLCred(user, pwd, passwd.getSalt());
            rtUser = new Cookie("CI-RTL", userCred.toCookie());
        } catch (InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidParameterSpecException
                | IllegalBlockSizeException | BadPaddingException
                | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        LOGGER.log(Level.INFO, "{0} {1}", new Object[]{userCred.getUser(),
                   userCred.getPasswd()});

        try {
            Connection con;
            con = DriverManager.getConnection(url, userCred.getUser(),
                                              userCred.getPasswd());
            con.close();
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath()
                    + "/login/login-failed.html");
            return;
        }

        session = request.getSession();
        session.setAttribute("username", passwd.getRealname());
        // setting cookie to expiry in 60 mins
        rtUser.setMaxAge(SEC_PER_HOUR);
        rtUser.setHttpOnly(true);
        rtUser.setPath("/");
        response.addCookie(rtUser);

        response.sendRedirect("/");
    }
}
