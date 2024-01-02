package com.phi.ps;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.security.UserBean;


/**
 * License Agreement.	
 *
 *  JBoss RichFaces - Ajax4jsf Component Library
 *
 * Copyright (C) 2007  Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

/**
 * Class for creating a tree structure of process repository.
 * 
 * @author rossi
 */
@BypassInterceptors
@Name("treeBean")
@Scope(ScopeType.SESSION)
@AutoCreate
public class TreeBean implements Serializable{
	
	private static final long serialVersionUID = -6822599285881529545L;

	private static final Logger log = Logger.getLogger(TreeBean.class);


	private Node rootNodeCache = null;
	private HashMap<String, Node> nodesCache = new HashMap<String, Node>();

	private Identity identity = Identity.instance();
	public FunctionsBean functions = FunctionsBean.instance();
	
    private List<String> processPath;
    
    private UserBean ub = UserBean.instance();

    @Create
    public void init() throws PhiException{
    	Node rootNode = RepositoryManager.instance().getRootProcessNode();
    	rootNodeCache= creteFilteredClonedNode(rootNode);
		
		//add node to nodesCache which can be always executable, without be visible.
    	updateAlwaysExecutableNode(rootNode);
    	
    }

	public List<String> getProcessPath() {
        return processPath;
    }
    
    public void setProcessPath(List<String> processPath) {
        this.processPath = processPath;
    }
	
    @Factory
    public List<Node> getProcessList() {
    	if (rootNodeCache == null) {
    		log.error("Error retrieving process List.");
    		return null;
    	}
    	return rootNodeCache.getChildren();
    }
    
    
    //used by change role process to reload completely the process list.
    public void resetProcessList(List<Node> l) {
    	Contexts.getSessionContext().set("processList", l);
    }
    
 	//clone the input node, and return the clone.
	//during cloning, for each child of input node, the function is called again.
	private Node creteFilteredClonedNode (Node node) {
    	Node filteredCloneNode = new Node(); 
    	
    	filteredCloneNode.setProcessDefinition(node.getProcessDefinition());
    	filteredCloneNode.setPath(node.getPath());
//    	filteredCloneNode.setTitle(messages.get(node.getPath()));
//    	filteredCloneNode.setTitle(node.getPath());
    	filteredCloneNode.setTitle(functions.getTranslation(node.getPath()));

    	
    	filteredCloneNode.setLeaf(node.isLeaf());
    	filteredCloneNode.setImagePath(node.getImagePath());
    	filteredCloneNode.setSortOrder(node.getSortOrder());

    	for (Node childNode : node.getChildren()) {

    		//for each child node, the node must be cloned (To be visibile in the filtered node three)
    		//if the identity give the proper security right
    		ProcessSecurityRole role = nodeToBeCloned(childNode,identity);
    		if (role != null) {
    			
				//Foreach children (childNode, calculate recursively the tree)
				Node cloneChild = creteFilteredClonedNode(childNode);
				
				cloneChild.setReadOnly(role.getReadonly());
				
				//add parent to clonedChild the filteredCloneNode
				cloneChild.setParent(filteredCloneNode);
				
				//calculate the process path (only here the parent relationship is available)
				cloneChild.setProcessPath(calculatePath(childNode));
				
				//before attach the cloneChild as child of its parent filteredCloneNode, check that filteredCloneNode has not already a child with the same name.
				//this allow to have at the same level different sub folder at design time, but aggregated with the same title at design time.
//				Node existing = getChildrenNodeWithTitle(filteredCloneNode, messages.get(childNode.getPath()));
				Node existing = getChildrenNodeWithTitle(filteredCloneNode, functions.getTranslation(childNode.getPath()));
				if (existing == null) {
					filteredCloneNode.getChildren().add(cloneChild);
				}
				else {
					//cloneChild is at the same level of existing node, so take the child of cloneChild, and add them to existing node. 
					existing.getChildren().addAll(cloneChild.getChildren());
					//because existing node childs were already ordered, need to order them again after this addition
					sort(existing.getChildren());	
				}

				//add the node to the cache.
				nodesCache.put(childNode.getPath(), cloneChild);
			}

		}
    	
    	//reorder children, if any.
    	sort(filteredCloneNode.getChildren());	
    	return filteredCloneNode;
    	
    }
	
