package com.phi.cs.hbs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.ApplicationException;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.actions.EmployeeRoleAction;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.role.ServiceDeliveryLocation;


/**
* MAnages Hospital Breakdown Structure. User manages categories
* with a tree view, nodes are dynamically managed by left-click or right-click.
*/
public class HBSManager implements Serializable {

	/*
	 * TODO: SAME AS com.phi.rest.datamodel.HBSManager: DELETE WHEN AjaxServlet will be switched down
	 */
	private static final long serialVersionUID = 7262633751159444501L;
	private static final Logger log = Logger.getLogger(HBSManager.class);
	
//	private static final String SDLOC_ROOT_OID ="2.16.840.1.113883.3.20.1.1";  //code: HBSSDLRT domain display: PHI_HOSPITAL_BREAKDOWN_STRUCTURE
//	private static final String SDLOC_CLASS_CODE_OID = "2.16.840.1.113883.3.20.1.1.598";  //it's a domain: PHI_HOSPITAL_BREAKDOWN_STRUCTURE with code HBSROOT
	
	//name of the domain/code used by hbs manager. These are default values  
	//use the same variable name in seam.properties to set different values.
	
	private static String SDLOC_ROOT_DOMAIN="PHI_HOSPITAL_BREAKDOWN_STRUCTURE";
	private static String SDLOC_ROOT_CODE="HBSROOT"; 
	private static boolean readed_SDLOC_Code = false;
	
	private static CodeValue cvRoot = null;
	private static CodeValue cvSdloc =  null;
	
	//private static final String SDLOC_ROOT_CODE = "2.16.840.1.113883.3.20.HBS.PRC.HBSSDLRT";
	//private static final String SDLOC_ROOT_QUERY = "SELECT sdl FROM ServiceDeliveryLocation sdl where code.oid = '" + SDLOC_ROOT_OID + "'";
	//private static final String SDLOC_ROOT_QUERY = "SELECT sdl FROM ServiceDeliveryLocation sdl where code.code = '" + SDLOC_ROOT_CODE + "'";
	private static final String SDLOC_ROOT_NAME = "Hospitals";
	
//	private static final String QUERY_SDLOC_ID_4_EMPLOYEE = "SELECT sdl.internalId FROM EmployeeRole emp JOIN emp.enabledServiceDeliveryLocations sdl WHERE emp.internalId = :idEmployee";
	
	private static final String QUERY_SDLOC_ALL = "select count(n1.internalId) as nestedSetLevel, n1.internalId, n1.name.giv, n1.code.code, n1.isActive, n1.nodeInfo.nsRight - n1.nodeInfo.nsLeft " +
			"from ServiceDeliveryLocation n1, ServiceDeliveryLocation n2 " +
			"where n1.nodeInfo.nsThread = n2.nodeInfo.nsThread " +
			"and n1.nodeInfo.nsLeft between n2.nodeInfo.nsLeft and n2.nodeInfo.nsRight " +
			"group by " +
			"n1.nodeInfo.nsLeft, " +
			"n1.nodeInfo.nsRight, " +
			"n1.nodeInfo.nsThread, " +
			"n1.internalId, " +
			"n1.name.giv, " +
			"n1.code.code, " +
			"n1.isActive " +
			"order by n1.nodeInfo.nsLeft asc)";
	
	private static final String QUERY_SDLOC_4_EMPLOYEE = "select count(n1.internalId) as nestedSetLevel, n1.internalId, n1.name.giv, n1.code.code, n1.isActive, n1.nodeInfo.nsRight - n1.nodeInfo.nsLeft " +
			"from ServiceDeliveryLocation n1, ServiceDeliveryLocation n2 " +
			"where n1.nodeInfo.nsThread = n2.nodeInfo.nsThread " +
			"and n1.nodeInfo.nsLeft between n2.nodeInfo.nsLeft and n2.nodeInfo.nsRight " +
			"and n1.internalId IN (SELECT sdl.internalId FROM EmployeeRole emp JOIN emp.enabledServiceDeliveryLocations sdl WHERE emp.internalId = :idEmployee) " +
			"group by " +
			"n1.nodeInfo.nsLeft, " +
			"n1.nodeInfo.nsRight, " +
			"n1.nodeInfo.nsThread, " +
			"n1.internalId, " +
			"n1.name.giv, " +
			"n1.code.code, " +
			"n1.isActive " +
			"order by n1.nodeInfo.nsLeft asc)";
	
