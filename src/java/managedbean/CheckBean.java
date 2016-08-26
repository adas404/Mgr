/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package managedbean;

import config.DBManager;
import entity.Event;
import entity.User;
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
    public boolean checkUser(String login){
        EntityManager em = DBManager.getManager().createEntityManager();
        List<User> userList = em.createQuery("SELECT u FROM User u WHERE u.login=:login").setParameter("login", login).getResultList();
        if(userList.isEmpty())
            return false;
        else
            return true;
    }
}
