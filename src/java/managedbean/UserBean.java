/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import config.DBManager;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

/**
 *
 * @author ADAM
 */
@ManagedBean(name = "userBean")
@RequestScoped
public class UserBean extends CheckBean {
    private User user = new User();
    private List<User> users = new ArrayList<User>();
    private List<User> uncheckedUser = new ArrayList<User>();
    
    public List<User> getUncheckedUser() {
        EntityManager em = DBManager.getManager().createEntityManager();
        uncheckedUser = em.createQuery("SELECT u From User u WHERE u.checked=0").getResultList();
        em.close();
        return uncheckedUser;
    }

    public void setUncheckedUser(List<User> uncheckedUser) {
        this.uncheckedUser = uncheckedUser;
    }

    public List<User> getUsers() {
        EntityManager em = DBManager.getManager().createEntityManager();
       // users = em.createNamedQuery("User.findAll").getResultList();
        users = em.createQuery("SELECT u FROM User u WHERE u.checked=:c").setParameter("c", 1).getResultList();
        em.close();
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    
    public void checkUser(){
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        this.user.setChecked(1);
        em.merge(this.user);
        em.getTransaction().commit();
        em.close();
        this.user= new User();
    }
    public void addUser(int checked){
        FacesContext context = FacesContext.getCurrentInstance();
        if(super.checkUser(user.getLogin())){
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Uwaga!", "Istnieje już użytkownik o podanym loginie"));
            return;
        }
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        user.setIduser(null);
        user.setChecked(checked);
        if (checked == 0)
            user.setType(1);
        em.persist(this.user);
        em.getTransaction().commit();
        em.close();
        this.user = new User();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Udało się!", "Użytkownik zarejestrowany"));
        
    }
    public void deleteUser(){
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        this.user = em.find(User.class, user.getIduser());
        em.remove(this.user);
        this.user = new User();
        em.getTransaction().commit();
        em.close();
    }

    public void editUser(){
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        em.merge(this.user);
        em.getTransaction().commit();
        em.close();
        this.user = new User();
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
