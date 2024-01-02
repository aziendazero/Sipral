package com.phi.converters;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.VerticalAlignment;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Esposti;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("spisalExcelExport")
@Scope(ScopeType.EVENT)
public class SpisalExcelExport implements Serializable {

	private static final long serialVersionUID = 1224187717792643064L;
	private final static Logger log = Logger.getLogger(SpisalExcelExport.class);

	private String listName = null;
	private String dataTable = null;
	private String subType = null;
	WritableCellFormat cellFormat;
	List<ColumnWidthBean> columnWidthList;
	
	public SpisalExcelExport() throws WriteException {
		cellFormat = new WritableCellFormat();
		cellFormat.setWrap(true);
		cellFormat.setVerticalAlignment(VerticalAlignment.BOTTOM);

	}
	
	
	public void convertToExcelEsposti(ActionEvent event) throws PhiException {
		Context conversationContext = Contexts.getConversationContext();

		List<Esposti> result = null;
		UIComponent component = event.getComponent();

		// get DataTable name and LIST object name
		getParameters(component.getChildren());

		// get DataTable
		IdataModel rowsDataPDM = null;

		if (rowsDataPDM == null) {
			Object currentList = Component.getInstance(listName);
			rowsDataPDM = IdataModel.class.cast(currentList);
		}

		if (rowsDataPDM != null) {
			OutputStream output = null;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			try {
				// get servlet response and init it
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Expires", "0");
				response.setHeader("Content-Disposition", "attachment;filename=RegistroEsposti.xls");
				output = response.getOutputStream();
				log.info("[" + getDate("dd/MM/yyyy - HH:mm")+ "] Exporting table into excel format [START]");

				// create excel workbook with a work sheet
				WritableWorkbook wb = Workbook.createWorkbook(output);
				WritableSheet sheet = wb.createSheet("First Sheet", 0);

				result = (List<Esposti>) ((IdataModel<Esposti>) conversationContext.get("EspostiList")).getFullList();

				// set Excel table Headers label
				String[] headersLabel = {
						"Cognome",
						"Nome",
						"Data di nascita",
						"Ditta",
						"Data inizio",
						"Data fine",
						"N.CAS"};

				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				UserBean ub = UserBean.instance();
				int rowIndex = 0;
				
				//INTESTAZIONE
				List<Object> values = new ArrayList<Object>();
				addValueString(values, "ELENCO REGISTRO ESPOSTI");
				writeCellsFromArray(values.toArray(), sheet, rowIndex, 0);
				rowIndex++;
				values.clear();
				
				addValueString(values, "Dati estratti da:");
				addValueString(values, ub.getNameGiv()+ " " + ub.getNameFam());//nome e cognome dell'operatore di login
				writeCellsFromArray(values.toArray(), sheet, rowIndex, 0);
				rowIndex++;
				values.clear();
				
				addValueString(values, "Data di estrazione:");
				addValueString(values, dateFormat.format(new Date()));	//sysdate
				writeCellsFromArray(values.toArray(), sheet, rowIndex, 0);
				rowIndex++;
				values.clear();
				
				columnWidthList = new ArrayList<ColumnWidthBean>(headersLabel.length);
				getHeaders(headersLabel, sheet, rowIndex);
				rowIndex++;

				for (Esposti esp : result) {
					String birtTime = null;
					String startTime = null;
					String endTime = null;
					if (esp.getStartDate()!=null)
						startTime = dateFormat.format(esp.getStartDate());

					if (esp.getEndDate()!=null)
						endTime = dateFormat.format(esp.getEndDate());
					
					if (esp.getPerson()!=null && esp.getPerson().getBirthTime()!=null)
						birtTime = dateFormat.format(esp.getPerson().getBirthTime());

					
					if(esp.getPerson()!=null && esp.getPerson().getName()!=null){
						addValueString(values, esp.getPerson().getName().getFam());	//COGNOME
						addValueString(values, esp.getPerson().getName().getGiv());	//NOME
					}else{
						addValueString(values, "");
						addValueString(values, "");
					}
					
					addValueString(values, birtTime);	//DATA DI NASCITA
					
					//Esposti.schedaEsposti.personeGiuridiche.denominazione
					if(esp.getSchedaEsposti()!=null && esp.getSchedaEsposti().getPersoneGiuridiche()!=null && esp.getSchedaEsposti().getPersoneGiuridiche()!=null){
						addValueString(values, esp.getSchedaEsposti().getPersoneGiuridiche().getDenominazione());	//DITTA
					}else{
						addValueString(values, "");
					}
					
					addValueString(values, startTime);	//DATA INIZIO
					addValueString(values, endTime);	//DATA FINE
					
					//Esposti.schedaEsposti.personeGiuridiche.denominazione
					if(esp.getCas()!=null){
						addValueString(values, esp.getCas());	//N.CAS
					}else{
						addValueString(values, "");
					}
					
					writeCellsFromArray(values.toArray(), sheet, rowIndex, 0);
					rowIndex++;
					values.clear();
				}
				resizeColumn(sheet);
				wb.write();
				wb.close();
				log.info("[" + getDate("dd/MM/yyyy - HH:mm") + "] Exporting table into excel format [FINISH]");
				output.flush();
				output.close();
			} catch (IOException e) {
				log.error("[" + getDate("dd/MM/yyyy - HH:mm") + "] I/O error", e);
			} catch (RowsExceededException e) {
				log.error("[" + getDate("dd/MM/yyyy - HH:mm") + "] Too many rows", e);
			} catch (WriteException e) {
				log.error("[" + getDate("dd/MM/yyyy - HH:mm") + "] Error writing data on sheet", e);
			}

			facesContext.responseComplete();
		}
	}
	
