package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class IIEmbeddable extends ANY implements Serializable, II {
	
	private static final long serialVersionUID = 6538870458945649034L;
	
	private String root;
	private String extension;

	public IIEmbeddable() {
	}

	@Override
	@Column(name="root")
	public String getRoot() {
		return root;
	}

	@Override
	public void setRoot(String root) {
		this.root = root;
	}

	@Override
	@Column(name="extension")
	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	@Override
	public String toString() {
		return extension + "@" + root;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IIEmbeddable other = (IIEmbeddable) obj;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		return true;
	}
}