package com.phi.entities.actions;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.act.VitalSign;
import com.phi.entities.dataTypes.PQ;
import com.phi.events.PhiEvent;

@BypassInterceptors
@Name("VitalSignAction")
@Scope(ScopeType.CONVERSATION)
public class VitalSignAction extends FilterForPrivacyAction<VitalSign, Long> {

	private static final long serialVersionUID = -5092382867539591845L;
	private static final Logger log = Logger.getLogger(VitalSignAction.class);
	
	
    public static VitalSignAction instance() {
        return (VitalSignAction) Component.getInstance(VitalSignAction.class, ScopeType.CONVERSATION);
    }
	
    /**
     * Returns a formatted string for datagrid (used in MOD_Medical_Obs\CORE\FORMS\f_list_measures.mmgp)
     * 
     * @param vs
     * @return
     */
	@ShowInDesigner(description = "getTypes")
	public String getTypes(VitalSign vs) {
	
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();
		String PressioneArteriosa=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304016pv}", String.class);
		String Temperatura=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304066pv}", String.class);
		String Glicemia=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304090pv}", String.class);
		String Dolore=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304109pv}", String.class);
		String Peso=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304128pv}", String.class);
		String Altezza=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304155pv}", String.class);
		String BMI=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304177pv}", String.class);
		String FrequenzaCardiaca=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304198pv}", String.class);
		String SpO2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304218pv}", String.class);
		String FrequenzaRespiratoria=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304237pv}", String.class);
		String PVC=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304257pv}", String.class);
		String DiamOcchioSX=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304270pv}", String.class);
		String ReactOcchioSX=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304283pv}", String.class);
		String DiamOcchioDX=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304296pv}", String.class);
		String ReactOcchioDX=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304313pv}", String.class);
		String ret = "";
		if (vs == null)
			return ret;
		
		if (vs.getSystolic() != null && vs.getSystolic().getValue() != null)
			ret += PressioneArteriosa + ":" +vs.getSystolic()+"/"+vs.getDiastolic()+"; ";
		if (vs.getBodyTemperature() != null
				&& vs.getBodyTemperature().getValue() != null)
			ret += Temperatura + ":" + vs.getBodyTemperature()+"; ";
		if (vs.getGlycemia() != null && vs.getGlycemia().getValue() != null)
			ret += Glicemia + ":" + vs.getGlycemia()+"; ";
		if (vs.getPain() != null && vs.getPain().getValue() != null)
			ret += Dolore + ":" + vs.getPain()+"; ";
		if (vs.getWeight() != null && vs.getWeight().getValue() != null)
			ret += Peso + ":" + vs.getWeight()+"; ";
		if (vs.getHeight() != null && vs.getHeight().getValue() != null)
			ret += Altezza + ":" + vs.getHeight() +"; ";
		if ((vs.getWeight() != null && vs.getWeight().getValue() != null)&&(vs.getHeight() != null && vs.getHeight().getValue() != null))
			ret += BMI + ":" + this.getBMI(vs)+"; ";	
		if (vs.getCardiacFrequency() != null
				&& vs.getCardiacFrequency().getValue() != null)
			ret += FrequenzaCardiaca + ":" + vs.getCardiacFrequency()+"; ";
		if (vs.getO2Saturation() != null
				&& vs.getO2Saturation().getValue() != null)
			ret += SpO2 + ":" + vs.getO2Saturation()+"; ";
		if (vs.getBreathFrequency()!=null && vs.getBreathFrequency().getValue()!=null)
			ret += FrequenzaRespiratoria + ":" + vs.getBreathFrequency().getValue()+"; ";
		if (vs.getCvp()!=null && vs.getCvp().getValue()!=null)
			ret+=PVC + ":" + vs.getCvp()+"; ";
		if (vs.getDiameterEyeL()!=null && vs.getDiameterEyeL().getValue()!=null)
			ret+=DiamOcchioSX + ":" + vs.getDiameterEyeL()+"; ";
		if (vs.getReactionEyeL()!=null)
			ret+=ReactOcchioSX + ":" + vs.getReactionEyeL()+"; ";
		if (vs.getDiameterEyeR()!=null && vs.getDiameterEyeR().getValue()!=null)	
			ret+=DiamOcchioDX + ":" + vs.getDiameterEyeR()+"; ";
		if (vs.getReactionEyeR()!=null)
			ret+=ReactOcchioDX + ":" + vs.getReactionEyeR()+"; ";
		
		ret = ret.replaceAll("(\\d)\\.0+(\\D|$)", "$1$2"); 
		
		if (ret.endsWith("; "))
			ret = ret.substring(0, ret.length() - 2);

		return ret;
	}

	/**
	 * Returns BMI value (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param vs
	 * @return
	 */
	@ShowInDesigner(description = "getBMI")
	public PQ getBMI(VitalSign vs){
		PQ result=new PQ(); 
		double height=0, weight=0;

		if(vs!= null && vs.getHeight()!=null && vs.getHeight().getValue()!=null) {
			height=vs.getHeight().getValue();
			if (vs.getHeight().getUnit() == null) {//assume the height is in cm, must be converted to meters. 
				height/=100;
			}
			/* TODO conversion to meters
			else {

			}
			*/
		}
		
		if(vs!= null && vs.getWeight()!=null && vs.getWeight().getValue()!=null) {
			weight=vs.getWeight().getValue();
			/* TODO conversion to kg
			if (vs.getWeight().getUnit() == null) {  //assume the weight is in kg (no conversion to be applied)
				
			}	
			else {
				//To do weight conversions to kg!
			}
			*/
		}
		
		double bmi=0;

		if(height!=0) {//avoid division by 0
			bmi=weight / (height * height);
			bmi=new BigDecimal(bmi).setScale(2 , BigDecimal.ROUND_UP).doubleValue(); 
		}
		result.setValue(bmi);
		return result;
	}
	
	/**
	 * 
	 * Returns BSA value (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param vs
	 * @return
	 */
	@ShowInDesigner(description = "getBSA")
	public PQ getBSA(VitalSign vs){
		PQ result= new PQ();
		double height=0,weight=0;

		if(vs!= null && vs.getHeight()!=null && vs.getHeight().getValue()!=null) {
			height=vs.getHeight().getValue();
			if (vs.getHeight().getUnit() == null) {//assume the height is in cm, must be converted to meters. 
				height/=100;
			}
			/* TODO conversion to meters
			else {

			}
			*/
		}
		
		if(vs!= null && vs.getWeight()!=null && vs.getWeight().getValue()!=null) {
			weight=vs.getWeight().getValue();
			/* TODO conversion to kg
			if (vs.getWeight().getUnit() == null) {  //assume the weight is in kg (no conversion to be applied)
				
			}	
			else {
				//To do weight conversions to kg!
			}
			*/
		}
		
		double bsa=0;
		bsa=71.84 * Math.pow(weight, 0.425) * Math.pow(height, 0.725); //assume the height is in cm and the weight is in kg
		bsa=new BigDecimal(bsa).setScale(2, BigDecimal.ROUND_UP).doubleValue();
		result.setValue(bsa);
		
		return result;
	}
	
	@ShowInDesigner(description = "getLep1")
	public Boolean getLep1(VitalSign vs) {
		
		if (vs == null){
			return false;
		}
		if (vs.getSystolic() != null && vs.getSystolic().getValue() != null && vs.getDiastolic()!=null && vs.getDiastolic().getValue() != null){
		return true;
		}
		else
			return false;
	}
	
	@ShowInDesigner(description = "getLep2")
	public Boolean getLep2(VitalSign vs) {
		if (vs == null){
			return false;
		}
		if  (vs.getGlycemia() != null && vs.getGlycemia().getValue() != null){
		return true;
		}
		else
			return false;
	}
	
	@ShowInDesigner(description = "getLep3")
	public Boolean getLep3(VitalSign vs) {

		if (vs == null){
			return false;
		}
		if (vs.getWeight() != null && vs.getWeight().getValue() != null && vs.getHeight() != null && vs.getHeight().getValue() != null){
		return true;
		}
		else
			return false;
	}
	
	@ShowInDesigner(description = "getLep4")
	public Boolean getLep4(VitalSign vs) {

		if (vs == null){
			return false;
		}
		if (vs.getO2Saturation() != null&& vs.getO2Saturation().getValue() != null && vs.getBreathFrequency()!=null && vs.getBreathFrequency().getValue()!=null){
		return true;
		}
		else
			return false;
	}
	
	@ShowInDesigner(description = "getLep5")
	public Boolean getLep5(VitalSign vs) {

		if (vs == null){
			return false;
		}
		if (vs.getCvp()!=null && vs.getCvp().getValue()!=null){
		return true;
		}
		else
			return false;
	}
	
	@ShowInDesigner(description = "getLep6")
	public Boolean getLep6(VitalSign vs) {

		if (vs == null){
			return false;
		}
		if (vs.getBodyTemperature() != null && vs.getBodyTemperature().getValue() != null){
		return true;
		}
		else
			return false;
	}
	
	@ShowInDesigner(description = "getLep7")
	public Boolean getLep7(VitalSign vs) {
		if (vs == null){
			return false;
		}
		if (vs.getCardiacFrequency() != null && vs.getCardiacFrequency().getValue() != null){
			return true;
		}
		else
			return false;
	}

	/**
	 * Checks Pressure range value: it's beetween 1 and 300? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param systolic
	 * @param diastolic
	 * @return
	 */
	@ShowInDesigner(description = "getPressureRangeValue")
	public Boolean getPressureRangeValue(PQ systolic, PQ diastolic){					
		if(systolic!=null && diastolic!=null){
			if(systolic.getValue()!=null && diastolic.getValue()!=null){
				Double systolic1=systolic.getValue(),diastolic1=diastolic.getValue();
				if((systolic1>=1 && systolic1 <= 300) && (diastolic1>=1 && diastolic1 <= 300))
					return false;
				else return true;
			}else return false;			
		}else return false;		
	}

	/**
	 * Checks Temperature range value: it's beetween 0 and 50? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param temp
	 * @return
	 */
	@ShowInDesigner(description = "getTemperatureRangeValue")
	public Boolean getTemperatureRangeValue(PQ temp){
		if(temp!=null){
			if (temp.getValue()!=null){
				Double temp1=temp.getValue();
				if(temp1>=0 && temp1 <= 50)
					return false;
				else return true;			
			}else return false;
		}else return false;		

	}

	/**
	 * Checks Glycemia range value: it's beetween 1 and 900? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param gly
	 * @return
	 */
	@ShowInDesigner(description = "getGlycemiaRangeValue")
	public Boolean getGlycemiaRangeValue(PQ gly){
		if(gly!=null){			
			if(gly.getValue()!=null){
				Double gliy1=gly.getValue();
				if(gliy1>=1 && gliy1 <= 900)
					return false;
				else return true;
			}else return false;	
		}return false;	

	}

	/**
	 * Checks Pain range value: it's beetween 1 and 10? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param pain
	 * @return
	 */
	@ShowInDesigner(description = "getPainRangeValue")
	public Boolean getPainRangeValue(PQ pain){
		if(pain!=null){
			if(pain.getValue()!=null){
				Double pain1=pain.getValue();
				if(pain1>=0 && pain1 <= 10)
					return false;
				else return true;
			}else return false;	
		}else return false;	
	}

	/**
	 * Checks wheight and height range value: it's beetween 0 and 300? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param weight
	 * @param height
	 * @return
	 */
	@ShowInDesigner(description = "getWeightHeightRangeValue")
	public Boolean getWeightHeightRangeValue(PQ weight,PQ height){
		if(weight!=null && height!=null){
			if(weight.getValue()!=null && height.getValue()!=null){
				Double weight1=weight.getValue(),height1=height.getValue();
				if((weight1>=0 && weight1<= 300) && (height1>=0 && height1 <= 300))
					return false;
				else return true;
			}else return false;	
		}else return false;				
	}

	/**
	 * Checks Cardiac Frequency range value: it's beetween 1 and 400? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param card
	 * @return
	 */
	@ShowInDesigner(description = "getCardiacFreqRangeValue")
	public Boolean getCardiacFreqRangeValue(PQ card){
		if(card!=null){
			if(card.getValue()!=null){
				Double card1=card.getValue();
				if(card1>=1 && card1 <= 400)
					return false;
				else return true;
			}else return false;	
		}else return false;	
	}

	/**
	 * Checks Saturation range value: it's beetween 1 and 100? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param sat
	 * @return
	 */
	@ShowInDesigner(description = "getSaturationRangeValue")
	public Boolean getSaturationRangeValue(PQ sat){
		if(sat!=null){
			if(sat.getValue()!=null){
				Double sat1=sat.getValue();
				if(sat1>=1 && sat1 <= 100)
					return false;
				else return true;
			}else return false;	
		}else return false;
	}

	/**
	 * Checks Breath Frequency range value: it's beetween 0 and 100? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param bFreq
	 * @return
	 */
	@ShowInDesigner(description = "getBreathFreqRangeValue")
	public Boolean getBreathFreqRangeValue(PQ bFreq){
		if(bFreq!=null){
			if(bFreq.getValue()!=null){
				Double bFreq1=bFreq.getValue();
				if(bFreq1>=0 && bFreq1 <= 100)
					return false;
				else return true;
			}else return false;	
		}else return false;
	}

	/**
	 * Checks cvp range value: it's beetween 10 and 30? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param cvp
	 * @return
	 */
	@ShowInDesigner(description = "getCvpRangeValue")
	public Boolean getCvpRangeValue(PQ cvp){
		if(cvp!=null){
			if(cvp.getValue()!=null){
				Double cvp1=cvp.getValue();
				if(cvp1>=-10 && cvp1 <= 30)
					return false;
				else return true;		
			}else return false;
		}else return false;
	}

	/**
	 * Checks diameter eye range value: it's beetween 0 and 15? Return false otherwise true
	 * (used in MOD_Medical_Obs\CORE\FORMS\f_edit_measure.mmgp)
	 * 
	 * @param dim
	 * @return
	 */
	@ShowInDesigner(description = "getDiameterEyeRangeValue")
	public Boolean getDiameterEyeRangeValue(PQ dim){
		if(dim!=null){
			if(dim.getValue()!=null){
				Double dim1=dim.getValue();
				if(dim1>=0 && dim1 <= 15)
					return false;
				else return true;
			}else return false;	
		}else return false;
	}
	
	/**
	 * Checks evaluation date: if it's <= current date then return false, otherwise return true
	 * 
	 * @param vs
	 * @return
	 */
	@ShowInDesigner(description = "checkInsertDate")
	public Boolean checkInsertDate(VitalSign vs){
		FunctionsBean f=new FunctionsBean();
		Date insDate=vs.getEvaluationDate(), currentDate=f.currentDateTime();
		if (insDate!=null){
			int i=insDate.compareTo(currentDate);
			if(i<=0) return false;
			else return true;
		}	
		return true;
	}
	
	private static PagedDataModel<VitalSign> oldList;
	
	public List<VitalSign> filterVitalSignList(PatientEncounter enc) throws PhiException{
		
		this.getEqual().put("patientEncounter",enc);
		this.getEqual().put("confirmed",true);
		this.getOrderBy().put("evaluationDate","descending");
		this.filterForPrivacy("creationDate");
		List<VitalSign> vsList = this.list();
		Context conversation = Contexts.getConversationContext();	
		conversation.set("ConfirmedVitalSignList", new PhiDataModel<VitalSign>(vsList, "ConfirmedVitalSign"));
		Events.instance().raiseEvent(PhiEvent.QUERY, Thread.currentThread().getStackTrace()[1].getMethodName() + '@' + HibernateProxyHelper.getClassWithoutInitializingProxy(this).getSimpleName());
		return vsList;		
	}

	
	public Boolean isVitalSignList()throws PhiException{
		List<VitalSign> vsList=this.list();
		if(vsList==null || (vsList.size()<1 && vsList.isEmpty())) return false;
		else{
			boolean check=false; 
			try{
				Iterator i=vsList.iterator();
				while(!check && i.hasNext() && i.next()!=null){
					VitalSign vs=(VitalSign)i.next();
					if(vs!=null){
						if(vs.getIsActive())
							check=true;
					}	
				}	
			}catch(NullPointerException n){
				log.error("Error loading measure");
			}
			return check;
		}
	}
	
	public void assignedOldVitalSignList(){
		Context conversation = Contexts.getConversationContext();
		if (oldList!=null)
			conversation.set("VitalSignList", oldList);
	}	
}