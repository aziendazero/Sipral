package com.phi.cs.catalog.adapter.resultTransformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Result transformar that transform each tuple into a nested map, containing properties.
 * Maps are nested in this way el binding for real class or nested maps is the same
 * 
 * Example of result:
 * {entityClass=com.phi.entities.role.Patient, internalId=1, name={fam=Test, giv=Patient}, birthPlace={cty=Trieste}, birthTime=2013-04-22 12:12:34.845}
 * 
 * @author alex.zupan
 */
public class AliasToEntityMapResultTransformer extends org.hibernate.transform.AliasToEntityMapResultTransformer {

	private static final Logger log = Logger.getLogger(AliasToEntityMapResultTransformer.class);
	
	private static final long serialVersionUID = -714240885010976118L;

	public static final String ENTITY_CLASS = "entityClass";

	@SuppressWarnings("rawtypes")
	private Class entityClass;
	
	@SuppressWarnings({ "rawtypes", "deprecation" })
	public AliasToEntityMapResultTransformer(Class entityClass) {
		if ( entityClass == null ) {
			throw new IllegalArgumentException( "resultClass cannot be null" );
		}
		this.entityClass = entityClass;
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object transformTuple(Object[] tuple, String[] aliases) {

		Map result = new HashMap(tuple.length);
		for ( int i=0; i<tuple.length; i++ ) {
			String alias = aliases[i];
			
			if (alias == null) {
				alias = Integer.toString(i);
				log.warn("Projection without alias, using column number!");
			}
			
			if (alias.endsWith("-SELECT")) {
				alias = alias.substring(0, alias.length()-7);
			}
			
			if ( alias!=null ) {
				Map currRes = result;
				String[] aliasParts = alias.split("\\.");
				if (aliasParts.length != 0) {
					for (int z = 0; z<aliasParts.length; z++) {
						String aliasPart = aliasParts[z];
						boolean isLast = z == aliasParts.length-1;
						if (!isLast) {
							if (!currRes.containsKey(aliasPart)) { 
								//Create a new nested map
								currRes.put(aliasPart, new HashMap());
							}
							currRes = (Map)currRes.get(aliasPart);
						} else {
							currRes.put(aliasPart, tuple[i]);
						}
					}
				} else {
					result.put( alias, tuple[i] );
				}
			}
		}

		result.put(ENTITY_CLASS, entityClass.getName());
		return result;
	}
}