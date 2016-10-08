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
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ADAM
 */
@ManagedBean(name = "invitationBean")
@RequestScoped
public class InvitationBean extends CheckBean{

    private List<Reservation> invitationList = new ArrayList<>();
    private Reservation invitation = new Reservation();
    
    @ManagedProperty(value="#{loginBean}")
    private LoginBean loginBean;

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public Reservation getInvitation() {
        return invitation;
    }

    public void setInvitation(Reservation invitation) {
        this.invitation = invitation;
    }

    public List<Reservation> getInvitationList() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        int id = (Integer) session.getAttribute("id");
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        invitationList = em.createQuery("SELECT r FROM Reservation r JOIN r.iduser iduser WHERE r.checked=0 AND iduser.iduser=:id").setParameter("id", id).getResultList();
        em.getTransaction().commit();
        em.close();
        return invitationList;
    }

    public void setInvitationList(List<Reservation> invitationList) {
        this.invitationList = invitationList;
    }

    public void confirm() {
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        this.invitation.setChecked(1);
        em.merge(this.invitation);
        em.getTransaction().commit();
        em.close();
        try{
            MailSender.sendEmailWithAttachments(loginBean.getCheckUser().getEmail(), "Potwierdzenie rezerwacji miejsca na wydarzenie", "Dziekujemy!Zarezerwowales miejsce na wydarzenie "+this.invitation.getIdshow().getName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void discard(){
        EntityManager em = DBManager.getManager().createEntityManager();
        em.getTransaction().begin();
        invitation = em.find(Reservation.class, invitation.getIdreservation());
        em.remove(this.invitation);
        em.getTransaction().commit();
        em.close();
    }
    public int freeTicket(){
       //return super.freeTicket(invitation.getIdshow());
       if (invitation.getIdreservation()!=null)
            return super.freeTicket(invitation.getIdshow());
        else return 0;
    }
}
