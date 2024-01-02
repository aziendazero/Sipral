package com.phi.cs.datamodel;

import java.util.List;

import com.phi.cs.exception.PhiException;

public interface IdataModel<T> {
	
	public Object getFullList();
	
	public List<T> getList();
	
	public int size();
	
	public boolean isEmpty();
	
	public T injectEject() throws PhiException;

	public void setSelectedIndex(int selectedIndex);
	
	public void setMnemonicName(String mnemonicName);
}