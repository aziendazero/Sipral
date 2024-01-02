package com.phi.entities.actions;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.VitalSign;
import com.phi.entities.baseEntity.IntraoperatoryCard;

@BypassInterceptors
@Name("IntraoperatoryCardAction")
@Scope(ScopeType.CONVERSATION)
public class IntraoperatoryCardAction extends BaseAction<IntraoperatoryCard, Long> {

	private static final long serialVersionUID = 1665699675L;

	public static IntraoperatoryCardAction instance() {
		return (IntraoperatoryCardAction) Component.getInstance(IntraoperatoryCardAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Calculate water balance for intraoperatory card from graph
	 * Called by: MOD_Operating_Theatre\customer_VCO\PROCESSES\IntraOperating.jpdl.xml process
	 * @return total ins - total outs
	 */
	public Integer calculateWaterBalance() {
		
		Double in = 0d;
		Double out = 0d;
		
		IntraoperatoryCard intraOpCrd = getEntity();
		
		//Reload entity:
		ca.refresh(intraOpCrd);
		
		//Reset values:
		intraOpCrd.setDiuresis(0);
		intraOpCrd.setHematicLoss(0);
		intraOpCrd.setPerspiratio(0);
//		intraOpCrd.setInfusion(0);
		
		
		List<VitalSign> vitalSigns = intraOpCrd.getVitalSign();
		
		for (VitalSign vs : vitalSigns) {
			//OUT
			if (vs.getHematicLoss() != null && vs.getHematicLoss().getValue() != null) {
				out += vs.getHematicLoss().getValue();
				intraOpCrd.setHematicLoss( intraOpCrd.getHematicLoss() + vs.getHematicLoss().getValue().intValue() );
			}
			if (vs.getPerspiratio() != null && vs.getPerspiratio().getValue() != null) {
				out += vs.getPerspiratio().getValue();
				intraOpCrd.setPerspiratio( intraOpCrd.getPerspiratio() + vs.getPerspiratio().getValue().intValue() );
			}
			if (vs.getDiuresis() != null && vs.getDiuresis().getValue() != null) {
				out += vs.getDiuresis().getValue();
				intraOpCrd.setDiuresis( intraOpCrd.getDiuresis() + vs.getDiuresis().getValue().intValue() );
				
			}
			
			//IN
//			if (vs.getAdministration1() != null && vs.getAdministration1().getValue() != null) {
//				in += vs.getAdministration1().getValue();
//				intraOpCrd.setInfusion( intraOpCrd.getInfusion() + vs.getAdministration1().getValue().intValue() );
//			}
//			if (vs.getAdministration2() != null && vs.getAdministration2().getValue() != null) {
//				in += vs.getAdministration2().getValue();
//				intraOpCrd.setInfusion( intraOpCrd.getInfusion() + vs.getAdministration2().getValue().intValue() );
//			}
//			if (vs.getAdministration3() != null && vs.getAdministration3().getValue() != null) {
//				in += vs.getAdministration3().getValue();
//				intraOpCrd.setInfusion( intraOpCrd.getInfusion() + vs.getAdministration3().getValue().intValue() );
//			}
			
		}
		
		intraOpCrd.setWaterBalance(in.intValue() - out.intValue());
		
		return intraOpCrd.getWaterBalance();
		
	}
	
	/**
	 * Calculate water balance for intraoperatory card from form
	 * IN: Infusioni, Emazie, Plasma, Altro
     * OUT: Diuresi, Perdite emat, SNG, Perspiratio
	 * Called by: MOD_Operating_Theatre\customer_VCO\PROCESSES\IntraOperating.jpdl.xml process
	 * @return
	 */
	public Integer calculateWaterBalanceTotal() {
		int tot = 0;
		
		IntraoperatoryCard intraOpCrd = getEntity();
		
		//IN
		if (intraOpCrd.getInfusion() != null) { 
			tot += intraOpCrd.getInfusion();
		}
		if (intraOpCrd.getEmazie() != null) {
			tot += intraOpCrd.getEmazie();
		}
		if (intraOpCrd.getPlasma() != null) {
			tot += intraOpCrd.getPlasma();
		}
		if (intraOpCrd.getOther() != null) {
			tot += intraOpCrd.getOther();
		}
		
		//OUT
		if (intraOpCrd.getHematicLoss() != null) {
			tot -= intraOpCrd.getHematicLoss();
		}
		if (intraOpCrd.getPerspiratio() != null) {
			tot -= intraOpCrd.getPerspiratio();
		}
		if (intraOpCrd.getDiuresis() != null) {
			tot -= intraOpCrd.getDiuresis();
		}
		if (intraOpCrd.getSng() != null) {
			tot -= intraOpCrd.getSng();
		}
		
		intraOpCrd.setWaterBalance(tot);
		
		return intraOpCrd.getWaterBalance();
	}
}