package com.phi.cs.catalog.adapter.sequence;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Query;
import javax.sql.DataSource;

import org.hibernate.LockMode;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.entities.SequenceConfiguration;
import com.phi.entities.SequenceValue;
import com.phi.entities.SequenceValueId;
import com.phi.security.UserBean;

/**
 * Manages sequence numbers. Three types of sequences: simple, sequence, complex,
 * 
 *   Simple sequence: numeric dense sequence.
 * 
 *   Oracle sequence: Sequence value obtained in oracle executing SELECT name.NEXTVAL from Dual
 * 
 *   Complex sequence: A dense sequence that depends on a time base and/or an owner
 * 
 * Based on SEQUENCE_CONFIGURATION and SEQUENCE_VALUE table.
 * 
 * SEQUENCE_CONFIGURATION table must me manually filled to define a sequence. Fields: 
 *   name - name of the sequence
 *   type - type can be simple, sequence, complex
 *   description - description of the sequence
 *   timeBase - can be no, daily, monthly, yearly
 *   timeBasePattern - date pattern for timeBased sequences
 *   	examples: 	yyyy'/#{000000}'	: 2014/000001, 2014/000002, ...
 *   	 			'B#{value}'			: B1, B1, ...
 *   ownerDependant - boolean, if true the sequence value query will search for a sequence that depends on:
 *		- current owners of the logged user if ownerId parameter is set to null
 *		- a custom owner if ownerId parameter is set to a value
 *   
 * SEQUENCE_VALUE table, autofilled when a sequence value is requested
 *   name - name of the sequence
 *   date - can be notDependant or for a daily seq is the date in format: DD/MM/YYYY or for a monthly seq is the date in format: MM/YYYY  or for a yearly seq is the date in format: YYYY
 *   owner - can be all or id of an owner
 *   value - last value of the sequence
 * 
 * @author Zupan
 */
@BypassInterceptors
@Name("SequenceManager")
@Scope(ScopeType.EVENT)
public class SequenceManager {

	protected enum SequenceType {simple, sequence, complex};
	
	protected enum TimeBaseType {no, daily, monthly, yearly};
	
	protected static final String allOwners = "all";
	
    private String name;
    private String ownerId;
    
    /**
     * Return next value of a sequence called name.
     * 
     * Based on sequence type calls: nextOfSimple, nextOfSequence or nextOfComplex
     * 
     * @param name
     * @param safe mode with flush
     * @return
     */
    public String nextOf(String name, boolean safeMode){
    	return nextOf(name, null, safeMode);
    }
    
    /**
     * Return next value of a sequence called name.
     * 
     * Based on sequence type calls: nextOfSimple, nextOfSequence or nextOfComplex
     *  
     * @param name
     * @return
     */
    public String nextOf(String name) {
        return nextOf(name, null, true);
    }
    
    /**
     * Return next value of a sequence called name.
     * 
     * Based on sequence type calls: nextOfSimple, nextOfSequence or nextOfComplex
     *  
     * @param name
     * @param customDate
     * @return
     */
    public String nextOf(String name, String customDate) {
    	return nextOf(name, customDate, true);
    }

    /**
     * Return next value of a sequence called name.
     * 
     * Based on sequence type calls: nextOfSimple, nextOfSequence or nextOfComplex
     *  
     * @param name
     * @param customDate
     * @return
     */
    public String nextOf(String name, String customDate, boolean safeMode) {
        
    	SequenceConfiguration seqConf = loadSeqConfig(name);

        if (SequenceType.simple.name().equals(seqConf.getType())) {
            return String.valueOf(nextOfSimple(seqConf, safeMode));
        } else if (SequenceType.sequence.name().equals(seqConf.getType())) {
            return String.valueOf(nextOfSequence(seqConf.getName()));
        } else if (SequenceType.complex.name().equals(seqConf.getType())) {
            return nextOfComplex(seqConf, customDate);
        }

        throw new IllegalArgumentException("Wrong type of sequence " + name + "Valid types: " + SequenceType.values());
    }
    
