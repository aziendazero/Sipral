package com.phi.json;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

public class JsonProxyGenerator extends ObjectIdGenerator<JsonProxy> {

	private static final long serialVersionUID = 1L;

	protected final Class<?> _scope;


 	protected JsonProxyGenerator() { 
 		this(null); 
 	}

    protected JsonProxyGenerator(Class<?> scope) {
        _scope = scope;
    }
    
    public final Class<?> getScope() {
      return this._scope;
    }
    
    public boolean canUseFor(ObjectIdGenerator<?> paramObjectIdGenerator)
    {
    	return (paramObjectIdGenerator.getClass() == getClass()) && (paramObjectIdGenerator.getScope() == this._scope);
    }
    
	@Override
	public ObjectIdGenerator<JsonProxy> forScope(Class<?> scope) {
        return (_scope == scope) ? this : new JsonProxyGenerator(scope);
	}

	@Override
	public ObjectIdGenerator<JsonProxy> newForSerialization(Object context) {
        return new JsonProxyGenerator(_scope);
	}

	@Override
	public IdKey key(Object key) {
        return new IdKey(getClass(), _scope, key);
	}
	
	@Override
	public JsonProxy generateId(Object obj) {
		long id = 0;
		try {
			id = (Long) obj.getClass().getMethod("getInternalId").invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String name = obj.getClass().getName();
		
		return new JsonProxy(id, name);
	}

}
