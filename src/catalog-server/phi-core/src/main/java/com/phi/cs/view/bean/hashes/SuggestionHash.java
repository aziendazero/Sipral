package com.phi.cs.view.bean.hashes;

import java.io.Serializable;
import java.util.HashMap;

import com.phi.cs.view.bean.InputSuggestionBean;

/**
 * 
 * @author Francesco Bruni
 *
 */
public class SuggestionHash extends HashMap<Object, InputSuggestionBean> implements Serializable {

	private static final long serialVersionUID = 405013345360005064L;
//	public static final String NAME = "suggestions";

	@Override
	public InputSuggestionBean get(Object key) {
		InputSuggestionBean suggestion = super.get(key);
		if (suggestion == null) {
			suggestion = new InputSuggestionBean(this);
			super.put(key, suggestion);
		}
		return suggestion;
	}
}

