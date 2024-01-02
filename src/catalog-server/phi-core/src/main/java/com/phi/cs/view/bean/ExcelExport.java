package com.phi.cs.view.bean;

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
import java.util.ResourceBundle;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIParameter;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Locale;

import com.phi.cs.datamodel.IdataModel;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;

/**
 * Bean for DataTable to MSExcel exporting
 * 
 * @author Francesco Bruni
 *
 */
@BypassInterceptors
@Name("excelExport")
@Scope(ScopeType.EVENT)
public class ExcelExport implements Serializable {

	private static final long serialVersionUID = 5002322354424455216L;
	private final static Logger log = Logger.getLogger(ExcelExport.class);

	protected String dataTable = null;
	protected String listName = null;
	protected String[] headers = null;
	protected String[] columns = null;

	/**
	 * Exports current DataTable to MSExcel file
	 * 
	 * @param event - the event
	 */
	public void convertToExcel(ActionEvent event) {
		UIComponent component = (UIComponent)event.getComponent();
		
		// get DataTable name and LIST object name
		getParameters(component.getChildren());

		// get DataTable
		UIData dt = (UIData)component.findComponent(dataTable);
		IdataModel dataModel = null;
		
		FunctionsBean fun = FunctionsBean.instance();
		
		// get RowsData in several ways
		if (dt != null) { // from DataTable value...
			Object currentDM = dt.getValue();
			if (currentDM instanceof IdataModel)
				dataModel = (IdataModel)currentDM;
		}
		if (dataModel == null) { // ...or from conversation
			Object currentList = Component.getInstance(listName);
			if (currentList instanceof IdataModel)
				dataModel = (IdataModel)currentList;
		}

			OutputStream output = null;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			ResourceBundle messages = ResourceBundle.getBundle("bundle.error.messages",Locale.instance());
			try {
				// get servlet response and init it
				HttpServletResponse response = (HttpServletResponse)facesContext.getExternalContext().getResponse();
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Expires", "0");
				response.setHeader("Content-Disposition", "attachment;filename="+dataTable+".xls");
				output = response.getOutputStream();
				log.info("["+getDate("dd/MM/yyyy - HH:mm")+"] Exporting table into excel format [START]");
				// create excel workbook with a work sheet
				WritableWorkbook wb = Workbook.createWorkbook(output);
				WritableSheet sheet = wb.createSheet("First Sheet", 0);
				// get headers from data table
				getHeaders(dt, sheet);
				
				if (dataModel != null) {
					for (int i=0;i<dataModel.getList().size();i++) {
						// set current row
						Object currRow = dataModel.getList().get(i);
						
						if (currRow instanceof Object[]) {
							// write row on work sheet
							writeCellsFromArray((Object[])currRow, sheet, i+1, true);
						} else if (currRow instanceof BaseEntity) {
							//TODO: to cleanup code and write better
							//get properties related to the DataGrid entity
							List<Object> values = new ArrayList<Object>(); 
							for(String binding : columns){
								try {
									String[] propertiesTree = binding.split("\\.");
									Object valueFromObj = currRow;
									for (int j=1;j<propertiesTree.length;j++) {
										valueFromObj = PropertyUtils.getProperty(valueFromObj,propertiesTree[j]);
										if (valueFromObj == null) {
											break;
										}
									}
									if(valueFromObj!=null) {
										values.add(valueFromObj);
									} else {
										values.add("");
									}
								} catch (Exception e) {
									sheet.removeRow(0);
									sheet.addCell(new Label(0,0,messages.getString("no.excel.exportable.table")));
									sheet.addCell(new Label(0,1,messages.getString("no.excel.exportable.table.solution")));
									e.printStackTrace();
								}
							}
							writeCellsFromArray(values.toArray(), sheet, i+1);
							       
						} else if (currRow instanceof Map) {
							//TODO: to cleanup code and write better
							//get properties related to the DataGrid entity
							List<Object> values = new ArrayList<Object>(); 
							for(String binding : columns){
								try {
									Map<Object,Object> currMap = (Map<Object,Object>)currRow;
									String propertyBinding = binding.substring(binding.indexOf(".")+1);
									Object valueFromObj = fun.resolveMapProperty(currMap, propertyBinding);
									
									if(valueFromObj!=null) {
										values.add(valueFromObj);
									} else {
										values.add("");
									}
								} catch (Exception e) {
									sheet.removeRow(0);
									sheet.addCell(new Label(0,0,messages.getString("no.excel.exportable.table")));
									sheet.addCell(new Label(0,1,messages.getString("no.excel.exportable.table.solution")));
									e.printStackTrace();
								}
							}
							writeCellsFromArray(values.toArray(), sheet, i+1);
							       
						} else {
							// if data come from old reads, generate an xls file with error message
							sheet.removeRow(0);
							sheet.addCell(new Label(0,0,messages.getString("no.excel.exportable.table")));
							sheet.addCell(new Label(0,1,messages.getString("no.excel.exportable.table.solution")));
							break;
						}
					}
				}
				wb.write();
				wb.close();
				log.info("["+getDate("dd/MM/yyyy - HH:mm")+"] Exporting table into excel format [FINISH]");
				output.flush();
				output.close();
			} catch (IOException e) {
				log.error("["+getDate("dd/MM/yyyy - HH:mm")+"] I/O error",e);
			} catch (RowsExceededException e) {
				log.error("["+getDate("dd/MM/yyyy - HH:mm")+"] Too many rows",e);
			} catch (WriteException e) {
				log.error("["+getDate("dd/MM/yyyy - HH:mm")+"] Error writing data on sheet",e);
			}
			facesContext.responseComplete();

	}

