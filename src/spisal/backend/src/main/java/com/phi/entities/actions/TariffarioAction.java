package com.phi.entities.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Conversation;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Tariffario;

@BypassInterceptors
@Name("TariffarioAction")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings("unused")
public class TariffarioAction extends BaseAction<Tariffario, Long> {

	private static final long serialVersionUID = -2985821922234161984L;
	
	private static final Logger log = Logger.getLogger(TariffarioAction.class);

	
	/* Trova il tariffario da utilizzare */
	private String queryTariffario = "SELECT t FROM Tariffario t " +
			"WHERE t.isActive=true and t.dataRif <= :dataVerifica and (t.dataRifEnd is null or t.dataRifEnd >= :dataVerifica) " +
			"ORDER BY t.dataRif desc";
	
	public static TariffarioAction instance() {
		return (TariffarioAction) Component.getInstance(TariffarioAction.class, ScopeType.CONVERSATION);
	}

	private Workbook getWorkbook(InputStream inputStream, String fileName)  throws IOException {
		Workbook workbook = null;
		
		if (fileName==null || "".equals(fileName))
			return null;
		
		if (fileName.endsWith("xlsx"))
			workbook = new XSSFWorkbook(inputStream);
		
		else if (fileName.endsWith("xls")) 
			workbook = new HSSFWorkbook(inputStream);

		return workbook;
	}
	
	/* Restituisce un dato foglio di lavoro del relativo tariffario */
	public Sheet getSheet(Date dataVerifica, int foglio)  throws Exception {
		if (dataVerifica==null){
			log.info("[cid="+Conversation.instance().getId()+"] Impossibile trovare un tariffario se data verifica null");
			return null;
		}
		
		Sheet sheet = null;
		
		try {
			Workbook tariffarioWb = this.getTariffario(dataVerifica);
		
			if (tariffarioWb==null)
				return null;
			
			sheet = tariffarioWb.getSheetAt(foglio);
			
		} catch (Exception e) {
		    e.printStackTrace();		    	
		    return null;
		}
		
		return sheet;	
	}
	
	/* Nota la data della verifica, restituisce il relativo tariffario */
    private Workbook getTariffario(Date dataVerifica) throws PhiException {
		try {
			Workbook tariffarioWb = null;
			
			HashMap<String, Object> parameters= new HashMap<String, Object>(2);
			parameters.put("dataVerifica", dataVerifica);
						
			@SuppressWarnings("unchecked")
			List<Tariffario> tariffarioList = ca.executeHQLwithParameters(queryTariffario, parameters);
			
			if (tariffarioList!=null && tariffarioList.size()==1){
				Tariffario tariffario = tariffarioList.get(0);

				if (tariffario!=null)
					tariffarioWb = getWorkbook(new ByteArrayInputStream(tariffario.getContent()), tariffario.getFilename());
			} else 
				log.info("[cid="+Conversation.instance().getId()+"] Tariffario non trovato per data verifica: " + dataVerifica);

			return tariffarioWb;
		
		} catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}

}