/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import config.DBManager;
import entity.Event;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author ADAM
 */
public class CheckBean {

    public int freeTicket(Event e) {
        EntityManager em = DBManager.getManager().createEntityManager();
        List<Event> freeTicket = em.createQuery("SELECT r FROM Reservation r WHERE r.idshow.idshow=:id AND r.checked=1").setParameter("id", e.getIdshow()).getResultList();
        em.close();
        return abs(freeTicket.size()-e.getNumSeats());
    }
}
