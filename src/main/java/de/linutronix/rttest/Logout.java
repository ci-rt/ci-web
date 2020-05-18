package de.linutronix.rttest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login/Logout")
public class Logout extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                String name = c.getName();

                if (name.equals("CI-RTL")) {
                    c.setValue("");
                    c.setPath("/");
                    c.setMaxAge(0);
                    response.addCookie(c);
                }
            }
        }

        String rqHdr = request.getHeader("referer");
        if (rqHdr != null) {
            response.sendRedirect(rqHdr);
        }
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
        doGet(request, response);
    }
}
