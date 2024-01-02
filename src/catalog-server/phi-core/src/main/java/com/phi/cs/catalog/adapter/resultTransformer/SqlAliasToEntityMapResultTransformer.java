package com.phi.cs.catalog.adapter.resultTransformer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Result transformar that transform each tuple into a map, containing properties.
 * To lower case and replace _ with .
 * 
 * @author alex.zupan
 */
public class SqlAliasToEntityMapResultTransformer extends org.hibernate.transform.AliasToEntityMapResultTransformer {

	private static final Logger log = Logger.getLogger(SqlAliasToEntityMapResultTransformer.class);
	
	private static final long serialVersionUID = -6231774724170308913L;
	
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
			
			alias = alias.toLowerCase();
			
			if (alias.contains("_")) {
				alias = alias.replace("_", ".");
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

		return result;
	}
}