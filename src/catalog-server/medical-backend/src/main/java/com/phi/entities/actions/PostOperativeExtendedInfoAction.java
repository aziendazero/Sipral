package com.phi.entities.actions;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.PostOperativeExtendedInfo;

@BypassInterceptors
@Name("PostOperativeExtendedInfoAction")
@Scope(ScopeType.CONVERSATION)
public class PostOperativeExtendedInfoAction extends BaseAction<PostOperativeExtendedInfo, Long> {

	private static final long serialVersionUID = 611189235L;

	public static PostOperativeExtendedInfoAction instance() {
		return (PostOperativeExtendedInfoAction) Component.getInstance(PostOperativeExtendedInfoAction.class, ScopeType.CONVERSATION);
	}

	public String concatAll(String mnemonicname) throws DictionaryException, PhiException{

		//		this.getEntity().getAntalgicTherapy();

		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();

		String l1k1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304016L}", String.class);
		String l1k2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304066L}", String.class);
		String l1k3=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304090L}", String.class);
		String l2p1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304109L}", String.class);
		String l2p2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304128L}", String.class);
		String l2p3=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304155L}", String.class);
		String l3d1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304177L}", String.class);
		String l3d21=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304198L}", String.class);
		String l3d22=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304198L2}", String.class);
		String l3d3=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304218L}", String.class);


		String m1k1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304016M}", String.class);
		String m1k2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304066M}", String.class);
		String m1k3=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304090M}", String.class);
		String m2t1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304109M}", String.class);
		String m2t2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304128M}", String.class);
		String m2t3=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304155M}", String.class);
		String m3m1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304177M}", String.class);
		String m3m2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304198M}", String.class);
		String m3m3=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304218M}", String.class);


		String s1m1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304016S}", String.class);
		String s1m2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304066S}", String.class);

		String s2mk1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304109S}", String.class);
		String s2mk2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304128S}", String.class);

		String s3p1=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304177S}", String.class);
		String s3p2=(String)app.evaluateExpressionGet(fc, "${static.Label_13953304198S}", String.class);

		//		this.getEntity().getAntalgicTherapy();

		String text = "";
		if(this.getEntity() != null && this.getEntity().getAntalgicTherapy() != null)
			text = this.getEntity().getAntalgicTherapy() + " ";
		
//		feature introduced with mantis : 19878
		if (mnemonicname.equalsIgnoreCase("L1K") || mnemonicname.equalsIgnoreCase("L2P") || mnemonicname.equalsIgnoreCase("L3D&WS") ||
				mnemonicname.equalsIgnoreCase("M1KT") || mnemonicname.equalsIgnoreCase("M2T") || mnemonicname.equalsIgnoreCase("M3M") ||
				mnemonicname.equalsIgnoreCase("S1M") || mnemonicname.equalsIgnoreCase("S2MK") || mnemonicname.equalsIgnoreCase("S3PERI")){
			
			if(entity.getProtocolType() == null) {
				text += "Protocollo " + mnemonicname ;
				setCodeValue("protocolType", "PHIDIC", "ProtocolType", mnemonicname.replace("&", ""));
				return text;
			}
			
			if(!text.equalsIgnoreCase("") && !entity.getProtocolType().getCode().equals(mnemonicname)){
				text = text.replace("Protocollo " + entity.getProtocolType().getCode() , "Protocollo " + mnemonicname);
				setCodeValue("protocolType", "PHIDIC", "ProtocolType", mnemonicname.replace("&", ""));
				return text;
			}
			
			return text += "Protocollo " + mnemonicname ;
		}
		
		
		else if (mnemonicname.equalsIgnoreCase("l1k1")){
			text += l1k1 ;		
		}else if  (mnemonicname.equalsIgnoreCase("l1k2")){
			text += l1k2 ;					
		}else if  (mnemonicname.equalsIgnoreCase("l1k3")){
			text += l1k3 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l2p1")){
			text += l2p1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l2p2")){
			text += l2p2 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l2p3")){
			text += l2p3 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l3d1")){
			text += l3d1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l3d21")){
			text += l3d21 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l3d22")){
			text += l3d22 ;			
		}else if  (mnemonicname.equalsIgnoreCase("l3d3")){
			text += l3d3 ;			
		}

		else if  (mnemonicname.equalsIgnoreCase("m1k1")){
			text += m1k1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m1k2")){
			text += m1k2 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m1k3")){
			text += m1k3 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m2t1")){
			text += m2t1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m2t2")){
			text += m2t2 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m2t3")){
			text += m2t3 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m3m1")){
			text += m3m1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m3m2")){
			text += m3m2 ;			
		}else if  (mnemonicname.equalsIgnoreCase("m3m3")){
			text += m3m3 ;			
		}


		else if  (mnemonicname.equalsIgnoreCase("s1m1")){
			text += s1m1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("s1m2")){
			text += s1m2 ;			
		}else if  (mnemonicname.equalsIgnoreCase("s2mk1")){
			text += s2mk1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("s2mk2")){
			text += s2mk2 ;			
		}else if  (mnemonicname.equalsIgnoreCase("s3p1")){
			text += s3p1 ;			
		}else if  (mnemonicname.equalsIgnoreCase("s3p2")){
			text += s3p2 ;			
		}
		return text;
	}

	/**
	 * Useful in form f_post_Operative once the protocolType have been chosen
	 */
	public void concatProtocolTextArea(){
		entity.setAntalgicTherapy(entity.getAntalgicTherapy() != null ? entity.getAntalgicTherapy() + " " : "" + "Protocollo " +entity.getProtocolType().getLangIt());
	}


}