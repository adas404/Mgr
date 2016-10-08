/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;
import config.DBManager;
import config.LoginBean;
import entity.Reservation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;

/**
 *
 * @author ADAM
 */
@ManagedBean(name = "showTimelineView")
@ViewScoped
public class ShowTimelineView {
    
    @ManagedProperty(value="#{loginBean}")
    private LoginBean loginBean;

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
    
    private TimelineModel model;
    private Reservation reservation;

    public TimelineModel getModel() {
        return model;
    }

    public void setModel(TimelineModel model) {
        this.model = model;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @PostConstruct
    void init() {
        model = new TimelineModel();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        int id = (Integer) session.getAttribute("id");
        EntityManager em = DBManager.getManager().createEntityManager();
        List<Reservation> reservationList = new ArrayList<>();
        reservationList = em.createQuery("SELECT r FROM Reservation r JOIN r.iduser iduser WHERE r.checked=1 AND iduser.iduser=:id").setParameter("id", id).getResultList();
        for (Reservation r : reservationList) {
            model.add(new TimelineEvent(r, r.getIdshow().getDate()));
        }
        reservation = new Reservation();
    }

    public String deleteRes(){
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
       reservation = em.find(Reservation.class, reservation.getIdreservation());
        em.remove(reservation);
        em.getTransaction().commit();
        em.close();
        
        try{
            MailSender.sendEmailWithAttachments(loginBean.getCheckUser().getEmail(), "Potwierdzenie rezygnacji z wydarzenia", "Przykro nam! Zrezygnowales z wydarzenia "+reservation.getIdshow().getName());
        }catch(Exception e){
            e.printStackTrace();
        }
        this.init();
        return "confirm_invitation.xhtml";
   }
    
    public boolean copareToActualDate(){
        Date date = new Date();
        if (date.getTime()>reservation.getIdshow().getDate().getTime())
            return true;
        else return false;
    } 
    private Calendar dateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    public void onSelect(TimelineSelectEvent e){
        TimelineEvent timelineEvent = e.getTimelineEvent();
        reservation =  (Reservation) timelineEvent.getData();
    }
    
}
