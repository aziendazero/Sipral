package com.phi.security;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Stateless
@BypassInterceptors
@Name("accessControl")
public class AccessControlAction extends BaseAccessControlAction implements AccessControl,Serializable {

	private static final long serialVersionUID = -3017710759585733038L;

	@PersistenceContext(unitName="rimEntityManagerPDM2")
	public void setEm(EntityManager em) {
		this.em = em;
	}
	
}