	private void updateAlwaysExecutableNode (Node node) {
		if (node.isAlways_executable() && !nodesCache.containsKey(node.getPath())) {
			Node cloneChild = new Node();
			cloneChild.setProcessPath(calculatePath(node));
			cloneChild.setProcessDefinition(node.getProcessDefinition());
//			cloneChild.setTitle(messages.get(node.getPath()));
//			cloneChild.setTitle(node.getPath());
			
			cloneChild.setLeaf(node.isLeaf());
			
			//not needed for always executable process node
//			cloneChild.setImagePath(node.getImagePath());
//			cloneChild.setSortOrder(node.getSortOrder());
			
			nodesCache.put(node.getPath(), cloneChild);
			
		}
		
		for (Node n : node.getChildren()) {
    		updateAlwaysExecutableNode(n); 
    	}
	}
    
    private Node getChildrenNodeWithTitle(Node node, String title) {

    	for (Node children : node.getChildren()) {
    		if (functions.getTranslation(children.getPath()) != null && functions.getTranslation(children.getPath()).equals(title))
    			return children;
    	}
    	
    	return null;
     }
    
    
    private ProcessSecurityRole nodeToBeCloned (Node node, Identity id) {
    	//a node must be cloned, if it's a leaf Node, with an allowed role,
    	//or if is a not leaf node, containing (recursively) a node which must be cloned.
    	//log.debug("NodeToBeCloned : "+node.getProcessDefinitionName()+" identity: "+id.getUsername()+" is leaf? "+node.isLeaf()+" roles: "+Arrays.toString(node.getRoles()));
    	if (node.isLeaf() ) {
			if (node.getRoles() != null) {
				for (ProcessSecurityRole role : node.getRoles()) {
					if (id.hasRole(role.getRole()) || ("GUEST".equals(role) && !id.isLoggedIn())) {
						log.debug("NodeToBeCloned "+node.getPath()+": contains valid Role"+role+" for identity: "+id.getUsername());
//						if (node.getSdlocs() == null || node.getSdlocs().isEmpty()) {
							return role;
//						} else {
//							if (!Collections.disjoint(node.getSdlocs(), ub.getSdLocs())) {
//								return role;
//							}
//							
//						}
					}
				}
			}
			
			if (log.isDebugEnabled()){
				String debugRoles="";
				for ( Group sg : id.getSubject().getPrincipals(Group.class) ){
					if ( "Roles".equals( sg.getName() ) ) {
							Enumeration  en =sg.members();
							while ( en.hasMoreElements()) {
								debugRoles+=((Principal)en.nextElement()).getName()+",";
							}
							if (debugRoles.endsWith(","))
								debugRoles = debugRoles.substring(0,debugRoles.length()-1);
					}
				}
				log.debug("Node "+node.getPath()+" with roles ["+node.getRoles()+"]: not cloned for identity: "+id.getUsername() + " with roles: ["+debugRoles+"]");
			}
			
			return null;
		}
    	
    	for (Node n : node.getChildren()) {
    		ProcessSecurityRole role = nodeToBeCloned(n, id);
    		if (role != null) 
    			return role;
    	}
    	
    	return null;
    }
    
    
    private LinkedList<String> calculatePath(Node node) {
    	//starting from a node, calulate its path, searching its parents.
//    	String title = messages.get(node.getPath());bundle.getString
    	String title = node.getPath();
    	LinkedList<String> ret = new LinkedList<String>();
    	if (node.getParent() != null) {
    		ret = calculatePath(node.getParent());
    	}
    	if (title != null && !title.isEmpty())
    		ret.addLast(title);
    	return ret;
    }
    
    
    
    

    public void sort(List<Node> res) {
		Collections.sort(res, new ProcessComparator());
	}
	
	
    public static TreeBean instance() {
        return (TreeBean) Component.getInstance(TreeBean.class, ScopeType.SESSION);
    }
    
    
    
	public HashMap<String, Node> getNodesCache() {
		return nodesCache;
	}
	
	public boolean isEnabled(String processDefinitionName) {
		if (nodesCache.get(processDefinitionName) == null) {
			return false;
		}
		return true;
	}
}

class ProcessComparator implements Comparator<Node> {

	FunctionsBean f = FunctionsBean.instance();
	
	@Override
	public int compare(Node n1, Node n2) {
		
		
		if (n1.getSortOrder() != null) {
			if (n2.getSortOrder() != null) {
				return n1.getSortOrder().compareTo(n2.getSortOrder());
			} else {
				return 1;
			}
		} else {
			if (n2.getSortOrder() != null) {
				return -1;
			} else {
				return f.getTranslation(n1.getPath()).compareToIgnoreCase(f.getTranslation(n2.getPath()));
			}
		}
	}
	
}