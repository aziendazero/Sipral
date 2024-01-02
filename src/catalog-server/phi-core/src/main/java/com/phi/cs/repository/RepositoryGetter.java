package com.phi.cs.repository;

public interface RepositoryGetter {

	public String getSeamProperty(String key);

	public String getSeamProperty(String key,String defaultValue);
}