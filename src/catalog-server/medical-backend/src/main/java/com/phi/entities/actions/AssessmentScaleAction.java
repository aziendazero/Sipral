package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.AssessmentScale;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("AssessmentScaleAction")
@Scope(ScopeType.CONVERSATION)
public class AssessmentScaleAction extends FilterForPrivacyAction<AssessmentScale, Long> {

	private static final long serialVersionUID = -540695879128L;

	public static AssessmentScaleAction instance() {
		return (AssessmentScaleAction) Component.getInstance(AssessmentScaleAction.class, ScopeType.CONVERSATION);
	}
	

	public List<SelectItem> trueFalseSelectItem () throws PersistenceException, DictionaryException {
		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue cvTrue =  voc.getCodeValueCsDomainCode("PHIDIC", "YesNo", "Yes");
		CodeValue cvFalse = voc.getCodeValueCsDomainCode("PHIDIC", "YesNo", "No");
		
		selectItems.add(new SelectItem(true, cvTrue.getCurrentTranslation()));
		selectItems.add(new SelectItem(false , cvFalse.getCurrentTranslation()));
		return selectItems;
		
	}
	
}