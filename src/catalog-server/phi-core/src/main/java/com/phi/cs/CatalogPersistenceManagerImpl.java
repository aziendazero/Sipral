package com.phi.cs;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.RimPdm2CatalogAdapter;
import com.phi.cs.exception.ApplicationException;

/**
 * This class manages the CatalogAdapater loading and interaction with JavaSig to get an instance of Rim Object starting from the mif name
 * 
 * @see com.phi.cs.CatalogPersistenceManager
 */
public class CatalogPersistenceManagerImpl {
	
	public static CatalogAdapter instance() {
		return (CatalogAdapter)Component.getInstance(RimPdm2CatalogAdapter.class, ScopeType.CONVERSATION);
	}
	
	public static CatalogAdapter getAdapter() throws ApplicationException {
		return (CatalogAdapter)Component.getInstance(RimPdm2CatalogAdapter.class, ScopeType.CONVERSATION);
	}
	
	public static CatalogAdapter getAdapter(String adapterName) throws ApplicationException {
		return (CatalogAdapter)Component.getInstance(adapterName, ScopeType.CONVERSATION);
	}
}
