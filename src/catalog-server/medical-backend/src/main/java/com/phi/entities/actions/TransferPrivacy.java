package com.phi.entities.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.baseEntity.Transfer;
import com.phi.entities.dataTypes.IVL;
import com.phi.events.PhiEvent;
import com.phi.security.UserBean;

@Name("TransferPrivacy")
@Scope(ScopeType.SESSION)

public class TransferPrivacy implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1658138096744730228L;

	private static final Logger log = Logger.getLogger(TransferPrivacy.class);

	private static final String transferHQL = " SELECT transfer" +
			" FROM Transfer transfer" +
			" WHERE" +
			" transfer.patientEncounter.internalId = :patEncId" +
			" AND transfer.isActive = true" +
			" ORDER BY transfer.effectiveDate ASC";

    private List<IVL<Date>>enabledDateIntervals = new ArrayList<IVL<Date>>();
    private IVL<Date> filterDate = null;
    private long enabledPatientEncId;
    private final int DAY_IN_MILLISECONDS = 86400000;

    public static TransferPrivacy instance(){
        return (TransferPrivacy) Component.getInstance(TransferPrivacy.class, ScopeType.SESSION);
    }

    // Lista di intervalli di date in cui il PatientEncounter è associato a strutture su cui l'utente ha visibilità
    public List<IVL<Date>> getEnabledDateIntervals(){
        return enabledDateIntervals;
    }

    public void setEnabledDateIntervals(List<IVL<Date>> enabledDateIntervals) {
        this.enabledDateIntervals = enabledDateIntervals;
    }


    // Intervallo di date da usare come filtro nelle query
    public IVL<Date> getFilterDate() {
        return filterDate;
    }

    public void setFilterDate(IVL<Date> filterDate){
        this.filterDate = filterDate;
    }


    //
    public long getEnabledPatientEncId(){
        return enabledPatientEncId;
    }

    public void setEnabledPatientEncId(long enabledPatientEncId){
        this.enabledPatientEncId = enabledPatientEncId;
    }



    /**
     * Dato un PatientEncounter, cerca tutti i trasferimenti collegato.
     * In base alle date dei trasferimenti, ai relativi reparti di destinazione e alle strutture selezionate alla login
     * costruisce una lista di intervalli di date su cui l'utente ha visibilità
     */
    public void buildEnabledDateIntervals(Long patientEncId){

        cleanEnabledDateIntervals();

        PatientEncounter pe;
        CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

        if (patientEncId != 0){
            pe = ca.get(PatientEncounter.class, patientEncId);
        }
        // Encounter appena creato --> tutto visibile
        else {
            enabledDateIntervals.add(buildInterval(new Date(), null));
            return;
        }

        if (pe.getAssignedSDL() == null){
            log.error("PatientEncounter with internalId = " + patientEncId + " has no assignedSDL!");
            return;
        }

        enabledPatientEncId = patientEncId;
        List<Long> sdlocIds = UserBean.instance().getSdLocs();

        Query qry = ca.createQuery(transferHQL);
        qry.setParameter("patEncId",pe.getInternalId());
        List<Transfer> list = (List<Transfer>) qry.getResultList();
        Date lastEnabledSDLTransferDate = pe.getAvailabilityTime();
        Long sdl = pe.getAssignedSDL().getInternalId();

        for (int i = 0; i < list.size(); i++ ){
            if (list.get(i).getIsSupport() == null || !list.get(i).getIsSupport()){
                sdl = list.get(i).getSDLocFrom().getInternalId();
                break;
            }
        }

        Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + this.getClass().getSimpleName());

        if(!list.isEmpty()){
            if (list.get(0).getSDLocFrom() != null && sdlocIds.contains(list.get(0).getSDLocFrom().getInternalId())){
                // Primo intervallo: da data accettazione a data primo trasferimento
                enabledDateIntervals.add(buildInterval(pe.getAvailabilityTime(), list.get(0).getEffectiveDate()));
                sdl = list.get(0).getSDLocFrom().getInternalId();
            }
            else if (list.get(0).getIsSupport() != null && list.get(0).getIsSupport() && sdlocIds.contains(sdl))
                // Primo intervallo: da data accettazione a data primo trasferimento
                enabledDateIntervals.add(buildInterval(pe.getAvailabilityTime(),list.get(0).getEffectiveDate()));
            for (int i = 0; i < list.size(); i++ ){
                // Intervalli intermedi
                if (i < list.size() - 1){
                    // Trasferimento
                    if (list.get(i).getIsSupport() == null || list.get(i).getIsSupport() == false){
                        sdl = list.get(i).getSDLocTo().getInternalId();
                        if (sdlocIds.contains(sdl)){
                            enabledDateIntervals.add(buildInterval(list.get(i).getEffectiveDate(),list.get(i+1).getEffectiveDate()));
                            lastEnabledSDLTransferDate = list.get(i).getEffectiveDate();
                        }
                        if (list.get(i).getSDLocFrom() != null && sdlocIds.contains(list.get(i).getSDLocFrom().getInternalId())){
                            filterDate = buildInterval(pe.getAvailabilityTime(), addExtraDays(list.get(i).getEffectiveDate()));
                        }
                    }
                    // Appoggio
                    else {
                        if (sdlocIds.contains(list.get(i).getSDLocTo().getInternalId())||sdlocIds.contains(sdl)){
                            enabledDateIntervals.add(buildInterval(list.get(i).getEffectiveDate(),list.get(i+1).getEffectiveDate()));
                        }
                    }
                }
                // Ultimo intervallo: da data ultimo trasferimento a infinito
                else if  (i == list.size() - 1){
                    if (sdlocIds.contains(list.get(i).getSDLocTo().getInternalId())){
                        enabledDateIntervals.add(buildInterval(list.get(i).getEffectiveDate(), null));
                        filterDate = null;
                    }
                    else {
                        // Se non ho visibilità sull'ultimo reparto, e l'ultimo è un trasferimento (quindi non un appoggio), devo mettere il filtro sulla data
                        if ((list.get(i).getIsSupport() == null || list.get(i).getIsSupport() == false) &&  sdlocIds.contains(list.get(i).getSDLocFrom().getInternalId())){
                            filterDate = buildInterval(pe.getAvailabilityTime(), addExtraDays(list.get(i).getEffectiveDate()));
                        } else if (list.get(i).getIsSupport() != null && list.get(i).getIsSupport()){
                            enabledDateIntervals.add(buildInterval(lastEnabledSDLTransferDate,null));
                            if (pe.getAssignedSDL() == pe.getTemporarySDL()){
                                filterDate = buildInterval(pe.getAvailabilityTime(), addExtraDays(list.get(i).getEffectiveDate()));
                            }
                        }
                    }
                }
            }
        }

        Date from = null;
        Date to = null;

        if (filterDate != null) {
            from = filterDate.getLow();
            to = filterDate.getHigh();
        }

        Contexts.getSessionContext().set("RenderPrivacy", isRender());
        Contexts.getSessionContext().set("DatePrivacyFrom", from);
        Contexts.getSessionContext().set("DatePrivacyTo", to);
    }

    public void cleanEnabledDateIntervals() {

        enabledDateIntervals.clear();
        enabledPatientEncId = 0;
        filterDate = null;

        Contexts.getSessionContext().set("RenderPrivacy", null);
        Contexts.getSessionContext().set("DatePrivacyFrom", null);
        Contexts.getSessionContext().set("DatePrivacyTo", null);
    }


    /**
     * Ritorna true se la data in ingresso è inclusa nella lista di intervalli di date su cui l'utente ha visibilitÃ 
     * @param dateToBeChecked
     * @return
     */
    public boolean isAllowed(Date dateToBeChecked){
        for (int i = 0; i < enabledDateIntervals.size(); i++){
            if (isDateInRange(dateToBeChecked, enabledDateIntervals.get(i))){
                return true;
            }
        }
        if (enabledDateIntervals.isEmpty()){
            return true;
        }
        return false;
    }


    /**
     * Ritorna true se la data odierna è comrpesa nell'ultimo intervallo di tempo
     * @return
     */
    public boolean isRender(){
        int i = enabledDateIntervals.size();
        if (i > 0 && enabledDateIntervals.get(i - 1).getHigh() != null){
            return false;
        } else {
            return true;
        }
    }

    /**
     * Ritorna true se la data odierna è compresa nel seguente intervallo di tempo :
     * giorno di trasferimento / giorno di trasferimento + intervallo di giorni
     * per cui è permesso visualizzare informazioni di un caso che non è
     * più in carico su una struttura alla quale l'utente è autenticato.
     * @return
     */
    public boolean isVisibilityAllowed(){
        Date today = new Date();
        if (TransferPrivacy.instance().getFilterDate()!= null){
            return today.before(TransferPrivacy.instance().getFilterDate().getHigh());
        }
        return true;
    }

    /**
     * Date due date crea l'intervallo
     * @param d1
     * @param d2
     * @return
     */
    private IVL<Date> buildInterval(Date d1, Date d2){
        IVL<Date> ivl = new IVL<Date>();
        ivl.setLow(d1);
        if (d2 != null)
            ivl.setHigh(d2);
        return ivl;
    }

    /**
     * Ritorna true se low <= d < high
     * @param d
     * @param ivl
     * @return
     */
    private boolean isDateInRange (Date d,  IVL<Date> ivl){
        if (d == null)
            return false;

        Date startRange = null;
        Date endRange = null;

        if (ivl.getLow() != null){
            startRange = ivl.getLow();
        }

        if (ivl.getHigh() != null){
            endRange = ivl.getHigh();
            return ((d.after(startRange) || d.equals(startRange)) && d.before(endRange));
        } else {
            return (d.after(startRange) || d.equals(startRange));
        }
    }

    /**
     * Ritorna la data con agigunti i giorni extra configurati in seam.properties
     * @param originalDate
     * @return
     */
    private Date addExtraDays(Date originalDate) {

        String days = RepositoryManager.instance().getSeamProperty("privacyDaysCI");
        int daysExtra = 30;
        if (days != null && !days.isEmpty() ){
            daysExtra = Integer.parseInt(days);
        }

        int millisecondsExtra = daysExtra * DAY_IN_MILLISECONDS;

        Date fixedDate = new Date(originalDate.getTime() + millisecondsExtra);

        return fixedDate;
    }

}