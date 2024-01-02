package com.phi.entities.dataTypes;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

@MappedSuperclass
public class SC extends ANY {

	private static final long serialVersionUID = 2954678499877164868L;

	private long internalId;
	
	private String data;
	
	private CodeValuePhi code;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Sc_sequence")
	@SequenceGenerator(name = "Sc_sequence", sequenceName = "Sc_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	public String getData() {
		return data;
	}

	@Column(name="data")
	public void setData(String data) {
		this.data = data;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
//	@ForeignKey(name="FK_sc_codeValue")  //duplicate name if defined
//	@Index(name="IX_sc_codeValue") //duplicate name if defined
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return data + " " + code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		SC other = (SC) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

}