/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import config.DBManager;
import config.LoginBean;
import entity.Event;
import entity.Reservation;
import entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

/**
 *
 * @author ADAM
 */
@ManagedBean(name = "publicEventBean")
@ViewScoped
public class PublicEventBean extends CheckBean {
    
    @ManagedProperty(value="#{loginBean}")
    private LoginBean loginBean;

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent();
    private Event e = new Event();
    private User user = new User();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getE() {
        return e;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        eventModel = initialize();
        e = new Event();
    }

    private ScheduleModel initialize() {
        ScheduleModel eventModel = new DefaultScheduleModel();
        List<Event> eventList = new ArrayList<>();
        EntityManager em = DBManager.getManager().createEntityManager();
        eventList = em.createQuery("SELECT e FROM Event e WHERE e.type=0").getResultList();
        for (Event e : eventList) {
            eventModel.addEvent(new DefaultScheduleEvent(e.getName(), e.getDate(), e.getDate(), e));
        }
        em.close();
        return eventModel;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        this.event = (ScheduleEvent) selectEvent.getObject();
        this.e = (Event)this.event.getData();
    }
    public int freeTicket(){
        if (e.getIdshow()!=null)
            return super.freeTicket(e);
        else return 0;
    }
    public void book(){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        FacesContext context = FacesContext.getCurrentInstance();
        int id = (Integer) session.getAttribute("id");
        if (this.freeTicket()<=0){
            context.addMessage(null, new FacesMessage("Przykro nam", "Ktoś w czasie gdy się zastanawiałeś zarezerwował Twoje miejsce") );
            return;
        }
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        Reservation tmpRes = new Reservation();
        tmpRes.setIdreservation(null);
        tmpRes.setIduser(em.find(User.class, id));
        tmpRes.setChecked(1);
        tmpRes.setIdshow(e);
        em.persist(tmpRes);
        em.getTransaction().commit();
        em.close();
        try{
            MailSender.sendEmailWithAttachments(loginBean.getCheckUser().getEmail(), "Potwierdzenie rezerwacji miejsca na wydarzenie", "Dziękujemy!Zarezerwowales miejsce na wydarzenie "+e.getName());
        }catch(Exception e){
            e.printStackTrace();
        }
        context.addMessage(null, new FacesMessage("Udało się!", "Twoje miejsce na impreze zostało zarezerwowane"));
    }
    public void bookPublic(){
        FacesContext context = FacesContext.getCurrentInstance();
        if (this.freeTicket()<=0){
            context.addMessage(null, new FacesMessage("Przykro nam", "Ktoś w czasie gdy się zastanawiałeś zarezerwował Twoje miejsce") );
            return;
        }
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        this.user.setIduser(null);
        em.persist(this.user);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Reservation tmpRes = new Reservation();
        tmpRes.setIdreservation(null);
        tmpRes.setIduser((User)em.createNamedQuery("User.findByName").setParameter("firstName", this.user.getFirstName()).setParameter("secondName", this.user.getSecondName()).getResultList().get(0));
        tmpRes.setChecked(1);
        tmpRes.setIdshow(e);
        em.persist(tmpRes);
        em.getTransaction().commit();
        em.close();
        context.addMessage(null, new FacesMessage("Udało się!", "Twoje miejsce na impreze zostało zarezerwowane"));
    }
}
