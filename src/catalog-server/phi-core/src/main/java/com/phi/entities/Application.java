package com.phi.entities;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.BaseEntity;

@javax.persistence.Entity
@Table(name = "application")
@Audited
public class Application extends BaseEntity {

	private static final long serialVersionUID = 1651977425L;

	
	
	private String application;
	private String url;
	private String server;
	private String langIt;
	private boolean main;
	
	@Column(name="application")
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	@Column(name="url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Column(name="server")
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	
	//methods needed for BaseEntity extension

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_Application_sdloc")
	@Index(name = "IX_Application_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Application_sequence")
	@SequenceGenerator(name = "Application_sequence", sequenceName = "Application_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	@Column(name="main")
	public boolean isMain() {
		return main;
	}
	public void setMain(boolean main) {
		this.main = main;
	}
	
	@Column(name="lang_it", length=255)
	public String getLangIt() {
		return this.langIt;
	}
	
	public void setLangIt(String translation) {
		this.langIt = translation;
	}

}