/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import config.DBManager;
import entity.Event;
import entity.Reservation;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
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
@ManagedBean(name = "showBean")
@ViewScoped
public class ShowBean extends CheckBean {

    private Event show = new Event();
    private List<Event> showList = new ArrayList<>();
    private List<User> selectedUsers = new ArrayList<>();
    private List<Reservation> takePartUser = new ArrayList<>();
    private Reservation res = new Reservation();

    public Reservation getRes() {
        return res;
    }

    public void setRes(Reservation res) {
        this.res = res;
    }

    public List<Reservation> getTakePartUser() {
        return takePartUser;
    }

    public void setTakePartUser(List<Reservation> takePartUser) {
        this.takePartUser = takePartUser;
    }

    public int freeTicket() {
        //return super.freeTicket(invitation.getIdshow());
        if (show.getIdshow() != null) {
            return super.freeTicket(show);
        } else {
            return 0;
        }
    }

    public void prepareShow() {
        show = new Event();
        /*  show.setAddress("adres");
        show.setName("name");
        show.setNumSeats(40);
        show.setDate(new Date());*/
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<User> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public Event getShow() {
        return show;
    }

    public void setShow(Event show) {
        this.show = show;
    }

    public List<Event> getShowList() {
        EntityManager em = DBManager.getManager().createEntityManager();
        showList = em.createNamedQuery("Event.findAll").getResultList();
        em.close();
        return showList;
    }

    public void setShowList(List<Event> showList) {
        this.showList = showList;
    }

    public void refresh() {
        selectedUsers.clear();
        EntityManager em = DBManager.getManager().createEntityManager();
        List<Reservation> tmpListReservation = new ArrayList<>();
        tmpListReservation = em.createQuery("SELECT r FROM Reservation r WHERE r.idshow.idshow=:s").setParameter("s", show.getIdshow()).getResultList();
        for (Reservation r : tmpListReservation) {
            selectedUsers.add(r.getIduser());
        }
        em.close();
        this.takePart();
    }

    public void addEvent() {
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        show.setIdshow(null);
        em.persist(show);
        em.getTransaction().commit();
        em.close();
    }

    public void invite() {
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        for (User u : selectedUsers) {
            Reservation tmpRes = new Reservation();
            tmpRes.setIdshow(this.show);
            tmpRes.setIduser(u);
            tmpRes.setChecked(0);
            tmpRes.setIdreservation(null);
            List<Reservation> r = new ArrayList<>();
            r = em.createQuery("SELECT r FROM Reservation r WHERE r.idshow.idshow=:s AND r.iduser.iduser=:u").setParameter("s", tmpRes.getIdshow().getIdshow()).setParameter("u", tmpRes.getIduser().getIduser()).getResultList();
            if (r.isEmpty()) {
                em.persist(tmpRes);
            }
        }
        em.getTransaction().commit();
        em.close();
        FacesContext contex = FacesContext.getCurrentInstance();
        contex.addMessage(null, new FacesMessage("Gratulacje", "Wysłano zaproszenie!"));
    }

    public void deleteEvent() {
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        show = em.find(Event.class, show.getIdshow());
        em.remove(show);
        em.getTransaction().commit();
        em.close();
    }

    public void takePart() {
        EntityManager em = DBManager.getManager().createEntityManager();
        takePartUser = em.createQuery("SELECT r FROM Reservation r WHERE r.idshow.idshow=:idshow AND r.checked=1").setParameter("idshow", show.getIdshow()).getResultList();
        em.close();
    }
    public String kickOut(){
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        this.res = em.find(Reservation.class, res.getIdreservation());
        em.remove(this.res);
        em.getTransaction().commit();
        em.close();
        return "show_admin.xhtml";
    }
}
