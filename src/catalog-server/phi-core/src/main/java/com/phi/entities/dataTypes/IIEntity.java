package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Version;

@MappedSuperclass
public abstract class IIEntity extends ANY implements Serializable, II {

	private static final long serialVersionUID = 6851133870887851455L;
	
	private long id;
	private long version;
	
	private String root;
	private String extension;

	public IIEntity() {
	}

	public IIEntity(String root, String extension) {
		this.root=root;
		this.extension=extension;
	}
	
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "id_sequence")
    @SequenceGenerator(name = "id_sequence", sequenceName = "id_sequence")
    @Column(name="id")
	public long getId() {
		return id;
	}

	public void setId(long value) { 
		id = value;
	}
	
    @Version
    @Column(name="version")
	public long getVersion() {
		return version; 
	}
	
	public void setVersion(long value) { 
		version = value; 
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
		IIEntity other = (IIEntity) obj;
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