	public void convertToExcelBio(ActionEvent event) throws PhiException {
		Context conversationContext = Contexts.getConversationContext();

		List<Esposti> result = null;
		UIComponent component = event.getComponent();

		// get DataTable name and LIST object name
		getParameters(component.getChildren());

		// get DataTable
		IdataModel rowsDataPDM = null;

		if (rowsDataPDM == null) {
			Object currentList = Component.getInstance(listName);
			rowsDataPDM = IdataModel.class.cast(currentList);
		}

		if (rowsDataPDM != null) {
			OutputStream output = null;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			try {
				// get servlet response and init it
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Expires", "0");
				response.setHeader("Content-Disposition", "attachment;filename=RegistroEsposti.xls");
				output = response.getOutputStream();
				log.info("[" + getDate("dd/MM/yyyy - HH:mm")+ "] Exporting table into excel format [START]");

				// create excel workbook with a work sheet
				WritableWorkbook wb = Workbook.createWorkbook(output);
				WritableSheet sheet = wb.createSheet("First Sheet", 0);

				result = (List<Esposti>) ((IdataModel<Esposti>) conversationContext.get("EspostiList")).getFullList();

				// set Excel table Headers label
				String[] headersLabel = {
						"Cognome",
						"Nome",
						"Data di nascita",
						"Ditta",
						"Data inizio",
						"Data fine",
						"Gruppo 3/4"};

				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				UserBean ub = UserBean.instance();
				int rowIndex = 0;
				
				//INTESTAZIONE
				List<Object> values = new ArrayList<Object>();
				addValueString(values, "ELENCO REGISTRO ESPOSTI");
				writeCellsFromArray(values.toArray(), sheet, rowIndex + 1, 0);
				rowIndex++;
				values.clear();
				
				addValueString(values, "Dati estratti da:");
				addValueString(values, ub.getNameGiv()+ " " + ub.getNameFam());//nome e cognome dell'operatore di login
				writeCellsFromArray(values.toArray(), sheet, rowIndex + 1, 0);
				rowIndex++;
				values.clear();
				
				addValueString(values, "Data di estrazione:");
				addValueString(values, dateFormat.format(new Date()));	//sysdate
				writeCellsFromArray(values.toArray(), sheet, rowIndex + 1, 0);
				rowIndex++;
				values.clear();
				
				columnWidthList = new ArrayList<ColumnWidthBean>(headersLabel.length);
				getHeaders(headersLabel, sheet, rowIndex);

				for (Esposti esp : result) {
					String birtTime = null;
					String startTime = null;
					String endTime = null;
					if (esp.getStartDate()!=null)
						startTime = dateFormat.format(esp.getStartDate());

					if (esp.getEndDate()!=null)
						endTime = dateFormat.format(esp.getEndDate());
					
					if (esp.getPerson()!=null && esp.getPerson().getBirthTime()!=null)
						birtTime = dateFormat.format(esp.getPerson().getBirthTime());

					
					if(esp.getPerson()!=null && esp.getPerson().getName()!=null){
						addValueString(values, esp.getPerson().getName().getFam());	//COGNOME
						addValueString(values, esp.getPerson().getName().getGiv());	//NOME
					}else{
						addValueString(values, "");
						addValueString(values, "");
					}
					
					addValueString(values, birtTime);	//DATA DI NASCITA
					
					//Esposti.schedaEsposti.personeGiuridiche.denominazione
					if(esp.getSchedaEsposti()!=null && esp.getSchedaEsposti().getPersoneGiuridiche()!=null && esp.getSchedaEsposti().getPersoneGiuridiche()!=null){
						addValueString(values, esp.getSchedaEsposti().getPersoneGiuridiche().getDenominazione());	//DITTA
					}else{
						addValueString(values, "");
					}
					
					addValueString(values, startTime);	//DATA INIZIO
					addValueString(values, endTime);	//DATA FINE
					
					//Esposti.schedaEsposti.personeGiuridiche.denominazione
					if(esp.getBio()!=null){
						addValueString(values, esp.getBio());	//GRUPPO 3/4
					}else{
						addValueString(values, "");
					}
					
					writeCellsFromArray(values.toArray(), sheet, rowIndex + 1, 0);
					rowIndex++;
					values.clear();
				}
				resizeColumn(sheet);
				wb.write();
				wb.close();
				log.info("[" + getDate("dd/MM/yyyy - HH:mm") + "] Exporting table into excel format [FINISH]");
				output.flush();
				output.close();
			} catch (IOException e) {
				log.error("[" + getDate("dd/MM/yyyy - HH:mm") + "] I/O error", e);
			} catch (RowsExceededException e) {
				log.error("[" + getDate("dd/MM/yyyy - HH:mm") + "] Too many rows", e);
			} catch (WriteException e) {
				log.error("[" + getDate("dd/MM/yyyy - HH:mm") + "] Error writing data on sheet", e);
			}

			facesContext.responseComplete();
		}
	}
	
