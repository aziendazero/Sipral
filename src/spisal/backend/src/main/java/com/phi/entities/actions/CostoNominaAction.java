package com.phi.entities.actions;

import java.text.DecimalFormat;

import com.phi.entities.baseEntity.CostoNomina;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("CostoNominaAction")
@Scope(ScopeType.CONVERSATION)
public class CostoNominaAction extends BaseAction<CostoNomina, Long> {

	private static final long serialVersionUID = 1276098707L;
    private static final Logger log = Logger.getLogger(CostoNominaAction.class);


	public static CostoNominaAction instance() {
		return (CostoNominaAction) Component.getInstance(CostoNominaAction.class, ScopeType.CONVERSATION);
	}
	 
	public Double getTotalePesoMedio(CostoNomina cn){
		 try{
			 return cn.getDirettoMedMod() + cn.getIndirettoRdpMedMod() + cn.getIndirettoRfpMedMod();
		 
		 } catch (Exception ex) {
			 log.error(ex);
			 throw new RuntimeException(ex);
		 }
	 }
	
	public Double getCaricoAtteso(CostoNomina cn, Integer praticheAttese){
		 try{
			 return this.getTotalePesoMedio(cn)*praticheAttese;
		 
		 } catch (Exception ex) {
			 log.error(ex);
			 throw new RuntimeException(ex);
		 }
	 }


}