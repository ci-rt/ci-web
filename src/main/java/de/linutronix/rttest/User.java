package de.linutronix.rttest;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import de.linutronix.rttest.db.Passwd;
import de.linutronix.rttest.util.CiRTLCred;
import de.linutronix.rttest.util.HibernateHttpServlet;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

/**
 *
 * @author bene
 */
@WebServlet("/admin/User")
public class User extends HibernateHttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private static final Logger LOGGER = Logger.getLogger("de.linutronix.rttest");

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(final ServletConfig config) throws ServletException {
        addAnnotatedClass(Passwd.class);
        super.init(config);
    }

    /**
     *
     * @param real
     * @param user
     * @param pwd
     * @throws ServletException
     */
    private void newUser(final String real, final String user, final String pwd)
            throws ServletException {
        try {
            CiRTLCred cred = new CiRTLCred(user, pwd);
            Passwd passwd = new Passwd(cred.getUser(), real, cred.getSalt());

            LOGGER.log(Level.INFO, "passwd = [{0} , {1}]",
                    new Object[]{passwd.getUser(),
                        Arrays.toString(passwd.getSalt())});

            try (Session session = openSession()) {
                session.beginTransaction();
                session.save(passwd);
                session.createNativeQuery("CREATE USER '" + cred.getUser()
                        + "' WITH PASSWORD '" + cred.getPasswd() + "';");
                session.getTransaction().commit();
            }
            LOGGER.log(Level.INFO, "new user ''{0}'' created.", cred.getUser());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException("could not create new user:"
                                       + ex.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException("Crypto failure:" + ex.getMessage());
        }
    }

    /**
     *
     * @param user
     * @throws ServletException
     */
    private void delUser(final String user) throws ServletException {
        try (Session session = openSession()) {
            session.beginTransaction();
            Passwd passwd = session
                    .byNaturalId(Passwd.class)
                    .using("username", user)
                    .load();
            session.delete(passwd);
            session.createNativeQuery("DROP ROLE " + user + ";");
            session.getTransaction().commit();
        }
        LOGGER.log(Level.INFO, "user ''{0}'' removed.", user);
    }

    /**
     *
     * @param user
     * @throws ServletException
     */
    private void toggleUser(final String user) throws ServletException {
        Passwd passwd;
        try (Session session = openSession()) {
            session.beginTransaction();
            passwd = session
                    .byNaturalId(Passwd.class)
                    .using("username", user)
                    .load();
            passwd.setUserStatus(!passwd.getUserStatus());
            session.getTransaction().commit();
        }

        String status;
        if (passwd.getUserStatus()) {
            status = "enabled";
        } else {
            status = "disabled";
        }
        LOGGER.log(Level.INFO, "user status toggled: ''{0}'' {1}.",
                new Object[]{user, status});
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        Transaction tx;
        List<Passwd> list;
        try (Session session = openSession()) {
            tx = session.beginTransaction();
            Query<Passwd> q = session.createQuery("from Passwd p", Passwd.class);
            list = q.getResultList();
            tx.commit();
        }
        request.setAttribute("userlist", list);
        this.getServletContext()
                .getRequestDispatcher("/admin/user.jsp")
                .include(request, response);
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
        String cmd = request.getParameter("cmd");
        String real = request.getParameter("j_realname");
        String user = request.getParameter("j_username");
        String pwd = request.getParameter("j_password");

        LOGGER.log(Level.INFO, "User.doPost() cmd = {0}", cmd);

        switch (cmd.toLowerCase()) {
            case "new":
                newUser(real, user, pwd);
                break;
            case "del":
                delUser(user);
                break;
            case "toggle":
                toggleUser(user);
                break;
            default:
                throw new ServletException("unknown command: '" + cmd + "'");
        }
        response.sendRedirect("User");
    }
}
