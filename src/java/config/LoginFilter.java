/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.IOException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Adam
 */
public class LoginFilter implements Filter {

    private static final boolean debug = false;

    private FilterConfig filterConfig = null;

    public LoginFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession ses = req.getSession(false);
        String reqURI = req.getRequestURI();
        //  FacesContext context = FacesContext.getCurrentInstance();
        //  System.out.println(reqURI.indexOf("/faces/index.xhtml"));
        //System.out.println(ses.getAttribute("type"));
        if (reqURI.indexOf("/faces/index.xhtml") == -1 && ses.getAttribute("id") == null && !(req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER))) {
            // context.addMessage(null, new FacesMessage("UWAGA!", "Wykryto nieuprawniony dostęp!"));
            res.sendRedirect(req.getContextPath() + "/faces/index.xhtml");
        } else if ((reqURI.indexOf("/faces/show_admin.xhtml") > 10 || reqURI.indexOf("/faces/users.xhtml") > 10 || reqURI.indexOf("/faces/check_user.xhtml") > 10) && (Integer) (ses.getAttribute("type")) != 2) {
            res.sendRedirect(req.getContextPath() + "/faces/index.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                System.out.println("init");
            }
        }
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
