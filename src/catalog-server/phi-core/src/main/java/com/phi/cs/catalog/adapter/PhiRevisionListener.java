 package com.phi.cs.catalog.adapter;

import org.hibernate.envers.RevisionListener;

import com.phi.entities.PhiRevisionEntity;
import com.phi.security.UserBean;

public class PhiRevisionListener implements RevisionListener {

	private String tag;

	public void newRevision(Object revisionEntity) {
		
		PhiRevisionEntity phiRevisionEntity = (PhiRevisionEntity) revisionEntity;
		try{
			phiRevisionEntity.setUsername(UserBean.instance().getUsername());//identity.getUsername is deprecated
		
		} catch (IllegalStateException e) {
			phiRevisionEntity.setUsername("phi-esb");
		}
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}