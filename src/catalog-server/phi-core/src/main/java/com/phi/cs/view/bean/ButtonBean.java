package com.phi.cs.view.bean;

import java.io.Serializable;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("button")
@Scope(ScopeType.EVENT)
public class ButtonBean implements Serializable {

	private static final long serialVersionUID = 6821194578158706235L;

	public String value = null;
	public String mnemonic = null;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

    public static ButtonBean instance() {
        return (ButtonBean) Component.getInstance(ButtonBean.class, ScopeType.EVENT);
    }
}