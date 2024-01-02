package com.phi.rest.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Java bean sended to jsTree to populate hbs tree via AjaxServlet
 * model for 3.2.1
 */
public class TreeNodeObject implements Serializable {
	

	private static final long serialVersionUID = 489832940948489904L;
	private String text;
	private String id;
	private String type;
	private Map<String, Boolean> state = new HashMap<String, Boolean>();
	//private List<TreeNodeObject> children = new ArrayList<TreeNodeObject>();
	private Object children;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getChildren() {
		return children;
	}

	public void setChildren(Object children) {
		this.children = children;
	}

	public Map<String, Boolean> getState() {
		return state;
	}

	public void setState(Map<String, Boolean> state) {
		this.state = state;
	}

}