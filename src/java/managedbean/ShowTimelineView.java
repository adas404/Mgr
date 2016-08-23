/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import config.DBManager;
import entity.Reservation;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
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
