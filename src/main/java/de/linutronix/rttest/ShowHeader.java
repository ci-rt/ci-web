package de.linutronix.rttest;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.linutronix.rttest.util.CiRTLCred;
import java.util.Arrays;

@WebServlet("/ShowHeader")
public class ShowHeader extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("This is the Test Servlet");

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            out.print("<br/>Header Name: <em>" + headerName);
            String headerValue = request.getHeader(headerName);
            out.print("</em>, Header Value: <em>" + headerValue);
            out.println("</em>");
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                String name = c.getName();
                String value = c.getValue();

                if (name.equals("CI-RTL")) {
                    try {
                        CiRTLCred cred = new CiRTLCred(value);

                        out.print("<br/>CI-RTL Keks User: <em>"
                                + cred.getUser() + "</em>");
                        out.print("<br/>CI-RTL Keks Pass: <em>"
                                + cred.getPasswd() + "</em>");
                        out.print("<br/>CI-RTL Keks Salt: <em>"
                                + Arrays.toString(cred.getSalt()) + "</em>");
                    } catch (InvalidKeyException | ClassNotFoundException
                            | NoSuchAlgorithmException | NoSuchPaddingException
                            | IllegalBlockSizeException | BadPaddingException
                            | InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    }
                    // TODO Auto-generated catch block

                } else {
                    out.print("<br/>Keks Name: <em>" + name);
                    out.print("</em>, Keks Value: <em>" + value);
                    out.println("</em>");
                }
            }
        }
    }
}