	/**
	 * resize columns width
	 * 
	 * @param sheet
	 *            the sheet where to resize columns
	 */
	private void resizeColumn(WritableSheet sheet) {
		if (columnWidthList != null) {
			for (int i = 0; i < this.columnWidthList.size(); i++) {
				if (columnWidthList.get(i).columnWidth > 0)
					sheet.setColumnView(i, columnWidthList.get(i).columnWidth);
			}
		}
	}
	
	/**
	 * Gets column headers from given string array and writes values as sheet
	 * headers
	 * 
	 * @param labels
	 *            - the array of label
	 * @param sheet
	 *            - current excel sheet
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void getHeaders(String[] labels, WritableSheet sheet) throws RowsExceededException, WriteException {
		getHeaders(labels, sheet, 0);
	}
	
	private void getHeaders(String[] labels, WritableSheet sheet, int rowIndex) throws RowsExceededException, WriteException {
		// loop through data table children
		if (labels != null && labels.length != 0) {
			for (int i = 0; i < labels.length; i++) {
				sheet.addCell(new Label(i, rowIndex, labels[i] != null ? labels[i] : ""));
				// set base width
				if (columnWidthList != null) {
					ColumnWidthBean cWidth = new ColumnWidthBean();
					cWidth.columnWidth = labels[i].length();
					columnWidthList.add(i, cWidth);
				}
			}
		}
	}
	
	/**
	 * Gets current row values and write them into given sheet
	 * 
	 * @param rowCells
	 *            - object array containing current row values
	 * @param sheet
	 *            - current excel sheet
	 * @param rowIndex
	 *            - current excel sheet row index
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	private void writeCellsFromArray(Object[] rowCells, WritableSheet sheet, int rowIndex, int offset) throws RowsExceededException, WriteException {
		if (rowCells != null) {
			// loop on all row objects but id column
			for (int colIndex = 0; (colIndex + offset) < rowCells.length; colIndex++) {
				Object cell = rowCells[colIndex];
				if (cell instanceof CodeValue) {
					// for code values get translation if exists. If not use
					// display name. Get CV code as extra data
					CodeValue cv = (CodeValue) cell;
					String label = cv.getDisplayName();
					if (cv.getCurrentTranslation() != null && !cv.getCurrentTranslation().isEmpty())
						label = cv.getCurrentTranslation();
					if (columnWidthList.get(colIndex).toWrap)
						sheet.addCell(new Label(colIndex, rowIndex, label + " [" + cv.getCode() + "]", cellFormat));
					else
						sheet.addCell(new Label(colIndex, rowIndex, label + " [" + cv.getCode() + "]"));

				} else if (cell instanceof Date) {
					// for TimeStamps get date...
					Date date = (Date) cell;
					Date fixedDate = null;
					if (date != null) {
						Calendar cal = new GregorianCalendar();
						cal.setTime(date);
						// ... and fix it considering current timezone
						fixedDate = new Date(date.getTime() + (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)));
					}
					if (columnWidthList != null && columnWidthList.get(colIndex).toWrap)
						sheet.addCell(fixedDate != null ? new DateTime(colIndex, rowIndex, fixedDate) : new Label(colIndex, rowIndex, "", cellFormat));
					else
						sheet.addCell(fixedDate != null ? new DateTime(colIndex, rowIndex, fixedDate) : new Label(colIndex, rowIndex, ""));
				} else {
					// in other cases get string value or use empty string for
					// null objects
					if (columnWidthList != null && columnWidthList.get(colIndex).toWrap)
						sheet.addCell(new Label(colIndex, rowIndex,cell != null ? cell.toString() : "", cellFormat));
					else
						sheet.addCell(new Label(colIndex, rowIndex,cell != null ? cell.toString() : ""));

					// compute column width (check if we must recalculate it)
					if (columnWidthList != null && columnWidthList.get(colIndex).widthToRecalculate) {
						int cellLenght = cell.toString().length();
						if (cellLenght > columnWidthList.get(colIndex).columnWidth) {
							columnWidthList.get(colIndex).columnWidth = cellLenght;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Gets dataTable and listName parameters from event invoker component
	 * 
	 * @param children
	 *            - event invoker component children list
	 */
	private void getParameters(List<UIComponent> children) {
		for (UIComponent child : children) {
			if (child instanceof UIParameter) {
				UIParameter parameter = (UIParameter) child;
				if ("dataTable".equals(parameter.getName())) {
					dataTable = (String) parameter.getValue();
				} else if ("tableHeaders".equals(parameter.getName())) {
					String h = (String) parameter.getValue();
					String[] headers = null;
					if (h != null && !h.isEmpty())
						headers = h.split("-/-");
				} else if ("listName".equals(parameter.getName())) {
					listName = (String) parameter.getValue();
				} else if ("subType".equals(parameter.getName())) {
					subType = (String) parameter.getValue();
				}
			}
		}

	}
	