    private SequenceConfiguration loadSeqConfig(String name) {
    	SequenceConfiguration seqConf;
    	
        try {
            CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();

            seqConf = ca.get(SequenceConfiguration.class, name);
            
            if (seqConf == null) {
            	throw new IllegalArgumentException("Sequence " + name + " doesn't exsist!"); 
            }
        	
        } catch (Exception e) {
            throw new IllegalArgumentException("Sequence " + name + " doesn't exsist!", e);
        }
        return seqConf;
    }

    /**
     * Return next value of a numeric dense sequence with name name.
     * Uses LockManager to lock the sequence till next flush.
     * 
     * @param name
     * @return
     */
	protected String nextOfSimple(SequenceConfiguration seqConf, boolean safeMode) {
		try {
			
			String name = seqConf.getName();

			SequenceValueId id = new SequenceValueId(name, TimeBaseType.no.name(), allOwners);

			if (safeMode) {  //flush immediately

				if (Boolean.TRUE.equals(seqConf.getCallProcedure())) {
					// USA PROCEDURA ESTERNA IN DB: GET_NEXT_SEQ_VALUE
					return String.valueOf(nextOfByProc(id.getName(), id.getDate(), id.getOwner()));
				} else {

					GenericAdapterLocalInterface ga = GenericAdapter.instance();

					SequenceValue seqValue = (SequenceValue) ga.get(
							SequenceValue.class, id, LockMode.UPGRADE);

					if (seqValue == null) {
						seqValue = new SequenceValue();
						seqValue.setId(id);
						seqValue.setValue(0L);
					}

					seqValue.setValue(seqValue.getValue() + 1);
					ga.create(seqValue);
					//ga.flushSession(); //ga.flushSession() is already invoked inside ga.create in a NEW transaction
					return String.valueOf(seqValue.getValue());
				}
			} else {
				//no flush
				CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();

				SequenceValue seqValue = (SequenceValue)ca.get(SequenceValue.class, id, CatalogAdapter.lockType.UPDATE);

				if (seqValue == null) {
					seqValue = new SequenceValue();
					seqValue.setId(id);
					seqValue.setValue(0L);

					ca.create(seqValue);
				}

				seqValue.setValue(seqValue.getValue() + 1);

				return String.valueOf(seqValue.getValue());
			}

		} catch (Exception e) {
			throw new IllegalArgumentException("Sequence " + name
					+ " doesn't exsist!", e);
		}

	}


    /**
     * Return next value of a Oracle sequence: 
     * Sequence value obtained in oracle executing SELECT name.NEXTVAL from Dual
     *
     * @param nome
     * @return
     */
    protected long nextOfSequence(String name) {
    	
    	BigDecimal seqVal = null;
    	
        try {
        	
        	GenericAdapterLocalInterface ga = GenericAdapter.instance();

        	Query qry = ga.createNativeQuery("SELECT " + name + ".NEXTVAL from Dual");
        	@SuppressWarnings("rawtypes")
			List seq = qry.getResultList();
        	
        	if (!seq.isEmpty())
        		seqVal = (BigDecimal)seq.get(0);
        	
        } catch (Exception e) {
            throw new IllegalArgumentException("Sequence " + name + " doesn't exsist!", e);
        }

        return seqVal.longValue();
    }
    
    
    /**
     * Return next value of a dense sequence that depends on a time base and/or an owner
     * Uses LockManager to lock the sequence till next flush.
     *
     * @param seqConf
     * @return
     */
    protected String nextOfComplex(SequenceConfiguration seqConf) {
    	return nextOfComplex(seqConf,null);
    }


