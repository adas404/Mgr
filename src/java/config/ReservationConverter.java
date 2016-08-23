/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

import entity.Reservation;
import entity.User;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;



/**
 *
 * @author Adam
 */
@FacesConverter("reservationConverter")
public class ReservationConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        Integer i = Integer.valueOf(string);
        EntityManager em = DBManager.getManager().createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        User p = em.find(User.class, i);
        em.close();
        return p;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (! (o instanceof Reservation))
            throw new ConverterException(new FacesMessage("błąd"));
        Reservation p = (Reservation)o;
        return p.getIduser().toString();
    }

}
