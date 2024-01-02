package com.phi.rest.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Locale;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.actions.CodeSystemAction;
import com.phi.entities.actions.CodeValueAction;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.rest.datamodel.TreeNodeObject;

@Path("/tree/dictionary")
public class DictionaryTreeRest {

	private static final Logger log = Logger.getLogger(DictionaryTreeRest.class);
	
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

	List<String> allowedDomains = new ArrayList<String>();
	
	/**
	 * Builds a tree of TreeNodeObject starting from root node cv
	 * @param treeNode
	 * @param codeValueClass the EntityClass to substitute in the search for children
	 * @param cv
	 * @return
	 * @throws PersistenceException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private TreeNodeObject loadTree(String cvId, TreeNodeObject treeNode, String dataComponent, Integer depth, Integer maxDepth, CatalogAdapter ca, String currentLang, String codeValueClass, Boolean disabled) throws PersistenceException {

		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("parentId", cvId);
		
		String translatedQuery=QRY_CODE_VALUE_CHILDREN;
		if (!currentLang.equals("it")) {
			translatedQuery = QRY_CODE_VALUE_CHILDREN.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
		}
		if(codeValueClass!=null){ //do this to no more join on all CodeValue extending class, but only on specific codeSystem
			translatedQuery=translatedQuery.replace("CodeValue", codeValueClass);
		}
		
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
			}
			
			TreeNodeObject treeNodechild = buildCvNode(id,display,description,translation,code, childCount, typeDb, dataComponent, codeValueClass);
			treeNodechild.getState().put("disabled", disabled);
			
			if (treeNode.getChildren() == null) {
				List<TreeNodeObject> nodeChilds = new ArrayList<TreeNodeObject>();
				treeNode.setChildren(nodeChilds);
			}
			((List)treeNode.getChildren()).add(treeNodechild);
			if (depth < maxDepth) {
				loadTree((String)child[0], treeNodechild, dataComponent, depth + 1, maxDepth, ca, currentLang, codeValueClass, disabled);
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

	@POST
	@Path("inject")
	@Produces("application/json; charset=utf-8")
	public Response inject(
			@FormParam("id") String id, 
			@FormParam("class") String clazz) {

		try{
			Serializable serialId;
			if (id.matches("\\d*")) {
				serialId = Long.parseLong(id);
			} else {
				serialId = id;
			}

			Object obj = CatalogPersistenceManagerImpl.instance().load(clazz, serialId);
			Contexts.getConversationContext().set(obj.getClass().getSimpleName(), obj);
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@Path("getTree")
	@Produces("application/json; charset=utf-8")
	public Response getTree(
			@FormParam("levelOfDepth") String levelOfDepthString, 
			@FormParam("parentId") String parentIdString,
			@FormParam("codeSystem") String codeSystem,
			@FormParam("domain") String domain,
			@FormParam("dataComponent") String dataComponent,
			@FormParam("codeValueClass") String className,
			@FormParam("disabled") String disabledCondition) {

		try{
			
			Integer levelOfDepth = null;
			
			if (levelOfDepthString != null) {
				levelOfDepth = Integer.parseInt(levelOfDepthString);
			}
			
			Boolean disabled = "true".equals(disabledCondition); 
			
			CodeValueAction cva = CodeValueAction.instance();
			allowedDomains=(List<String>)cva.getTemporary().get("allowedDomains");
			if (allowedDomains == null )
				allowedDomains = new ArrayList<String>();

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
				
				List<Object[]> idAndTranslation = ca.executeHQLwithParameters(translatedQuery, pars );
				
				if (idAndTranslation.size() != 1) {
					throw new IllegalArgumentException("Unable to find CodeValue with displayName: " + domain + " of code system: " + codeSystem);
				}
				
				cvId = (String)idAndTranslation.get(0)[0];
				if (!allowedDomains.isEmpty() && !allowedDomains.contains(cvId)) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Domain is not allowed").build();
				}
				String cvTranslation = (String)idAndTranslation.get(0)[1];
				codeValueClass= (String)idAndTranslation.get(0)[2];
				TreeNodeObject rootNodez = new TreeNodeObject();
				rootNodez.setText(cvTranslation);
				rootNodez.setType("DOMAIN");
				
				rootNodez.getState().put("opened", true);
				rootNodez.getState().put("disabled", disabled);
				rootNode = loadTree(cvId, rootNodez, dataComponent, 2, levelOfDepth, ca, currentLang, codeValueClass, disabled).getChildren();

				
			} else if ((parentIdString == null || parentIdString.isEmpty()) && ( domain == null || domain.isEmpty() || "null".equals(domain)) ) {
				//here to have all the nodes for a code system, without a specific a domain. 2
				//Need to  build a root node with code system name, and as many node as the top levels.
				
				TreeNodeObject rootNodez = new TreeNodeObject(); 
				rootNodez.setText(codeSystem);
				rootNodez.getState().put("opened", true);
				rootNodez.getState().put("disabled", disabled);
				rootNodez.setType("ROOT");
				
				//treeNodeOpenStatus.put(codeSystem, true);
				CodeSystemAction csa = CodeSystemAction.instance();
				if(csa.getEntity()==null){
					csa.inject(csa.getCodeSystem(codeSystem));
				}
				List<Object[]> idAndTranslation = CodeSystemAction.instance().getTopLevel();
				Contexts.getConversationContext().set("listTopLevel",idAndTranslation);  //used in CodeValueAction for suggest method
				
				if (idAndTranslation == null || idAndTranslation.isEmpty() ) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Unable to find top level domain for code system: " + codeSystem).build();
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
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Domain is not allowed").build();
				}
				TreeNodeObject rootNodez = new TreeNodeObject();
				rootNodez.setText("ROOT");
				
				rootNode = loadTree(cvId, rootNodez, dataComponent, 2, levelOfDepth, ca, currentLang, className, disabled).getChildren();

			}
			String json;
			if (rootNode != null) {
				json = mapper.writeValueAsString(rootNode);
			} else {
				json = "[]";
			}
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("search")
	@Produces("application/json; charset=utf-8")
	public Response search(
			@FormParam("str") String search,
			@FormParam("codeValueClass") String className) {

		try{
			String currentLang = Locale.instance().getLanguage();
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("suggestion", "%" + search.toUpperCase() + "%");
			
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			
			String effectiveQuery=QRY_CODE_VALUE_SEARCH;
			if (!className.equals("CodeValue")) {
				effectiveQuery = effectiveQuery.replace("CodeValue", className);
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
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("create")
	@Produces("application/json; charset=utf-8")
	public Response create(
			@FormParam("id") String id,
			@FormParam("node_type") String node_type,
			@FormParam("name") String name,
			@FormParam("translation") String translation,
			@FormParam("generateId") String generateIdString,
			@FormParam("readableId") String readableIdString) {

		try{
			CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
			CodeSystemAction csa = CodeSystemAction.instance();
			CodeValueAction cva = CodeValueAction.instance();
			
			//if true the generated id depends on readableId id, if false id = parent.id + this.code
			boolean generateId = true;
			if ("false".equals(generateIdString)) {
				generateId = false;
			}
			
			//if true the generated id is based on first free abbreviation of the name
			boolean readableId = false;
			if ("true".equals(readableIdString)) {
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
			return Response.ok(json).build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@POST
	@Path("delete")
	@Produces("application/json; charset=utf-8")
	public Response delete(
			@FormParam("id") String id) {

		try{
			CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
			CodeValueAction cva = CodeValueAction.instance();
			CodeValue cv = (CodeValue)CatalogPersistenceManagerImpl.getAdapter().get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), id);
			cva.inject(cv);
			cva.delete();
			cva.flushSession();
			 
			CodeValue cvParent= cv.getParent();
			if (cvParent != null ) {
				cva.refresh(cvParent);
			}
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("select")
	@Produces("application/json; charset=utf-8")
	public Response select(
			@FormParam("listName") String listName,
			@FormParam("selected") String selected,
			@FormParam("parents[]") String parents[]) {

		try{

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
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("unselect")
	@Produces("application/json; charset=utf-8")
	public Response unselect(
			@FormParam("listName") String listName,
			@FormParam("unselected") String unselected,
			@FormParam("codeValueClass") String clazz) {

		try{

			if(listName==null || listName.isEmpty()){
				listName="CodeValueList";
			}
			
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("parentId", unselected);

			String queryAllChildren = "SELECT cv.id FROM " + clazz + " cv " +
					"LEFT JOIN cv.children cvChildren " +
					"WHERE cv.parent.id = :parentId ";
			
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
			
			return Response.ok().build();

		} catch (Exception e) {
			log.error("Error inject " , e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error: " + e.getMessage()).build();
		}
	}
}
