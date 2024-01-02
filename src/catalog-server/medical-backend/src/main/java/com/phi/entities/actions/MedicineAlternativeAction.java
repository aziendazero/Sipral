package com.phi.entities.actions;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Medicine;

   @BypassInterceptors
   @Name("MedicineAlternativeAction")
   @Scope(ScopeType.CONVERSATION)
   public class MedicineAlternativeAction extends BaseAction<Medicine, Long>{

	private static final long serialVersionUID = -6204401828113262507L;

	public MedicineAlternativeAction() {
         super();
         conversationName = "MedicineAlternative";
      }   
}