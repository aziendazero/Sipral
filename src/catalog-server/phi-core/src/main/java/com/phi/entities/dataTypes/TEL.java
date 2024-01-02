package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.envers.Audited;

@Embeddable
@Audited
public class TEL extends ANY implements Serializable {

	private static final long serialVersionUID = 8090166382743611243L;

	private String as;
	private String bad;
	private String dir;
	private String ec;
	private String h;
	private String hp;
	private String hv;
	private String mail;
	private String mc;
	private String pg;
	private String pub;
	private String sip;
	private String tmp;
	private String wp;

	public String getAs() {
		return as;
	}

	public void setAs(String as) {
		this.as = as;
	}

	public String getBad() {
		return bad;
	}

	public void setBad(String bad) {
		this.bad = bad;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getEc() {
		return ec;
	}

	public void setEc(String ec) {
		this.ec = ec;
	}

	public String getH() {
		return h;
	}

	public void setH(String h) {
		this.h = h;
	}

	public String getHp() {
		return hp;
	}

	public void setHp(String hp) {
		this.hp = hp;
	}

	public String getHv() {
		return hv;
	}

	public void setHv(String hv) {
		this.hv = hv;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getMc() {
		return mc;
	}

	public void setMc(String mc) {
		this.mc = mc;
	}

	public String getPg() {
		return pg;
	}

	public void setPg(String pg) {
		this.pg = pg;
	}

	public String getPub() {
		return pub;
	}

	public void setPub(String pub) {
		this.pub = pub;
	}

	public String getSip() {
		return sip;
	}

	public void setSip(String sip) {
		this.sip = sip;
	}

	public String getTmp() {
		return tmp;
	}

	public void setTmp(String tmp) {
		this.tmp = tmp;
	}

	public String getWp() {
		return wp;
	}

	public void setWp(String wp) {
		this.wp = wp;
	}

	@Override
	public String toString() {
		 StringBuffer sb = new StringBuffer();

		 sb.append(as).append(" ");
		 sb.append(bad).append(" ");
		 sb.append(dir).append(" ");
		 sb.append(ec).append(" ");
		 sb.append(h).append(" ");
		 sb.append(hp).append(" ");
		 sb.append(hv).append(" ");
		 sb.append(mail).append(" ");
		 sb.append(mc).append(" ");
		 sb.append(pg).append(" ");
		 sb.append(pub).append(" ");
		 sb.append(sip).append(" ");
		 sb.append(tmp).append(" ");
		 sb.append(wp).append(" ");
		 
		 return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((as == null) ? 0 : as.hashCode());
		result = prime * result + ((bad == null) ? 0 : bad.hashCode());
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		result = prime * result + ((ec == null) ? 0 : ec.hashCode());
		result = prime * result + ((h == null) ? 0 : h.hashCode());
		result = prime * result + ((hp == null) ? 0 : hp.hashCode());
		result = prime * result + ((hv == null) ? 0 : hv.hashCode());
		result = prime * result + ((mail == null) ? 0 : mail.hashCode());
		result = prime * result + ((mc == null) ? 0 : mc.hashCode());
		result = prime * result + ((pg == null) ? 0 : pg.hashCode());
		result = prime * result + ((pub == null) ? 0 : pub.hashCode());
		result = prime * result + ((sip == null) ? 0 : sip.hashCode());
		result = prime * result + ((tmp == null) ? 0 : tmp.hashCode());
		result = prime * result + ((wp == null) ? 0 : wp.hashCode());
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
		TEL other = (TEL) obj;
		if (as == null) {
			if (other.as != null)
				return false;
		} else if (!as.equals(other.as))
			return false;
		if (bad == null) {
			if (other.bad != null)
				return false;
		} else if (!bad.equals(other.bad))
			return false;
		if (dir == null) {
			if (other.dir != null)
				return false;
		} else if (!dir.equals(other.dir))
			return false;
		if (ec == null) {
			if (other.ec != null)
				return false;
		} else if (!ec.equals(other.ec))
			return false;
		if (h == null) {
			if (other.h != null)
				return false;
		} else if (!h.equals(other.h))
			return false;
		if (hp == null) {
			if (other.hp != null)
				return false;
		} else if (!hp.equals(other.hp))
			return false;
		if (hv == null) {
			if (other.hv != null)
				return false;
		} else if (!hv.equals(other.hv))
			return false;
		if (mail == null) {
			if (other.mail != null)
				return false;
		} else if (!mail.equals(other.mail))
			return false;
		if (mc == null) {
			if (other.mc != null)
				return false;
		} else if (!mc.equals(other.mc))
			return false;
		if (pg == null) {
			if (other.pg != null)
				return false;
		} else if (!pg.equals(other.pg))
			return false;
		if (pub == null) {
			if (other.pub != null)
				return false;
		} else if (!pub.equals(other.pub))
			return false;
		if (sip == null) {
			if (other.sip != null)
				return false;
		} else if (!sip.equals(other.sip))
			return false;
		if (tmp == null) {
			if (other.tmp != null)
				return false;
		} else if (!tmp.equals(other.tmp))
			return false;
		if (wp == null) {
			if (other.wp != null)
				return false;
		} else if (!wp.equals(other.wp))
			return false;
		return true;
	}

	
	public TEL cloneTel(){
		TEL t = new TEL();
		
		t.as=this.as;
		t.bad=this.bad;
		t.dir=this.dir;
		t.ec=this.ec;
		t.h=this.h;
		t.hp=this.hp;
		t.hv=this.hv;
		t.mail=this.mail;
		t.mc=this.mc;
		t.pg=this.pg;
		t.pub=this.pub;
		t.sip=this.sip;
		t.tmp=this.tmp;
		t.wp=this.wp;
		return t;
	}
}
