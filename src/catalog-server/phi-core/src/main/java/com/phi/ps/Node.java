package com.phi.ps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * @author Tomaz Cerar
 * @created 23.9.2010 1:13:42
 */
public class Node implements Serializable{

	private static final long serialVersionUID = 8061951736046389639L;
	
	private String title;
    private List<Node> children = new ArrayList<Node>();
    private Node parent;
    private String path; //path of node on filesystem
    private String imagePath; //path of incon on filesystem
	private LinkedList<String> processPath = new LinkedList<String>(); //bread crumb
	private ProcessDefinition processDefinition;
	private List<ProcessSecurityRole> roles;
//	private List<Long> sdlocs;
	private Integer sortOrder;
	private boolean alwaysExecutable=false;
	private boolean readOnly=false;


	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}


    
    private boolean isLeaf = false;

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonManagedReference
    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    @JsonBackReference
    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    
	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public LinkedList<String> getProcessPath() {
		return processPath;
	}

	public void setProcessPath(LinkedList<String> processPath) {
		this.processPath = processPath;
	}

	@JsonIgnore
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	
	@JsonIgnore
	public List<ProcessSecurityRole> getRoles() {
		return roles;
	}

	public void setRoles(List<ProcessSecurityRole> roles) {
		this.roles = roles;
	}

//	@JsonIgnore
//	public List<Long> getSdlocs() {
//		return sdlocs;
//	}
//
//	public void setSdlocs(List<Long> sdlocs) {
//		this.sdlocs = sdlocs;
//	}

	@Override
	 public String toString() {
		if (processDefinition != null) {
			return processDefinition.getName() + " enabledRoles: " + roles;
		} else {
			return title + " enabledRoles: " + roles;
		}
	 }	

	public boolean isAlways_executable() {
		return alwaysExecutable;
	}

	public void setAlways_executable(boolean always_executable) {
		this.alwaysExecutable = always_executable;
	}

	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
}