	/**
	 * Gets column headers from given data table and writes values as sheet headers
	 * 
	 * @param dataTable - the data table
	 * @param sheet - current excel sheet
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	protected void getHeaders (final UIData dataTable, WritableSheet sheet) throws RowsExceededException, WriteException {
		// loop through data table children
		if (dataTable != null) {
			for (int i=0;i<dataTable.getChildCount();i++) {
				UIComponent child = dataTable.getChildren().get(i);
				// filter column children
				if (child instanceof UIColumn) {
					// get current column header
					ValueHolder header = (ValueHolder)UIColumn.class.cast(child).getFacet("header");
					// add header value into current sheet
					sheet.addCell(new Label(i,0,header != null ? header.getValue()+"" : ""));
				}
			}
		} else if (headers != null && headers.length != 0) {
			for (int i=0;i<headers.length;i++) {
				sheet.addCell(new Label(i,0,headers[i] != null ? headers[i] : ""));
			}
		}
	}

	/**
	 * Gets current row values and write them into given sheet 
	 * 
	 * @param rowCells - object array containing current row values
	 * @param sheet - current excel sheet
	 * @param rowIndex - current excel sheet row index
	 * @throws RowsExceededException
	 * @throws WriteException
	 */
	protected void writeCellsFromArray(Object[] rowCells, WritableSheet sheet, int rowIndex) throws RowsExceededException, WriteException {
		writeCellsFromArray(rowCells, sheet, rowIndex, false);
	}
	
	protected void writeCellsFromArray(Object[] rowCells, WritableSheet sheet, int rowIndex, boolean hasIdData) throws RowsExceededException, WriteException {
		if (rowCells != null) {
			// loop on all row objects but id column
			for (int colIndex=0;(colIndex+(hasIdData ? 1 : 0))<rowCells.length;colIndex++) {
				Object cell = rowCells[colIndex+(hasIdData ? 1 : 0)];
				if (cell instanceof CodeValue){
					// for code values get translation if exists. If not use display name. Get CV code as extra data
					CodeValue cv = (CodeValue) cell;
					String label = cv.getDisplayName();
					String translation = cv.getTranslation(Locale.instance().getLanguage());
					if (translation != null)
						label = translation;
					sheet.addCell(new Label(colIndex,rowIndex,label + " ["+cv.getCode()+"]"));
				} else if (cell instanceof Date) {
					// for TimeStamps get date...
					Date date = (Date)cell;
					Date fixedDate = null;
					if (date != null) {
						Calendar cal = new GregorianCalendar();
						cal.setTime(date);
						// ... and fix it considering current timezone
						fixedDate = new Date(date.getTime()+(cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)));
					}
					sheet.addCell(fixedDate != null ? new DateTime(colIndex,rowIndex,fixedDate) : new Label(colIndex,rowIndex,""));
				} else {
					// in other cases get string value or use empty string for null objects
					sheet.addCell(new Label(colIndex,rowIndex,cell != null ? cell.toString() : ""));
				}
			}
		}
	}

	/**
	 * Gets dataTable and listName parameters from event invoker component 
	 * 
	 * @param children - event invoker component children list
	 */
	protected void getParameters(List<UIComponent> children) {
		for (UIComponent child : children) {
			if (child instanceof UIParameter) {
				UIParameter parameter = (UIParameter)child;
				if ("dataTable".equals(parameter.getName())) {
					dataTable = (String)parameter.getValue();
				} else if ("tableHeaders".equals(parameter.getName())) {
					String h = (String)parameter.getValue();
					if (h != null && !h.isEmpty())
						headers = h.split("-/-");
				} else if ("tableColumns".equals(parameter.getName())) {
					String h = (String)parameter.getValue();
					if (h != null && !h.isEmpty())
						columns = h.split("-/-");
				} else if ("listName".equals(parameter.getName())) {
					listName = (String)parameter.getValue();
				}
			}
		}

	}


	/**
	 * Given a date format string, this returns a formatted date string of current time
	 * 
	 * @param format - a date format string
	 * @return a formatted date string of current time
	 */
	protected String getDate(String format) {
		String result = null;

		Date current = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.instance());
		result = sdf.format(current);

		return result;
	}

}
