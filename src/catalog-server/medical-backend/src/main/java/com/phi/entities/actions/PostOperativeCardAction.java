package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.PostOperativeCard;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("PostOperativeCardAction")
@Scope(ScopeType.CONVERSATION)
public class PostOperativeCardAction extends BaseAction<PostOperativeCard, Long> {

	private static final long serialVersionUID = 583461472L;

	public static PostOperativeCardAction instance() {
		return (PostOperativeCardAction) Component.getInstance(PostOperativeCardAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Calculate the Aldrete SCORE depending on the parameters
	 * @param consciusness
	 * @param respiratory
	 * @param saO2
	 * @param pas
	 * @param motorActivity
	 */
	public int getAldreteScore(CodeValuePhi consciusness, CodeValuePhi respiratory, CodeValuePhi saO2, CodeValuePhi pas, CodeValuePhi motorActivity){
		int score = 0;
		
		if(consciusness != null && consciusness.getScore() != null) {
			score += consciusness.getScore(); 
		}
		
		if(respiratory != null && respiratory.getScore() != null) {
			score += respiratory.getScore(); 
		}
		
		if(saO2 != null && saO2.getScore() != null) {
			score += saO2.getScore(); 
		}
		
		if(pas != null && pas.getScore() != null) {
			score += pas.getScore(); 
		}
		
		if(motorActivity != null && motorActivity.getScore() != null) {
			score += motorActivity.getScore(); 
		}
		
		return score;
	}

}