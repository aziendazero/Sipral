package com.phi.rest.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 * jsTree datamodel
 * See http://www.jstree.com/
 * 
 * @author Alex
 */
public class TreeDatamodel {
	public String data; // label
	public Attribute attr = new Attribute(); //attributes
	public String state; // open / closed
	public List<TreeDatamodel> children = new ArrayList<TreeDatamodel>();
	
	public class Attribute {
		public String id; // id
		public String rel; // rel
	}
}
