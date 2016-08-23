/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package config;

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
@FacesConverter("userConverter")
public class UserConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        System.out.println("jestem w konwerterze" + string);
        Integer i = Integer.valueOf(string);
        EntityManager em = DBManager.getManager().createEntityManager();
        em.setFlushMode(FlushModeType.COMMIT);
        User p = em.find(User.class, i);
        em.close();
        return p;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        System.out.println(o);
        if (! (o instanceof User))
            throw new ConverterException(new FacesMessage("błąd"));
        User p = (User)o;
        return p.getIduser().toString();
    }

}