	//Same as previous but filter sdloc by CodeValueRole.enabledServiceDeliveryLocations instead of EmployeeRole.enabledServiceDeliveryLocations
	private static final String QUERY_SDLOC_4_CODEVALUEROLE = "select count(n1.internalId) as nestedSetLevel, n1.internalId, n1.name.giv, n1.code.code, n1.isActive, n1.nodeInfo.nsRight - n1.nodeInfo.nsLeft " +
			"from ServiceDeliveryLocation n1, ServiceDeliveryLocation n2 " +
			"where n1.nodeInfo.nsThread = n2.nodeInfo.nsThread " +
			"and n1.nodeInfo.nsLeft between n2.nodeInfo.nsLeft and n2.nodeInfo.nsRight " +
			"and n1.internalId IN (SELECT sdl.internalId FROM EmployeeRole emp JOIN emp.code cvr JOIN cvr.enabledServiceDeliveryLocations sdl WHERE emp.internalId = :idEmployee) " +
			"group by " +
			"n1.nodeInfo.nsLeft, " +
			"n1.nodeInfo.nsRight, " +
			"n1.nodeInfo.nsThread, " +
			"n1.internalId, " +
			"n1.name.giv, " +
			"n1.code.code, " +
			"n1.isActive " +
			"order by n1.nodeInfo.nsLeft asc)";

	private TreeNodeObject rootTreeNode;
	
	private Long id_node;
	
	private String node_name;
	
	private String node_code;
	
//	private GenericAdapterLocalInterface ca;

	public HBSManager() {
//		ca = GenericAdapter.instance();//CatalogPersistenceManagerImpl.instance();
		loadHBSdomains();
	}

	public String getNode_name() {
		return node_name;
	}

	public void setNode_name(String node_name) {
		this.node_name = node_name;
	}

	public Long getId_node() {
		return id_node;
	}

	public void setId_node(Long id_node) {
		this.id_node = id_node;
	}
	
	public String getNode_code() {
		return node_code;
	}
	
