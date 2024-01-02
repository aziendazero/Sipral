package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Dosage;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("DosageAction")
@Scope(ScopeType.CONVERSATION)
public class DosageAction extends BaseAction<Dosage, Long> {

private static final long serialVersionUID = 1L;

	public  List<SelectItem> radioList() {
		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		SelectItem selItem1 = new SelectItem("1","Moment of the day");
		SelectItem selItem2 = new SelectItem("2","Hours");
		selectItems.add(selItem1);
		selectItems.add(selItem2);
		return selectItems;
	}

	
	public List<SelectItem> getItemType(CodeValue frequency) throws Exception {
		
		String[] singleFreqCodeArray =  {"DAY","DAYMOMENT","DAYTIME","SPECIFDATE"};
		
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		if (frequency == null) {
			return selectItems;
		}
		
		Vocabularies voc = VocabulariesImpl.instance();
		
		if (frequency.getCode().equals("SINGLE")) {
			for (String code : singleFreqCodeArray) {
				CodeValue cv = voc.getCodeValueCsDomainCode("PHIDIC", "DOSAGETYPE", code);
				if (cv  != null) {
					SelectItem selItem = new SelectItem(cv.getId() , cv.getDisplayName());
					selectItems.add(selItem);
				}
			}
		}
		else {
			selectItems=voc.getIdValues("PHIDIC:DosageType");
		}
		return selectItems;
	
	}
	
}