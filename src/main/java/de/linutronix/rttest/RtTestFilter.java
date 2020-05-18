package de.linutronix.rttest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import de.linutronix.rttest.util.CiRTLCred;

/**
 *
 * @author bene
 */
@WebFilter("/jspFilter")
public class RtTestFilter implements Filter {
    /**
     *
     */
    private ServletContext context;

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(final FilterConfig config) throws ServletException {
        this.context = config.getServletContext();
        this.context.log("AuthenticationFilter initialized");
    }

    /**
     *
     */
    @Override
    public void destroy() {
    }

    /**
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        CiRTLCred cred = CiRTLCred.getCred(req);

        if (cred == null) {
            req.setAttribute("realname", null);
            req.setAttribute("user", null);
        } else {
            req.setAttribute("realname", null);
            req.setAttribute("user", cred.getUser());
        }

        chain.doFilter(request, response);
    }
}
