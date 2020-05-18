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

import de.linutronix.rttest.db.Usergroups;
import de.linutronix.rttest.util.HibernateHttpServlet;

@WebServlet("/admin/Group")
public class Group extends HibernateHttpServlet {
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
        addAnnotatedClass(Usergroups.class);
        super.init(config);
    }

    /**
     *
     * @param groupname
     */
    private void newGroup(final String groupname) throws ServletException {
        Usergroups group = new Usergroups(groupname);

        try (Session session = openSession()) {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
        }
        LOGGER.log(Level.INFO, "new group ''{0}'' created.", groupname);
    }

    /**
     *
     * @param groupname
     */
    private void delGroup(final String groupname) throws ServletException {
        try (Session session = openSession()) {
            session.beginTransaction();
            Usergroups group = session
                    .byNaturalId(Usergroups.class)
                    .using("groupname", groupname)
                    .load();
            session.delete(group);
            session.getTransaction().commit();
        }
        LOGGER.log(Level.INFO, "group ''{0}'' removed.", groupname);
    }

    /**
     *
     * @param groupname
     */
    private void toggleGroup(final String groupname) throws ServletException {
        Usergroups group;
        try (Session session = openSession()) {
            session.beginTransaction();
            group = session
                    .byNaturalId(Usergroups.class)
                    .using("groupname", groupname)
                    .load();
            group.setGroupStatus(!group.getGroupStatus());
            session.getTransaction().commit();
        }
        LOGGER.log(Level.INFO, "group status toggled: {0}", group);
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
        List<Usergroups> list;
        try (Session session = openSession()) {
            Transaction tx = session.beginTransaction();
            LOGGER.log(Level.INFO, "--> {0}", Usergroups.class.getName());
            Query<Usergroups> q =
                    session.createQuery("from Usergroups g", Usergroups.class);
            list = q.getResultList();
            tx.commit();
        }
        request.setAttribute("grouplist", list);
        this.getServletContext()
                .getRequestDispatcher("/admin/group.jsp")
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
        String groupname = request.getParameter("j_groupname");

        LOGGER.log(Level.INFO, "Group.doPost() cmd = {0}", cmd);

        switch (cmd.toLowerCase()) {
            case "new":
                newGroup(groupname);
                break;
            case "del":
                delGroup(groupname);
                break;
            case "toggle":
                toggleGroup(groupname);
                break;
            default:
                throw new ServletException("unknown command: '" + cmd + "'");
        }
        response.sendRedirect("Group");
    }
}