    /**
     * Return next value of a dense sequence that depends on a time base and/or an owner
     * Uses LockManager to lock the sequence till next flush.
     *
     * @param seqConf
     * @return
     */
    protected String nextOfComplex(SequenceConfiguration seqConf, String customYear) {
        try {
        	UserBean usr = UserBean.instance();
              	
        	GenericAdapterLocalInterface ga = GenericAdapter.instance();
        	
        	SequenceValueId id = new SequenceValueId();
        	
        	id.setName(seqConf.getName());

        	Calendar now = Calendar.getInstance();
        	
        	int date = now.get(Calendar.DATE);
        	int month = now.get(Calendar.MONTH) + 1;
        	int year = now.get(Calendar.YEAR);
        	
        	//if user defined a custom year for the sequence:
        	if (customYear != null && !customYear.equalsIgnoreCase("")) {
        		year = Integer.parseInt(customYear);
        		now.set(Calendar.YEAR, year);
        	}
        			
	    	if (seqConf.getTimeBase().equals(TimeBaseType.no.name())) {
	    		
	    		id.setDate(TimeBaseType.no.name());
	    		
	    	} else if (seqConf.getTimeBase().equals(TimeBaseType.daily.name())) {
	    		
	    		id.setDate(date + "/" + month + "/" + year);
	    		
    		} else if (seqConf.getTimeBase().equals(TimeBaseType.monthly.name())) {
	    		
    			id.setDate(month + "/" + year);
	    		
    		} else if (seqConf.getTimeBase().equals(TimeBaseType.yearly.name())) {
	    		
	    		id.setDate(String.valueOf(year));
	    		
	    	} else {
	    		throw new IllegalArgumentException("Wrong timeBase type of sequence " + seqConf.getName() + "Valid types: " + TimeBaseType.values());
	    	}
	    	
	    	if (seqConf.getOwnerDependant()) {
	    		if (ownerId != null) {
	    			
	    			id.setOwner(ownerId);
	    			
	    		} else {
		    		List<Long> owners = usr.getSdLocs();
		    		
		    		if (owners.size() != 1) {
		    			throw new IllegalArgumentException("User associted to multiple owners, cannot find an unique sequence for owners with id : " + owners);
		    		}
	    		
	    		id.setOwner(String.valueOf(owners.get(0)));
	    		//  Multiple owner implementation
//	    		String hql = "select seq from SequenceValue seq where seq.id.name = :name and seq.id date= :date and seq.id.owner in (";
//	    		
//	    		for (int z=0; z<owners.size(); z++) {
//	    			hql += (z == 0 ? "" : ", ") + owners.get(z);
//	    		}
//	    		hql += ")";
//
//	    		HashMap<String,Object> pars = new HashMap<String, Object>();
//	    		pars.put("name", id.getName());
//	    		pars.put("date", id.getDate());
//	    		
//	    		List<SequenceValue> lstSeq = ca.executeHQLwithParameters(hql, pars);
//	    		
//	    		if (lstSeq.size() != 1) {
//	    			throw new IllegalArgumentException("Unable to find a unique sequence " + seqConf.getName() + " for owners: " + owners);
//	    		}
//	    		
//	    		seqValue = lstSeq.get(0);
	    		}
	    	} else {
	    		id.setOwner(allOwners);
	    	}
	    	
	    	long seqValue = -1l;
			if (Boolean.TRUE.equals(seqConf.getCallProcedure())) {
		    	seqValue = nextOfByProc(id.getName(), id.getDate(), id.getOwner());
			} else {
				SequenceValue sValue =  (SequenceValue)ga.get(SequenceValue.class, id, LockMode.UPGRADE);

				if (sValue == null) {
					sValue = new SequenceValue();
					sValue.setId(id);
					sValue.setValue(0L);
				}

				sValue.setValue(sValue.getValue() + 1);
				ga.create(sValue);
				//ga.flushSession(); //ga.flushSession() is already invoked inside ga.create in a NEW transaction
				
				seqValue = sValue.getValue();
			}
	    	
	        if (seqConf.getTimeBase().equals(TimeBaseType.no.name())) {
	        	return String.valueOf(seqValue);
	        } else {
	        	
	        	if (seqConf.getTimeBasePattern() != null &&  !seqConf.getTimeBasePattern().equalsIgnoreCase("")) {
	        		
	        		String timeBasePattern = seqConf.getTimeBasePattern();
	        		boolean timeBasePatternContainsValue = false;
	        		
	        		Pattern valuePtrn = Pattern.compile("(.*)\\#\\{([^\\}]*)\\}(.*)");
	        		Matcher m = valuePtrn.matcher(timeBasePattern);
	        		if (m.matches()) {
	        			String valueOrPattern = m.group(2);
	        			if (valueOrPattern.equals("value")) {
	        				timeBasePattern = timeBasePattern.replace("#{value}", String.valueOf(seqValue));
	        			} else {
	        				DecimalFormat myFormatter = new DecimalFormat(valueOrPattern);
	        			    String output = myFormatter.format(seqValue);
	        			    timeBasePattern = m.group(1) + output + m.group(3);
	        			}
	        			timeBasePatternContainsValue = true;
	        		}

	        		try {
						SimpleDateFormat sdf = new SimpleDateFormat(timeBasePattern);
						String value = sdf.format(now.getTime());
						if (timeBasePatternContainsValue) {
							return value;
						} else {
							return value + "/" + seqValue;
						}
					} catch (Exception e) {
						throw new IllegalArgumentException("Unable to format date based on pattern " + seqConf.getTimeBasePattern(), e);
					}
	        	} else {
	        		return String.valueOf(seqValue); //was: return id.getDate() + "/" + seqValue.getValue();
	        	}
	        }
	    	
        } catch (Exception e) {
        	throw new IllegalArgumentException("Sequence " + seqConf.getName() + " doesn't exsist!", e);
        }
    }

