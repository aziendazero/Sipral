package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("OreEquivalentiAction")
@Scope(ScopeType.CONVERSATION)
public class OreEquivalentiAction extends CodeValueBaseAction<CodeValue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5447489661257263839L;
	private static final Logger log = Logger.getLogger(CodeValueAtecoAction.class);

	public static OreEquivalentiAction instance() {
		return (OreEquivalentiAction) Component.getInstance(OreEquivalentiAction.class, ScopeType.CONVERSATION);
	}
	
	public void injectChildrenList(CodeValue par){
		try {
			List<CodeValue> children = new ArrayList<CodeValue>();
			
			UserBean ub = (UserBean) Component.getInstance("userBean");
			List<Long> sdLocs = ub.getSdLocs();
			
			for (CodeValue cv : par.getChildren()) {
				String sdl = ((CodeValueParameter)cv).getLocation();
				if (sdl!=null && sdl.contains("-")){
					Long id = Long.parseLong(sdl.split("-")[1]);
					
					if (id !=null && sdLocs.contains(id))
						children.add(cv);
				}				
			}
			
			this.injectList(children, "ChildrenList");
	
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
}