	/**
	 * Given a date format string, this returns a formatted date string of
	 * current time
	 * 
	 * @param format
	 *            - a date format string
	 * @return a formatted date string of current time
	 */
	private String getDate(String format) {
		String result = null;

		Date current = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		result = sdf.format(current);

		return result;
	}

	private void addValueString(List<Object> values, Object input) {
		if (values==null){
			return;
		}else{
			if(input==null)
				values.add("");
			else
				values.add(input.toString());
		}
	}
	
	/**
	 * Given a map and a binding, extract the corresponding value
	 * @param map
	 * @param binding
	 * @return
	 */
	private Object resolveMapProperty(Map<Object, Object> map, String binding) {

		if (map != null && binding != null && !binding.isEmpty()) {
			String[] tokens = binding.split("\\.");
			if (tokens.length == 1)
				return map.get(tokens[0]);

			if (tokens.length > 1) {
				Object obj = map.get(tokens[0]);
				if (obj instanceof Map) {
					return resolveMapProperty((Map) obj, binding.substring(binding.indexOf(".") + 1));
				} else {
					// should never happen
					return obj;
				}
			}
		}

		return null;
	}

	/**
	 * inner class to save column width and to check if column width must be
	 * recalculated when we have all column filled. By default recalculate width
	 * each time thre is a new cell and do not wrap content.
	 * 
	 * @author rossi
	 * 
	 */
	class ColumnWidthBean {
		protected int columnWidth;
		protected boolean widthToRecalculate = true;
		protected boolean toWrap = false;

	}
}