    public SequenceConfiguration newSequenceConfiguration() {
    	return new SequenceConfiguration();
    }
    
    //Used by admin console to configure sequences
    @SuppressWarnings("unchecked")
	@Factory(scope=ScopeType.CONVERSATION)
    public List<SequenceConfiguration> getSequenceConfigurations() throws PhiException {
        try {
        	CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
        	return ca.executeHQL("FROM SequenceConfiguration");
	    } catch (Exception e) {
	    	throw new PhiException("Unable to load  Sequence Configurations", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
	    }
    }
    
    @SuppressWarnings("unchecked")
	@Factory(scope=ScopeType.CONVERSATION)
    public List<SequenceValue> getSequenceValues() throws PhiException {
        try {
        	CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
        	return ca.executeHQL("FROM SequenceValue");
	    } catch (Exception e) {
	    	throw new PhiException("Unable to load  Sequence Values", e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
	    }
    }
    
    public void save(Object entity) throws PhiException {
        try {
        	CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
        	ca.create(entity);
        	ca.flushSession();
	    } catch (Exception e) {
	    	throw new PhiException("Unable to save " + entity, e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
	    }
    }
    
    public void unlockSequence(String name) throws PhiException {
    	try {
    		CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
    		
    		ca.flushSession();
			
		} catch (Exception e) {
	    	throw new PhiException("Unable to unlock " + name, e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);

		}
    }
    
    //Used by a4jFunction
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
    public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerSource) {
		this.ownerId = ownerSource;
	}	
	
	public String nextOf() {
		return nextOf(name);
	}
	
	protected long nextOfByProc(String name, String date, String owner) throws Exception {

		DataSource dataSource = null;
		Connection conn = null;
		CallableStatement cstmt = null;

		try {
			javax.naming.Context jndiContext = new javax.naming.InitialContext(); // Lazy Initialization
			dataSource = (DataSource) jndiContext.lookup("java:/rimDatasourcePDM2");

			conn = dataSource.getConnection();

			cstmt = conn.prepareCall("BEGIN GET_NEXT_SEQ_VALUE(:pDate, :pName, :pOwner, :newValue); END;");

			cstmt.setString("pDate", date);
			cstmt.setString("pName", name);
			cstmt.setString("pOwner", owner);

			cstmt.registerOutParameter("newValue", java.sql.Types.BIGINT);

			cstmt.execute();

			return cstmt.getLong("newValue");
		} catch (Exception e) {
			throw new Exception("ERROR INTERACTING WITH DATABASE", e);
		} finally {
			try {
				if (cstmt!=null){
					cstmt.close();
				}
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				System.out.println("Problemi durante la chiusura della connessione");
			}
		}
	}
}
