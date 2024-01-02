package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.util.List;


/**
 * The persistent class for the AGENTIMATERIALI database table.
 * 
 */
@Entity
@NamedQuery(name="Agentimateriali.findAll", query="SELECT a FROM Agentimateriali a")
public class Agentimateriali implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idagente;
	private String agente;
	private String chkattivo;
	private String codice;
	private String descrizione;
	private String e1;
	private String e2;
	private String e3;
	private String e4;
	private String lavorazione;
	private Agentimateriali agentimateriali;
	private List<Agentimateriali> agentimaterialis;
	private List<InfortuniPrevnet> InfortuniPrevnets1;
	private List<InfortuniPrevnet> InfortuniPrevnets2;

	public Agentimateriali() {
	}


	@Id
	public long getIdagente() {
		return this.idagente;
	}

	public void setIdagente(long idagente) {
		this.idagente = idagente;
	}


	public String getAgente() {
		return this.agente;
	}

	public void setAgente(String agente) {
		this.agente = agente;
	}


	public String getChkattivo() {
		return this.chkattivo;
	}

	public void setChkattivo(String chkattivo) {
		this.chkattivo = chkattivo;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public String getE1() {
		return this.e1;
	}

	public void setE1(String e1) {
		this.e1 = e1;
	}


	public String getE2() {
		return this.e2;
	}

	public void setE2(String e2) {
		this.e2 = e2;
	}


	public String getE3() {
		return this.e3;
	}

	public void setE3(String e3) {
		this.e3 = e3;
	}


	public String getE4() {
		return this.e4;
	}

	public void setE4(String e4) {
		this.e4 = e4;
	}


	public String getLavorazione() {
		return this.lavorazione;
	}

	public void setLavorazione(String lavorazione) {
		this.lavorazione = lavorazione;
	}


	//bi-directional many-to-one association to Agentimateriali
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPADRE")
	public Agentimateriali getAgentimateriali() {
		return this.agentimateriali;
	}

	public void setAgentimateriali(Agentimateriali agentimateriali) {
		this.agentimateriali = agentimateriali;
	}


	//bi-directional many-to-one association to Agentimateriali
	@OneToMany(mappedBy="agentimateriali")
	public List<Agentimateriali> getAgentimaterialis() {
		return this.agentimaterialis;
	}

	public void setAgentimaterialis(List<Agentimateriali> agentimaterialis) {
		this.agentimaterialis = agentimaterialis;
	}

	public Agentimateriali addAgentimateriali(Agentimateriali agentimateriali) {
		getAgentimaterialis().add(agentimateriali);
		agentimateriali.setAgentimateriali(this);

		return agentimateriali;
	}

	public Agentimateriali removeAgentimateriali(Agentimateriali agentimateriali) {
		getAgentimaterialis().remove(agentimateriali);
		agentimateriali.setAgentimateriali(null);

		return agentimateriali;
	}


	//bi-directional many-to-one association to InfortuniPrevnet
	@OneToMany(mappedBy="agenteMateriale")
	public List<InfortuniPrevnet> getInfortuniPrevnets1() {
		return this.InfortuniPrevnets1;
	}

	public void setInfortuniPrevnets1(List<InfortuniPrevnet> InfortuniPrevnets1) {
		this.InfortuniPrevnets1 = InfortuniPrevnets1;
	}

	public InfortuniPrevnet addInfortuniPrevnets1(InfortuniPrevnet InfortuniPrevnets1) {
		getInfortuniPrevnets1().add(InfortuniPrevnets1);
		InfortuniPrevnets1.setAgenteMateriale(this);

		return InfortuniPrevnets1;
	}

	public InfortuniPrevnet removeInfortuniPrevnets1(InfortuniPrevnet InfortuniPrevnets1) {
		getInfortuniPrevnets1().remove(InfortuniPrevnets1);
		InfortuniPrevnets1.setAgenteMateriale(null);

		return InfortuniPrevnets1;
	}


	//bi-directional many-to-one association to InfortuniPrevnet
	@OneToMany(mappedBy="contattoAmbiente")
	public List<InfortuniPrevnet> getInfortuniPrevnets2() {
		return this.InfortuniPrevnets2;
	}

	public void setInfortuniPrevnets2(List<InfortuniPrevnet> InfortuniPrevnets2) {
		this.InfortuniPrevnets2 = InfortuniPrevnets2;
	}

	public InfortuniPrevnet addInfortuniPrevnets2(InfortuniPrevnet InfortuniPrevnets2) {
		getInfortuniPrevnets2().add(InfortuniPrevnets2);
		InfortuniPrevnets2.setContattoAmbiente(this);

		return InfortuniPrevnets2;
	}

	public InfortuniPrevnet removeInfortuniPrevnets2(InfortuniPrevnet InfortuniPrevnets2) {
		getInfortuniPrevnets2().remove(InfortuniPrevnets2);
		InfortuniPrevnets2.setContattoAmbiente(null);

		return InfortuniPrevnets2;
	}

}