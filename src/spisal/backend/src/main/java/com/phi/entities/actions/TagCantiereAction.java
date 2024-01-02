package com.phi.entities.actions;

import com.phi.entities.baseEntity.TagCantiere;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("TagCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class TagCantiereAction extends BaseAction<TagCantiere, Long> {

	private static final long serialVersionUID = 594212955L;
    private static final Logger log = Logger.getLogger(DitteCantiereAction.class);

	public static TagCantiereAction instance() {
		return (TagCantiereAction) Component.getInstance(TagCantiereAction.class, ScopeType.CONVERSATION);
	}
	
	public TagCantiere copy(TagCantiere toCopy){
		try{
			TagCantiere tagCantiere = new TagCantiere();
			
			tagCantiere.setCantiere(toCopy.getCantiere());
			tagCantiere.setTipologiaCantiere(toCopy.getTipologiaCantiere());
			
			tagCantiere.setCreationDate(toCopy.getCreationDate());
			tagCantiere.setCreatedBy(toCopy.getCreatedBy());
			
			tagCantiere.setStartValidity(toCopy.getStartValidity());
			tagCantiere.setEndValidity(toCopy.getEndValidity());
			tagCantiere.setNotes(toCopy.getNotes());
			
			return tagCantiere;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}


}