package com.phi.cs.view;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Locale;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.hbs.HBSManager;
import com.phi.cs.hbs.TreeNodeObject;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.actions.CodeSystemAction;
import com.phi.entities.actions.CodeValueAction;
import com.phi.entities.actions.CodeValueRoleAction;
import com.phi.entities.actions.EmployeeRoleAction;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

/**
 * Manages ajax request from JQuery UI components like autocomplete, jsTree...
 * 
 * @author Zupan
 */
public class AjaxServlet extends HttpServlet {
	/*
	 * TODO: WHEN AjaxServlet will be switched down, DELETE package com.phi.cs.hbs
	 */
	
	private static final long serialVersionUID = -5126950382069410741L;
	private static final Logger log = Logger.getLogger(AjaxServlet.class);
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		//mapper.registerModule(new HibernateModule());
	}
	
	//Dictionary tree Queries
	
	//FIXME: check if is it possible to avoid unions
	private static final String QRY_CODE_VALUE_BY_NAME_AND_CODESYSTEM = "SELECT cv.id, cv.langIt,codSys.codeValueClass FROM CodeValue cv " +
			"JOIN cv.codeSystem codSys " +
			"WHERE cv.displayName =:display " +
//			"AND codSys.name = :name" +
			"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :name AND cssub.status = 1)";
	
	//FIXME: check if is it possible to avoid unions
	private static final String QRY_CODE_VALUE_CHILDREN = "SELECT cv.id, cv.displayName, cv.description, cv.langIt, count(cvChildren), cv.code, cv.type FROM CodeValue cv " +
			"LEFT JOIN cv.children cvChildren " +
			"WHERE cv.parent.id = :parentId " +
			"GROUP BY cv.id, cv.displayName, cv.description, cv.langIt, cv.code, cv.type " +
			"ORDER BY cv.displayName";
	

	private static final String QRY_CODE_VALUE_SEARCH = "SELECT cv " +
			"FROM CodeValue cv " +
			"WHERE ( upper(cv.displayName) like :suggestion " +
			"OR upper(cv.langIt) like :suggestion " +
			"OR upper(cv.code) like :suggestion) " +
			"ORDER BY upper(cv.displayName)";
	
	private static final String QRY_SDL_ALL_CHILDREN = 
			"SELECT child.internalId " +
			"FROM " +
				"ServiceDeliveryLocation child, " +
				"ServiceDeliveryLocation parent " +
			"WHERE " +
				"child.nodeInfo.nsLeft between parent.nodeInfo.nsLeft and parent.nodeInfo.nsRight " +
				"and parent.internalId = :sdlId";

	

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doWork(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doWork(req, resp);
	}

	List<String> allowedDomains = new ArrayList<String>();
	List<String> allowedCodes = new ArrayList<String>();
	
	
	public void doWork(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String operation = null;
		PrintWriter out = null;

		try {

			operation = request.getParameter("operation");
			
			if (operation == null) {
				return;
			}
			
			out = response.getWriter();
			response.setContentType("application/json");

			if ("inject".equals(operation)) {
				// Injects in conversation an object
				String id = request.getParameter("id");
				String clazz = request.getParameter("class");

				Serializable serialId;

				if (id.matches("\\d*")) {
					serialId = Long.parseLong(id);
				} else {
					serialId = id;
				}

				Object obj = CatalogPersistenceManagerImpl.instance().load(clazz, serialId);

				Contexts.getConversationContext().set(obj.getClass().getSimpleName(), obj);

			} else if (operation.startsWith("HbsGetTree") || "DictionaryGetTree".equals(operation)) {
				//Geserate Json tree of ServiceDeliveryLocations or CodeValues
				
				HBSManager hbs = new HBSManager();
				
				String levelOfDepthString = request.getParameter("levelOfDepth");
				
				Integer levelOfDepth = null;
				
				if (levelOfDepthString != null) {
					levelOfDepth = Integer.parseInt(levelOfDepthString);
				}
				
				String parentIdString = request.getParameter("parentId");
				
				Long parentId = null;
				
				if (parentIdString != null && parentIdString.matches("\\d*")) {
					parentId = Long.parseLong(parentIdString);
				}
				
				if ("HbsGetTree".equals(operation)) {
					// Used creating hbs structure

					String json = hbs.getJsonTree(null, false, false, levelOfDepth, parentId, null);
					out.print(json);
	
				} else if ("HbsGetTree4CurrentEmployeeRole".equals(operation)) {
					// Used creating employee role
					EmployeeRole eRole = EmployeeRoleAction.instance().getEntity();
					
					if (eRole != null) {
						String json = hbs.getJsonTree(eRole.getInternalId(), false, false, levelOfDepth, parentId, null);
						out.print(json);
					} else {
						out.print("{}");
					}
		
				} else if ("HbsGetTree4CurrentUser".equals(operation)) {
					// Used at login to populate tree
	
					String loginSdlocFromCondevalueRole =  RepositoryManager.instance().getSeamProperty("loginSdlocFromCondevalueRole");
					
					Long selectedRole = Long.parseLong(request.getParameter("selectedRole"));
					boolean sdlocFromCondevalueRole = false;
					
					if ("true".equals(loginSdlocFromCondevalueRole)) {
						sdlocFromCondevalueRole = true;
					}
	
					String json = hbs.getJsonTree(selectedRole, sdlocFromCondevalueRole, true, levelOfDepth, parentId, null);
					out.print(json);
	
				} else if ("HbsGetTree4CodeValueRole".equals(operation)) {
					// Used during editing of CodeValueRole
	
					CodeValueRole codeValueRole = CodeValueRoleAction.instance().getEntity();
					
					List<Long> cvrEnabledSdl = new ArrayList<Long>();
					
					if (codeValueRole.getEnabledServiceDeliveryLocations() != null) {
						
						for (ServiceDeliveryLocation sdl : codeValueRole.getEnabledServiceDeliveryLocations()) {
							cvrEnabledSdl.add(sdl.getInternalId());
						}
					}
	
					String json = hbs.getJsonTree(null, false, false, levelOfDepth, parentId, cvrEnabledSdl);
					out.print(json);
	
				} else if ("DictionaryGetTree".equals(operation)) {
					//Generates Json tree of dictionary domain starting with oid

					String codeSystem = request.getParameter("codeSystem");
					String domain = request.getParameter("domain");
					String dataComponent =  request.getParameter("dataComponent");
					String className = request.getParameter("codeValueClass");
					String disabledCondition = request.getParameter("disabled");
					
					String validityDateStr = request.getParameter("validityDate");
					Date validityDate = null;
					if (validityDateStr != null) {
						DateTimeFormatter fmt = ISODateTimeFormat.dateTimeParser();
				    	validityDate = fmt.parseLocalDateTime(validityDateStr).toDate();
					}
					
					
					
					Boolean disabled = "true".equals(disabledCondition); 
					
					CodeValueAction cva = CodeValueAction.instance();
					allowedDomains=(List<String>)cva.getTemporary().get("allowedDomains");
					if (allowedDomains == null )
						allowedDomains = new ArrayList<String>();
 
					allowedCodes=(List<String>)cva.getTemporary().get("allowedCodes");
					if (allowedCodes == null )
						allowedCodes = new ArrayList<String>();

					CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
					
					String currentLang = Locale.instance().getLanguage();
					
					Object rootNode = null;
					String cvId = null;
					String codeValueClass=null;
					
					if ((parentIdString == null || parentIdString.isEmpty()) && domain != null && !domain.isEmpty() && !"null".equals(domain)) {
						//loading root node 1
						
						HashMap<String, Object> pars = new HashMap<String, Object>();

						pars.put("name", codeSystem);
						pars.put("display", domain);
						
						String translatedQuery=QRY_CODE_VALUE_BY_NAME_AND_CODESYSTEM;
						if (!currentLang.equals("it")) {
							translatedQuery = QRY_CODE_VALUE_BY_NAME_AND_CODESYSTEM.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
						}
						
						if (className != null && !className.isEmpty()) {
							translatedQuery = translatedQuery.replace("CodeValue", className);
						}
						
						@SuppressWarnings("unchecked")
						List<Object[]> idAndTranslation = ca.executeHQLwithParameters(translatedQuery, pars );
						
						if (idAndTranslation.size() != 1) {
							throw new IllegalArgumentException("Unable to find CodeValue with displayName: " + domain + " of code system: " + codeSystem);
						}
						
						cvId = (String)idAndTranslation.get(0)[0];
						if (!allowedDomains.isEmpty() && !allowedDomains.contains(cvId)) {
							return;
						}
						String cvTranslation = (String)idAndTranslation.get(0)[1];
						codeValueClass= (String)idAndTranslation.get(0)[2];
						TreeNodeObject rootNodez = new TreeNodeObject();
						rootNodez.setText(cvTranslation);
						rootNodez.setType("DOMAIN");
						
						rootNodez.getState().put("opened", true);
						rootNodez.getState().put("disabled", disabled);
						//setTreeNodeOpenStatus(cvId,Boolean.TRUE);
						rootNode = loadTree(cvId, rootNodez, dataComponent, 2, levelOfDepth, ca, currentLang, codeValueClass, disabled, validityDate).getChildren();

						
					} else if ((parentIdString == null || parentIdString.isEmpty()) && ( domain == null || domain.isEmpty() || "null".equals(domain)) ) {
						//here to have all the nodes for a code system, without a specific a domain. 2
						//Need to  build a root node with code system name, and as many node as the top levels.
						
						TreeNodeObject rootNodez = new TreeNodeObject(); 
						rootNodez.setText(codeSystem);
						rootNodez.getState().put("opened", true);
						rootNodez.getState().put("disabled", disabled);
						rootNodez.setType("ROOT");
						
						//treeNodeOpenStatus.put(codeSystem, true);
						List<Object[]> idAndTranslation = new ArrayList<Object[]>();
						if(validityDate!=null){
							idAndTranslation = CodeSystemAction.instance().getTopLevel(validityDate);
						}else{
							idAndTranslation = CodeSystemAction.instance().getTopLevel();
						}
						
						Contexts.getConversationContext().set("listTopLevel",idAndTranslation);  //used in CodeValueAction for suggest method
						
						if (idAndTranslation == null || idAndTranslation.isEmpty() ) {
							//throw new IllegalArgumentException("Unable to find top level domain for code system: " + codeSystem);
							//String json = mapper.writeValueAsString(new ArrayList());
							out.print("{\"data\":\"Empty\",\"state\":\"open\"}");
							return;
						}
						
						
						for (Object[] result : idAndTranslation) {
						
							
							String id=(String)result[0];
							String display = (String)result[1];
							String description =(String)result[2];
							String translation =(String)result[3];
							String typeDb = (String)result[4];
							String code = (String)result[5];
							if (!typeDb.equals("C") && !allowedDomains.isEmpty() && !allowedDomains.contains(id)) {
								continue;
							}
							
							//because not calculated, childcount is set to -1 to do not have conflict 
							TreeNodeObject treeNodechild = buildCvNode(id,display,description,translation, code, -1, typeDb, dataComponent, codeValueClass);
							if (treeNodechild != null) {
								if (rootNodez.getChildren() == null) {
									List<TreeNodeObject> rootChilds = new ArrayList<TreeNodeObject>();
									rootNodez.setChildren(rootChilds);
								}
								((List)rootNodez.getChildren()).add(treeNodechild);
							} else {
								rootNodez.setChildren(Boolean.FALSE);
								
							}
						}
						rootNode = rootNodez;
						
					}
					else {
						//loading child nodes
						cvId = parentIdString;
						if (!allowedDomains.isEmpty() && !allowedDomains.contains(cvId)) {
							return;
						}
						//setTreeNodeOpenStatus(cvId,Boolean.TRUE);
						codeValueClass=request.getParameter("codeValueClass");
						TreeNodeObject rootNodez = new TreeNodeObject();
						rootNodez.setText("ROOT");
						
						rootNode = loadTree(cvId, rootNodez, dataComponent, 2, levelOfDepth, ca, currentLang, codeValueClass, disabled, validityDate).getChildren();

					}

					
					String json = mapper.writeValueAsString(rootNode);
					out.print(json);

				}
			} else if ("HbsCreate".equals(operation)) {

				String id = request.getParameter("id");
				String name = request.getParameter("name");
				String node_type = request.getParameter("node_type");

				HBSManager hbs = new HBSManager();
				hbs.setId_node(Long.parseLong(id));
				hbs.setNode_name(name);
				hbs.setNode_code(node_type);

				TreeNodeObject treeNode = hbs.add();

				String json = mapper.writeValueAsString(treeNode);
				out.print(json);

			} else if ("HbsRename".equals(operation)) {

				String id = request.getParameter("id");
				String name = request.getParameter("name");

				HBSManager hbs = new HBSManager();
				hbs.setId_node(Long.parseLong(id));
				hbs.setNode_name(name);

				hbs.rename();
				
				out.print("{}");

			} else if ("HbsDisable".equals(operation)) {

				String id = request.getParameter("id");
				String name = request.getParameter("name");

				HBSManager hbs = new HBSManager();
				hbs.setId_node(Long.parseLong(id));
				hbs.setNode_name(name);

				hbs.disable();
				
				out.print("{}");

			}else if ("HbsReEnable".equals(operation)) {

				String id = request.getParameter("id");
				String name = request.getParameter("name");

				HBSManager hbs = new HBSManager();
				hbs.setId_node(Long.parseLong(id));
				hbs.setNode_name(name);

				hbs.reEnable();
				
				out.print("{}");

			} else if ("HbsDelete".equals(operation)) {

				String id = request.getParameter("id");

				HBSManager hbs = new HBSManager();
				hbs.setId_node(Long.parseLong(id));

				hbs.delete();
				
				out.print("{}");

			} else if ("HbsSelect".equals(operation) || "HbsSelect4CodeValueRole".equals(operation)) {
				// Used creating employee role or CodeValueRole to select SDLOC
				String selected = request.getParameter("selected");
				String parents[] = request.getParameterValues("parents[]");
				String clazz = request.getParameter("class");

				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				EmployeeRole empRole = EmployeeRoleAction.instance().getEntity();
				CodeValueRole codeValueRole = CodeValueRoleAction.instance().getEntity();

				
				HashMap<String, Object> pars = new HashMap<String, Object>();
				pars.put("sdlId", Long.parseLong(selected));

				@SuppressWarnings("unchecked")
				List<Long> ids = ca.executeHQLwithParameters(QRY_SDL_ALL_CHILDREN, pars);
				
				List<Long> parentAndChilds = new ArrayList<Long>();
				if (parents != null) {
					for (String parent : parents) {
						parentAndChilds.add(Long.parseLong(parent));
					}
				}
				parentAndChilds.addAll(ids);
				
				for (int i=0; i < parentAndChilds.size(); i++ ) {

					ServiceDeliveryLocation sdloc = (ServiceDeliveryLocation) ca.load(clazz, parentAndChilds.get(i));
					if ("HbsSelect".equals(operation)) {
						empRole.addEnabledServiceDeliveryLocations(sdloc);
					} else {
						codeValueRole.addEnabledServiceDeliveryLocations(sdloc);
					}

				}
				out.print("{}");

			} else if ("HbsSelect4CurrentUser".equals(operation)) {
				// Used at login to select SDLOC

				String[] ids = request.getParameterValues("id[]");
				List<Long> sdLocs = UserBean.instance().getSdLocs();

				for (String id : ids) {
					Long longId = Long.parseLong(id);
					if (!sdLocs.contains(longId)) {
						sdLocs.add(longId);
					}
				}
				
				out.print("{}");

			} else if ("HbsUnselect".equals(operation) || "HbsUnselect4CodeValueRole".equals(operation)) { 
				// Used creating employee role or CodeValueRole to unselect SDLOC

				String unselected = request.getParameter("unselected"); 
				String clazz = request.getParameter("class");
				
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				EmployeeRole empRole = EmployeeRoleAction.instance().getEntity();
				CodeValueRole codeValueRole = CodeValueRoleAction.instance().getEntity();
				
				HashMap<String, Object> pars = new HashMap<String, Object>();
				pars.put("sdlId", Long.parseLong(unselected));

				@SuppressWarnings("unchecked")
				List<Long> ids = ca.executeHQLwithParameters(QRY_SDL_ALL_CHILDREN, pars);
				
				for (int i=0; i <ids.size(); i++ ) {

					ServiceDeliveryLocation sdloc = (ServiceDeliveryLocation) ca.load(clazz, ids.get(i));
					if ("HbsUnselect".equals(operation)) {
						empRole.removeEnabledServiceDeliveryLocations(sdloc);
					} else {
						codeValueRole.removeEnabledServiceDeliveryLocations(sdloc);   
					}

				}
				out.print("{}");

			} else if ("HbsUnselect4CurrentUser".equals(operation)) {
				// Used at login to unselect SDLOC

				String[] ids = request.getParameterValues("id[]");
				List<Long> sdLocs = UserBean.instance().getSdLocs();

				for (String id : ids) {
					Long longId = Long.parseLong(id);
					if (sdLocs.contains(longId)) {
						sdLocs.remove(longId);
					}
				}
				
				out.print("{}");

			} else if ("DictionarySearch".equals(operation)) {
				// Searches into Dictionary 
				String codeValueClass = request.getParameter("codeValueClass");
				String search = request.getParameter("str");

				String currentLang = Locale.instance().getLanguage();

				HashMap<String, Object> pars = new HashMap<String, Object>();
				pars.put("suggestion", "%" + search.toUpperCase() + "%");
				
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				
				String effectiveQuery=QRY_CODE_VALUE_SEARCH;
				if (!codeValueClass.equals("CodeValue")) {
					effectiveQuery = effectiveQuery.replace("CodeValue", codeValueClass);
				}
				if (!currentLang.equals("it")) {
					effectiveQuery = effectiveQuery.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
				}
				
				@SuppressWarnings("unchecked")
				List<CodeValue> cvs = ca.executeHQLwithParameters(effectiveQuery, pars );

				//find parents till root
				List<String> allParentIds = new ArrayList<String>();
				for (CodeValue cv : cvs) {
					CodeValue curr = cv;
					while(curr.getParent() != null) {
						if (!allParentIds.contains(curr.getParent().getId())) {
							allParentIds.add(curr.getParent().getId());
						}
						curr = curr.getParent();
					}
				}

				String json = mapper.writeValueAsString(allParentIds);
				out.print(json);
				
			} else if ("CvCreate".equals(operation)) {
				
				CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
				CodeSystemAction csa = CodeSystemAction.instance();
				CodeValueAction cva = CodeValueAction.instance();

				String id = request.getParameter("id");
				String node_type = request.getParameter("node_type");
				String name = request.getParameter("name");
				String translation = request.getParameter("translation");
				
				//if true the generated id depends on readableId id, if false id = parent.id + this.code
				boolean generateId = true;
				if ("false".equals(request.getParameter("generateId"))) {
					generateId = false;
				}
				
				//if true the generated id is based on first free abbreviation of the name
				boolean readableId = false;
				if ("true".equals(request.getParameter("readableId"))) {
					readableId = true;
				}
				
				CodeValue cv = cva.createNew(id, node_type, name, translation, generateId, readableId, cs);

				cva.inject(cv);
				cva.create(cv);
				cva.flushSession();
				
				
				if (cv.getParent() != null) {
					//refresh father codeValue, because hibernate cache must be updated, to proper calculate next id brother codeValue.
					cva.refresh(cv.getParent());
				}
				else {
					//refresh list of top levels in conversation, for the same reason above.
					List<Object[]> idAndTranslation = csa.getTopLevel();
					Contexts.getConversationContext().set("listTopLevel",idAndTranslation);  //used in CodeValueAction for suggest method
				}
				
				
				if ("TOPLEVEL".equals(node_type) || "DOMAIN".equals(node_type)) {
					cva.addToCodeValueUsers(cv.getId());
				}
				
				TreeNodeObject treeNode = buildCvNode(cv.getId(),name,"","","-", 0, cv.getType(), "[code] - displayName", cs.getCodeValueClass()); 

				String json = mapper.writeValueAsString(treeNode);
				out.print(json);
				
			
			} else if ("CvDelete".equals(operation)) {
				
				String id = request.getParameter("id"); 
				CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
				CodeValueAction cva = CodeValueAction.instance();
				CodeValue cv = (CodeValue)CatalogPersistenceManagerImpl.getAdapter().get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), id);
//				CodeValue cv = cva.get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), id);
				
//				String type = cv.getType();
//				if (type.equals("S")|| type.equals("T")|| type.equals("A")) {
//					cva.removeFromCodeValueUsers(id);
//				}
				
				cva.inject(cv);
				cva.delete();
				cva.flushSession();
				 
				CodeValue cvParent= cv.getParent();
				if (cvParent != null ) {
					cva.refresh(cvParent);
				}
			}else if ("CvSelect".equals(operation)) {
				// Used in multiple selection trees of code values
				String listName = request.getParameter("listName");
				String selected = request.getParameter("selected");
				String parents[] = request.getParameterValues("parents[]");
				if(listName==null || listName.isEmpty()){
					listName="CodeValueList";
				}
				List<String> cvList = (List<String>)Contexts.getConversationContext().get(listName);
				
				if(cvList==null){
					cvList = new ArrayList<String>();
					Contexts.getConversationContext().set(listName,cvList);
				}
				if(!cvList.contains(selected))
					cvList.add(selected);
				
				if (parents != null) {
					for (String parent : parents) {
						if(!cvList.contains(parent))
							cvList.add(parent);
					}
				}
				
				
			}else if ("CvUnselect".equals(operation)) {
				// Used in multiple selection trees of code values
				String listName = request.getParameter("listName");
				String unselected = request.getParameter("unselected");
				String clazz = request.getParameter("codeValueClass");
				if(listName==null || listName.isEmpty()){
					listName="CodeValueList";
				}
				
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				
				HashMap<String, Object> pars = new HashMap<String, Object>();
				pars.put("parentId", unselected);

				String queryAllChildren = "SELECT cv.id FROM " + clazz + " cv " +
						"LEFT JOIN cv.children cvChildren " +
						"WHERE cv.parent.id = :parentId ";
				
				@SuppressWarnings("unchecked")
				List<String> ids = ca.executeHQLwithParameters(queryAllChildren, pars);
				List<String> cvList = (List<String>)Contexts.getConversationContext().get(listName);
				
				if(cvList==null){
					cvList = new ArrayList<String>();
					Contexts.getConversationContext().set(listName,cvList);
				}
				if (ids != null) {
					for (String id : ids) {
						cvList.remove(id);
					}
				}
				cvList.remove(unselected);
			}
			
			
		} catch (Exception e) {

			response.setStatus(500);
			response.setContentType("test/plain");

			if (e instanceof PhiException) {
				String msg = FacesErrorUtils.addErrorMessage(((PhiException) e).getCode(), e.getMessage());
				response.getWriter().print(msg);
			} else {
				response.getWriter().print(e.getMessage());
			}

			log.error("Error managing operation " + operation, e);

		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	
	/**
	 * Builds a tree of TreeNodeObject starting from root node cv
	 * @param treeNode
	 * @param codeValueClass the EntityClass to substitute in the search for children
	 * @param cv
	 * @return
	 * @throws PersistenceException 
	 */
	private TreeNodeObject loadTree(String cvId, TreeNodeObject treeNode, String dataComponent, Integer depth, Integer maxDepth, CatalogAdapter ca, String currentLang, String codeValueClass, Boolean disabled, Date validityDate) throws PersistenceException {

		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("parentId", cvId);
		
		String translatedQuery=QRY_CODE_VALUE_CHILDREN;
		if (validityDate != null) {
			translatedQuery = QRY_CODE_VALUE_CHILDREN.replace("GROUP BY", 
				"AND (cv.validFrom is null OR cv.validFrom <= :date) AND (cv.validTo is null OR cv.validTo >= :date) GROUP BY");
			pars.put("date", validityDate);
		}
		if (!currentLang.equals("it")) {
			translatedQuery = QRY_CODE_VALUE_CHILDREN.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
		}
		if(codeValueClass!=null){ //do this to no more join on all CodeValue extending class, but only on specific codeSystem
			translatedQuery=translatedQuery.replace("CodeValue", codeValueClass);
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> idDispnameDescTranslNofchilds = ca.executeHQLwithParameters(translatedQuery, pars );
		
		for (Object[] child : idDispnameDescTranslNofchilds) {
			
			
			String id=(String)child[0];
			String display = (String)child[1];
			String description =(String)child[2];
			String translation =(String)child[3];
			Long childCount = (Long)child[4];
			String code = (String)child[5];
			String typeDb = (String)child[6];
			if (!typeDb.equals("C") && !allowedDomains.isEmpty() && !allowedDomains.contains(id)) {
				continue;
			}else if(typeDb.equals("C") && !allowedCodes.isEmpty() && !allowedCodes.contains(id)){
				continue;
			}
			
			TreeNodeObject treeNodechild = buildCvNode(id,display,description,translation,code, childCount, typeDb, dataComponent, codeValueClass);
			treeNodechild.getState().put("disabled", disabled);
			
			if (treeNodechild != null) {

				if (treeNode.getChildren() == null) {
					List<TreeNodeObject> nodeChilds = new ArrayList<TreeNodeObject>();
					treeNode.setChildren(nodeChilds);
				}
				((List)treeNode.getChildren()).add(treeNodechild);
			} else {
				treeNode.setChildren(Boolean.FALSE);
			}
			
			if (depth < maxDepth) {
				loadTree((String)child[0], treeNodechild, dataComponent, depth + 1, maxDepth, ca, currentLang, codeValueClass, disabled, validityDate);
			}
//			else {
//				if ("closed".equals(treeNode.getState()) && Boolean.TRUE.equals(getTreeNodeOpenStatus((String)child[0]))) {
//					//open node which was already open.
//					treeNodechild.setState("open");
//					loadTree((String)child[0], treeNodechild, 1, 1, ca, currentLang, codeValueClass);
//				}
//			}
		}
		
		return treeNode;
	}

	private TreeNodeObject buildCvNode (String id, String display, String description, String translation, String code, 
			long childCount, String typeDb, String dataComponent, String codeValueClass) {
		TreeNodeObject treeNodechild = new TreeNodeObject();
		
		treeNodechild.setId(id);
		treeNodechild.setText(description);
		
		//titleComponent allowed values: "displayName - translation", "translation", "[code] displayName", "[code] translation"
		if (dataComponent == null || dataComponent.isEmpty())
			dataComponent= "translation"; 
		
		if (dataComponent.equals("displayName - translation")) {
			treeNodechild.setText(display + " - " + translation);
		} 
		else if (dataComponent.equals("translation")){ //show only translation
			if (translation == null || translation.isEmpty())
				translation = display;
			treeNodechild.setText(translation);	
		}
		else if (dataComponent.equals("[code] displayName")){ 
			String codeLabel = (code == null || code.isEmpty() || code.equals("-")) ? "" : "["+code+"] ";
				
			treeNodechild.setText(codeLabel+display);	
		}
		else if (dataComponent.equals("[code] translation")){ 
			if (translation == null || translation.isEmpty())
				translation = display;
			String codeLabel = (code == null || code.isEmpty() || code.equals("-")) ? "" : "["+code+"] ";
				
			treeNodechild.setText(codeLabel+translation);	
		}
		
		if (treeNodechild.getText() != null && treeNodechild.getText().isEmpty()) { 
			//Avoid infinite loop: if data is "" then jsTree will call ajaxservlet again -> infinite loop
			treeNodechild.setText(" ");
		}
		

		if (childCount == 0) { //no childs: leaf node
			treeNodechild.setChildren(Boolean.FALSE);
//			treeNodechild.getAttr().put("class", "jstree-leaf"); FIXMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
		} else {
			treeNodechild.setChildren(Boolean.TRUE);
		}
		
		if ("C".equals(typeDb)) {                      //leaf node, type C.
//			treeNodechild.getAttr().put("class", "jstree-leaf"); FIXMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
			treeNodechild.setChildren(Boolean.FALSE);
			treeNodechild.setType("CODE");
		}
		else if ("T".equals(typeDb)) {                      //top level domain
			treeNodechild.setType("TOPLEVEL");
		}
		else if ("A".equals(typeDb)) {  //intermediate abstract domain
			if ("CodeValueParameter".equals(codeValueClass)) {
				treeNodechild.setType("PARAMETER");
			} else {
				treeNodechild.setType("DOMAIN");
			}
		}
		else if ("S".equals(typeDb)) {  //intermediate specialized domain
			treeNodechild.setType("DOMAIN");
		}
		
		return treeNodechild;
		
	}
	
}