	public void setNode_code(String node_code) {
		this.node_code = node_code;
	}
	
	
	/**
	 * Build a json tree os ServiceDelivery location
	 * @param employeeRoleId id of employee role, optional, to view only sdloc of that role
	 * @param sdlocFromCondevalueRole if true filter sdloc by CodeValueRole.enabledServiceDeliveryLocations instead of EmployeeRole.enabledServiceDeliveryLocations
	 * @param onlyEnabledSdlocs if true show only enabled sdloc, used at login
	 * @param levelOfDepth depth of tree to load
	 * @param parentId id of sdloc: to view only a part of the tree. Used for lazy loading
	 * @param enabledSdl list of id of sdl wich will be "checked" in tree
	 * @return
	 * @throws ApplicationException
	 * @throws PersistenceException
	 * @throws DictionaryException
	 */
	public String getJsonTree(Long employeeRoleId, boolean sdlocFromCondevalueRole, boolean onlyEnabledSdlocs, Integer levelOfDepth, Long parentId, List<Long> enabledSdl) throws ApplicationException, PersistenceException, DictionaryException {
		String jsonTreeStr = "{}";
		try {
			GenericAdapterLocalInterface ga = GenericAdapter.instance();
			
			String nsQueryLight = QUERY_SDLOC_ALL;  
			HashMap<String,Object> qryPars = null;
			List<Long> sdLocs = null;
			
			boolean loginTree = false;
			
			StringBuffer jsonTree = new StringBuffer();
			List<String> treeState = null;
			


			if (employeeRoleId != null) {
				
				qryPars = new HashMap<String, Object>();
				qryPars.put("idEmployee", employeeRoleId);
				
				if (onlyEnabledSdlocs) {
					
					if (sdlocFromCondevalueRole) { //Login tree based on enabled sdl defined in CodeVlaueRole
						nsQueryLight = QUERY_SDLOC_4_CODEVALUEROLE;
					} else { //Login tree based on enabled sdl defined in EmployeeRole
						nsQueryLight = QUERY_SDLOC_4_EMPLOYEE;
					}

					//jsonTree.append("{\"data\":\""+SDLOC_ROOT_NAME+"\",\"attr\":{\"rel\":\"HBSSDLRT\"},\"children\":[");
					loginTree = true;
				} else {
//					sdLocs =  ga.executeHQL(QUERY_SDLOC_ID_4_EMPLOYEE, qryPars);
					sdLocs = new ArrayList<Long>();
					EmployeeRole er = EmployeeRoleAction.instance().getEntity();
					if (er != null) {
						List<ServiceDeliveryLocation> enabSdl = er.getEnabledServiceDeliveryLocations();
						if (enabSdl != null) {
							for (ServiceDeliveryLocation sdl : enabSdl) {
								sdLocs.add(sdl.getInternalId());
							}
						}
					}
					qryPars = null;
				}
			
			}
			
			if (parentId == null && levelOfDepth != null) {
				nsQueryLight = nsQueryLight.replace("order by", "HAVING count(n1.internalId) <= " + levelOfDepth + " order by");
			}
			
			if (parentId != null) {
				if (levelOfDepth == 1) {//Open one level
					nsQueryLight = nsQueryLight.replace("group by", "and n1.parent = " + parentId + " group by");
				} else {//Open branch
					nsQueryLight = nsQueryLight.replace("group by", "and n1.nodeInfo.nsLeft > (SELECT nodeInfo.nsLeft FROM ServiceDeliveryLocation WHERE internalId = " + parentId + ") " +
						"and n1.nodeInfo.nsRight < (SELECT nodeInfo.nsRight FROM ServiceDeliveryLocation WHERE internalId = " + parentId + ") group by");
				}
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> rawNestedSet = ga.executeHQL(nsQueryLight, qryPars);
			Long prevDepth = 0l;

			if (!rawNestedSet.isEmpty()) {

				if (loginTree || parentId != null || enabledSdl != null) {
					//jsonTree.append("{\"text\":\"ROOT\",\"children\":[");
					jsonTree.append("[");
				}
				
				if (enabledSdl != null) {
					if (SDLOC_ROOT_CODE.equals(rawNestedSet.get(0)[3])) { //Remove root
						rawNestedSet.remove(0);
					}
				}
				
				Long realDepth = 0l;
				HashMap<Long, Long>realDepths = new HashMap<Long, Long>();
				//FunctionsBean.instance().getStaticTranslation("sdloc_root"); //"Strutture ospedaliere";
				//rawNestedSet.get(0)[2] = FunctionsBean.instance().getStaticTranslation("sdloc_root"); //"Strutture ospedaliere";
				for (int z = 0; z < rawNestedSet.size(); z++) {
					Object[] depthIdName = rawNestedSet.get(z);
					Long currDepth = (Long)depthIdName[0];
					Long currId = (Long)depthIdName[1];
					String currName = (String)depthIdName[2];
					String currCode = (String)depthIdName[3];
					Boolean isActive = (Boolean)depthIdName[4];
//					Long nsInterval = (Long)depthIdName[5];
					
					if (SDLOC_ROOT_CODE.equals(currCode)) {
						currName = FunctionsBean.instance().getStaticTranslation("sdloc_root");
					}
					
					if (currDepth.equals(prevDepth)) {
						jsonTree.append("]},");
					} else if (currDepth > prevDepth) {
						realDepth++;
					} else if (currDepth < prevDepth) {
						Long previousRealDepth = 0l;
						//Go backward to find node with depth smaller or equal to current
						for (int x = z-1; x>=0; x--) {
							Long previousDbDepth =(Long)rawNestedSet.get(x)[0];
							if (previousDbDepth.equals(currDepth)) {
								previousRealDepth = realDepths.get(previousDbDepth);
								break;
							} else if (previousDbDepth < currDepth) {
								previousRealDepth = realDepths.get(previousDbDepth) + 1;
								break;
							}
						}
						//Calculate real difference between depths
						Long realDifference = realDepth-previousRealDepth;
						for (int toClose = 0; toClose <= realDifference; toClose++) {
							jsonTree.append("]}");
						}
						jsonTree.append(",");
						realDepth= realDepth - realDifference;
					}

					realDepths.put(currDepth, realDepth);
					
					jsonTree.append("{\"text\":\"");
					jsonTree.append(currName.replace("\"", "\\\""));// Escape: " -> \"
//				jsonTree.append(currName + "[" + currId +  "]");//FIXME REMOVEEEEEEEEEEEE
					jsonTree.append("\",\"id\":\"");
					jsonTree.append(currId);
					jsonTree.append("\",\"type\":\"");
					jsonTree.append(currCode);
					jsonTree.append("\"");
					
					treeState = new ArrayList<String>();
					
					if (loginTree || (sdLocs != null && sdLocs.contains(currId))) {
						treeState.add("\"selected\":true");
					}
					if (enabledSdl != null && enabledSdl.contains(currId)) {
						treeState.add("\"selected\":true");
					}
					if(isActive != null && isActive == false ){
						treeState.add("\"disabled\":true");
					}
//					if(nsInterval==1){ //no childs: leaf node FIXME
//						treeState.append(" jstree-leaf");
//					}
					
					if (parentId == null && levelOfDepth != null) {
						if (currDepth < levelOfDepth) {
							treeState.add("\"opened\":true");
						} 
						else {
							treeState.add("\"opened\":false");
						}
					}
					if (parentId != null) {
						if (levelOfDepth == 1) {
							treeState.add("\"opened\":false");
						} else {
							treeState.add("\"opened\":true");
						}
					}
					
					if (!treeState.isEmpty()) {
						jsonTree.append(",\"state\":{");
						for (int ts=0;ts<treeState.size();ts++) {
							jsonTree.append(treeState.get(ts));
							if (ts<treeState.size()-1) {
								jsonTree.append(", ");
							}
						}
						jsonTree.append("}");
					}
							
					jsonTree.append(",\"children\":[");
					
					prevDepth = currDepth;
				}
				
				for (Long toClose = 1l; toClose <= realDepth; toClose++) {
					jsonTree.append("]}");
				}
				if (loginTree || parentId != null || enabledSdl != null) {
					jsonTree.append("]");
				}
			} else if (!loginTree && rawNestedSet.isEmpty() && parentId == null){
				//getting tree for hbs manager process
				//no sdl found, need to be created a new hbs root. BRAGA
				ServiceDeliveryLocation rootSdl = new ServiceDeliveryLocation();
				
				if (cvRoot == null) {
					Vocabularies voc = VocabulariesImpl.instance();
					cvRoot = voc.getCodeValueCsDomainCode("PHIDIC", SDLOC_ROOT_DOMAIN, SDLOC_ROOT_CODE); 
					
					if (cvRoot == null) {
						throw new IllegalStateException("Dictionary not contains code "+SDLOC_ROOT_CODE+ " in/or domain "+SDLOC_ROOT_DOMAIN);
					}
					
				}
				rootSdl.setCode(cvRoot);
				
				EN enToSet = new EN();
				enToSet.setGiv(SDLOC_ROOT_NAME);

				rootSdl.setName(enToSet);
				
				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				
				ca.create(rootSdl);
				
				ca.flushSession();
				
				jsonTree.append("{\"text\":\""+SDLOC_ROOT_NAME+"\",\"id\":\""+rootSdl.getInternalId()+"\",\"type\":\"HBSROOT\"}");
				
			} else {
				jsonTree.append("[]");
			}

			jsonTreeStr = jsonTree.toString();
			
			if (onlyEnabledSdlocs) { //login tree loaded all in one, has no children
				jsonTreeStr = jsonTreeStr.replace(",\"children\":[]", ",\"children\":false");
			} else {
				jsonTreeStr = jsonTreeStr.replace(",\"children\":[]", ",\"children\":true");
			}
			
			
		} catch (Exception e) {
			log.error("Error getting json tree", e);
		}
		return jsonTreeStr;
	}
	 
	
//	private void buildTree(TreeNodeObject rooTreeNode, List<NestedSetNodeWrapper<ServiceDeliveryLocation>> nsListOfSdl, EmployeeRole employeeRole, boolean onlyEnabledSdlocs){
//
//		for (NestedSetNodeWrapper<ServiceDeliveryLocation> sdlCh : nsListOfSdl) {
//			if (!onlyEnabledSdlocs || (onlyEnabledSdlocs && employeeRole.getEnabledServiceDeliveryLocations().contains(sdlCh.getWrappedNode()))) {
//				if (sdlCh.getWrappedNode().getIsActive()) {
//					TreeNodeObject treeNode = buildTreeNode(sdlCh.getWrappedNode(), employeeRole);
//					rooTreeNode.getChildren().add(treeNode);
//					buildTree(treeNode, sdlCh.getWrappedChildren(), employeeRole, onlyEnabledSdlocs);
//				}
//			} else {
//				buildTree(rooTreeNode, sdlCh.getWrappedChildren(), employeeRole, onlyEnabledSdlocs);
//			}
//		}
//
//	}
	
	//FIXME
	private TreeNodeObject buildTreeNode(ServiceDeliveryLocation sdl, EmployeeRole empRole) {
		TreeNodeObject treeNode = new TreeNodeObject();
		if (sdl.getName() != null && sdl.getCode() != null) {
		//get NAME.GIV
			treeNode.setText(sdl.getName().getGiv());
			//FIXME: better to use: treeNode.attr.put("id", "id-" + Long.toString(sdl.getInternalId())); 
			treeNode.setId(Long.toString(sdl.getInternalId()));
			treeNode.setType(sdl.getCode().getCode());
			//FIXME: remove attr
			if (empRole != null && empRole.getEnabledServiceDeliveryLocations() != null) {
				if (empRole.getEnabledServiceDeliveryLocations().contains(sdl)) {
					treeNode.getState().put("selected", true); 
				}
			}
		}	
		return treeNode;
	}
	
	/**
	 * getTree() gets the TreeNodeObject rootTreeNode, this TreeNodeObject is passed as parameter to the js function 
	 * responsible for building and viewing tree.
	 * This method is called from a4j's "data" tag
	 * 
	 */
	public TreeNodeObject getTree() {
		return rootTreeNode;
	}
	
	
	/**
	 * Creates RimObject - ServiceDeliveryLocation
	 * This method is called from a4j:jsFunction name="addNodeObject" 
	 */
	public TreeNodeObject add() throws PhiException {
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();

		ServiceDeliveryLocation parent_sdl = ca.get(ServiceDeliveryLocation.class, id_node);
		
		//check code and define child node's code
		CodeValue childNodeCode = null;
		//CodeValue classCode = null;
		
		Vocabularies voc = VocabulariesImpl.instance();

		childNodeCode = voc.getCodeValueCsDomainCode("PHIDIC", SDLOC_ROOT_DOMAIN, node_code);
		
		//no more set Class code.
		//classCode = voc.getCodeValueOid(SDLOC_CLASS_CODE_OID);  
//		if (cvSdloc ==null){
//			cvSdloc=voc.getCodeValueCsDomainCode("PHIDIC", "PHIDIC", SDLOC_CLASS_CODE);
//		}
		
		if (childNodeCode == null) {
			//throw new IllegalStateException("Dictionary does not contain " + codSdl + "!");
			throw new PersistenceException("CodeSystem: PHIDIC, Domain: "+SDLOC_ROOT_DOMAIN+", Code: " + node_code, ErrorConstants.PERSISTENCE_DICTIONARY_DOMAIN_NOT_FOUND_CODE);
		}
		
		ServiceDeliveryLocation child_sdl = new ServiceDeliveryLocation();

		child_sdl.setCode(childNodeCode);
		child_sdl.setClassCode(cvSdloc);  //for each sdl, created with hbs manager, set is classcode as SDLOC
		
		EN enToSet = new EN();
		enToSet.setGiv(node_name);	
		child_sdl.setName(enToSet);

		child_sdl.setParent(parent_sdl);
		parent_sdl.addChildren(child_sdl);
		
		//create SDL
		ca.create(child_sdl);

		ca.flushSession();
		
		//reset node type
		setNode_code(null);
		
		return buildTreeNode(child_sdl, null);
		
	}
	
	/**
	 * delete() removes selected RimObject
	 * This method is called from a4j:jsFunction name="deleteNodeObject" 
	 */
	public void delete() throws ApplicationException, PersistenceException {
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		
		ServiceDeliveryLocation node_sdl = null;
		try {
			node_sdl = ca.get(ServiceDeliveryLocation.class, id_node);

			node_sdl.getParent().getChildren().remove(node_sdl);
			
			ca.delete(node_sdl);
			
			ca.flushSession();
		} catch (Exception e) {
			if (e.getCause().getCause() instanceof ConstraintViolationException) {
				log.error("Error Deleting " + node_sdl.getName().getGiv() + " structure already used or with childs", e);
				throw new ApplicationException("Error Deleting " + node_sdl.getName().getGiv() + " structure already used or with childs", e, ErrorConstants.PERSISTENCE_INTEGRITY_CONSRAINT_VIOLATION_DELETE_CODE);
			} else {
				log.error("Error Deleting " + node_sdl.getName().getGiv(), e);
				throw new ApplicationException(e, ErrorConstants.PERSISTENCE_INTEGRITY_CONSRAINT_VIOLATION_DELETE_CODE);
			}
		}
	}
	
	/**
	 * renames selected RimObject
	 * This method is called from a4j:jsFunction name="renameNodeObject" 
	 * @throws PersistenceException 
	 */
	public void rename() throws PersistenceException {

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		
		ServiceDeliveryLocation sdl = ca.get(ServiceDeliveryLocation.class, id_node);

		if (sdl.getName() == null) {
			EN enToSet = new EN();
			enToSet.setGiv(node_name);	
			sdl.setName(enToSet);
		} else {
			sdl.getName().setGiv(node_name);
		}
		
		//if (!(sdl.getCode().getOid().equals(SDLOC_ROOT_OID))) {
		if (!(sdl.getCode().getCode().equals(SDLOC_ROOT_CODE))) {
			ca.create(sdl);
			ca.flushSession();
		}
		
}

	/**
	 * renames selected RimObject
	 * This method is called from a4j:jsFunction name="disableNodeObject" 
	 * @throws PersistenceException 
	 */
	public void disable() throws PersistenceException {

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		
		ServiceDeliveryLocation sdl = ca.get(ServiceDeliveryLocation.class, id_node);

		//if (!(sdl.getCode().getOid().equals(SDLOC_ROOT_OID))) {
		if (!(sdl.getCode().getCode().equals(SDLOC_ROOT_CODE))) {
			sdl.setIsActive(false);

			ca.create(sdl);
			ca.flushSession();
		}

	}

	/**
	 * renames selected RimObject
	 * This method is called from a4j:jsFunction name="disableNodeObject" 
	 * @throws PersistenceException 
	 */
	public void reEnable() throws PersistenceException {

		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		
		ServiceDeliveryLocation sdl = ca.get(ServiceDeliveryLocation.class, id_node);

		//if (!(sdl.getCode().getOid().equals(SDLOC_ROOT_OID))) {
		if (!(sdl.getCode().getCode().equals(SDLOC_ROOT_CODE))) {

			sdl.setIsActive(true);

			ca.create(sdl);
			ca.flushSession();
		}
	}

	

	
	private void loadHBSdomains(){
		if (!readed_SDLOC_Code) { 
			RepositoryManager rm = RepositoryManager.instance();
			String rootDomain = rm.getSeamProperty("SDLOC_ROOT_DOMAIN");
			if (rootDomain!=null && !rootDomain.isEmpty())
				SDLOC_ROOT_DOMAIN=rootDomain;
			
			String rootCode = rm.getSeamProperty("SDLOC_ROOT_CODE");
			if (rootCode != null && !rootCode.isEmpty())
				SDLOC_ROOT_CODE = rootCode;
			readed_SDLOC_Code=true;
		}
//		
//		String classCode = rm.getSeamProperty("SDLOC_CLASS_CODE");
//		if (classCode != null && !classCode.isEmpty())
//			SDLOC_CLASS_CODE = classCode;
	}
}