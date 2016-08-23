/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import entity.User;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ADAM
 */
@ManagedBean(name = "loginBean")
@SessionScoped
public class LoginBean {
    private boolean notLogged = true;
    private User user = new User();
    private User checkUser = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isNotLogged() {
        return notLogged;
    }

    public void setNotLogged(boolean notLogged) {
        this.notLogged = notLogged;
    }

    public String checkLogin(){
        EntityManager em = DBManager.getManager().createEntityManager();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        try{
            checkUser = (User) em.createQuery("SELECT u FROM User u WHERE u.login=:log AND u.password=:pass")
                    .setParameter("log", this.user.getLogin()).setParameter("pass", this.user.getPassword()).getSingleResult();
            HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false); 
            session.setAttribute("id", checkUser.getIduser());
            session.setAttribute("type", checkUser.getType());
            this.notLogged = false;
        }catch(NoResultException e){
            user = new User();
            this.notLogged = true;
            return "index";
        }
        return "index";
    }
    public String logout(){
        HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        this.notLogged = true;
        return "index";
    }
}
