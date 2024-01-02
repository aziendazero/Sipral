package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the SO_IMPATTO_MEDICI database table.
 * 
 */
@Entity
@Table(name="SO_IMPATTO_MEDICI")
public class SoImpattoMedici implements Serializable {
	private static final long serialVersionUID = 1L;
	private String acapo;
	private String ambito;
	private String asl;
	private String capdom;
	private String capres;
	private String categoria;
	private String codassaft;
	private String codicealternativo;
	private String codiceambulatorio1;
	private String codiceambulatorio2;
	private String codiceambulatorio3;
	private String codiceambulatorio4;
	private String codiceassociazione;
	private String codicefiscale;
	private String codiceregionale;
	private String cognome;
	private String comdom;
	private String comres;
	private String comunenascita;
	private String dataLaurea;
	private String dataassmedico;
	private String dataassmedicoaft;
	private String datadecesso;
	private String datafinerapporto;
	private String datainiziorapporto;
	private String dataiscralbo;
	private String datanascita;
	private String dataspec;
	private String datavaliditarecord;
	private String descambito;
	private String descdistretto;
	private String descrassaft;
	private String descrizioentelefono2;
	private String descrizioneassociazione;
	private String descrizionetelefono1;
	private String distretto;
	private String email;
	private String idmedico;
	private String inddom;
	private String indres;
	private String nome;
	private String numcivdom;
	private String numcivres;
	private String sesso;
	private String specializzazione;
	private String telefono1;
	private String telefono1dom;
	private String telefono1res;
	private String telefono2;
	private String telefono2dom;
	private String telefono2res;

	public SoImpattoMedici() {
	}


	public String getAcapo() {
		return this.acapo;
	}

	public void setAcapo(String acapo) {
		this.acapo = acapo;
	}


	public String getAmbito() {
		return this.ambito;
	}

	public void setAmbito(String ambito) {
		this.ambito = ambito;
	}


	public String getAsl() {
		return this.asl;
	}

	public void setAsl(String asl) {
		this.asl = asl;
	}


	public String getCapdom() {
		return this.capdom;
	}

	public void setCapdom(String capdom) {
		this.capdom = capdom;
	}


	public String getCapres() {
		return this.capres;
	}

	public void setCapres(String capres) {
		this.capres = capres;
	}


	public String getCategoria() {
		return this.categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}


	public String getCodassaft() {
		return this.codassaft;
	}

	public void setCodassaft(String codassaft) {
		this.codassaft = codassaft;
	}


	public String getCodicealternativo() {
		return this.codicealternativo;
	}

	public void setCodicealternativo(String codicealternativo) {
		this.codicealternativo = codicealternativo;
	}


	public String getCodiceambulatorio1() {
		return this.codiceambulatorio1;
	}

	public void setCodiceambulatorio1(String codiceambulatorio1) {
		this.codiceambulatorio1 = codiceambulatorio1;
	}


	public String getCodiceambulatorio2() {
		return this.codiceambulatorio2;
	}

	public void setCodiceambulatorio2(String codiceambulatorio2) {
		this.codiceambulatorio2 = codiceambulatorio2;
	}


	public String getCodiceambulatorio3() {
		return this.codiceambulatorio3;
	}

	public void setCodiceambulatorio3(String codiceambulatorio3) {
		this.codiceambulatorio3 = codiceambulatorio3;
	}


	public String getCodiceambulatorio4() {
		return this.codiceambulatorio4;
	}

	public void setCodiceambulatorio4(String codiceambulatorio4) {
		this.codiceambulatorio4 = codiceambulatorio4;
	}


	public String getCodiceassociazione() {
		return this.codiceassociazione;
	}

	public void setCodiceassociazione(String codiceassociazione) {
		this.codiceassociazione = codiceassociazione;
	}


	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}


	public String getCodiceregionale() {
		return this.codiceregionale;
	}

	public void setCodiceregionale(String codiceregionale) {
		this.codiceregionale = codiceregionale;
	}


	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public String getComdom() {
		return this.comdom;
	}

	public void setComdom(String comdom) {
		this.comdom = comdom;
	}


	public String getComres() {
		return this.comres;
	}

	public void setComres(String comres) {
		this.comres = comres;
	}


	public String getComunenascita() {
		return this.comunenascita;
	}

	public void setComunenascita(String comunenascita) {
		this.comunenascita = comunenascita;
	}


	@Column(name="DATA_LAUREA")
	public String getDataLaurea() {
		return this.dataLaurea;
	}

	public void setDataLaurea(String dataLaurea) {
		this.dataLaurea = dataLaurea;
	}


	public String getDataassmedico() {
		return this.dataassmedico;
	}

	public void setDataassmedico(String dataassmedico) {
		this.dataassmedico = dataassmedico;
	}


	public String getDataassmedicoaft() {
		return this.dataassmedicoaft;
	}

	public void setDataassmedicoaft(String dataassmedicoaft) {
		this.dataassmedicoaft = dataassmedicoaft;
	}


	public String getDatadecesso() {
		return this.datadecesso;
	}

	public void setDatadecesso(String datadecesso) {
		this.datadecesso = datadecesso;
	}


	public String getDatafinerapporto() {
		return this.datafinerapporto;
	}

	public void setDatafinerapporto(String datafinerapporto) {
		this.datafinerapporto = datafinerapporto;
	}


	public String getDatainiziorapporto() {
		return this.datainiziorapporto;
	}

	public void setDatainiziorapporto(String datainiziorapporto) {
		this.datainiziorapporto = datainiziorapporto;
	}


	public String getDataiscralbo() {
		return this.dataiscralbo;
	}

	public void setDataiscralbo(String dataiscralbo) {
		this.dataiscralbo = dataiscralbo;
	}


	public String getDatanascita() {
		return this.datanascita;
	}

	public void setDatanascita(String datanascita) {
		this.datanascita = datanascita;
	}


	public String getDataspec() {
		return this.dataspec;
	}

	public void setDataspec(String dataspec) {
		this.dataspec = dataspec;
	}


	public String getDatavaliditarecord() {
		return this.datavaliditarecord;
	}

	public void setDatavaliditarecord(String datavaliditarecord) {
		this.datavaliditarecord = datavaliditarecord;
	}


	public String getDescambito() {
		return this.descambito;
	}

	public void setDescambito(String descambito) {
		this.descambito = descambito;
	}


	public String getDescdistretto() {
		return this.descdistretto;
	}

	public void setDescdistretto(String descdistretto) {
		this.descdistretto = descdistretto;
	}


	public String getDescrassaft() {
		return this.descrassaft;
	}

	public void setDescrassaft(String descrassaft) {
		this.descrassaft = descrassaft;
	}


	public String getDescrizioentelefono2() {
		return this.descrizioentelefono2;
	}

	public void setDescrizioentelefono2(String descrizioentelefono2) {
		this.descrizioentelefono2 = descrizioentelefono2;
	}


	public String getDescrizioneassociazione() {
		return this.descrizioneassociazione;
	}

	public void setDescrizioneassociazione(String descrizioneassociazione) {
		this.descrizioneassociazione = descrizioneassociazione;
	}


	public String getDescrizionetelefono1() {
		return this.descrizionetelefono1;
	}

	public void setDescrizionetelefono1(String descrizionetelefono1) {
		this.descrizionetelefono1 = descrizionetelefono1;
	}


	public String getDistretto() {
		return this.distretto;
	}

	public void setDistretto(String distretto) {
		this.distretto = distretto;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Id
	public String getIdmedico() {
		return this.idmedico;
	}

	public void setIdmedico(String idmedico) {
		this.idmedico = idmedico;
	}


	public String getInddom() {
		return this.inddom;
	}

	public void setInddom(String inddom) {
		this.inddom = inddom;
	}


	public String getIndres() {
		return this.indres;
	}

	public void setIndres(String indres) {
		this.indres = indres;
	}


	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getNumcivdom() {
		return this.numcivdom;
	}

	public void setNumcivdom(String numcivdom) {
		this.numcivdom = numcivdom;
	}


	public String getNumcivres() {
		return this.numcivres;
	}

	public void setNumcivres(String numcivres) {
		this.numcivres = numcivres;
	}


	public String getSesso() {
		return this.sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}


	public String getSpecializzazione() {
		return this.specializzazione;
	}

	public void setSpecializzazione(String specializzazione) {
		this.specializzazione = specializzazione;
	}


	public String getTelefono1() {
		return this.telefono1;
	}

	public void setTelefono1(String telefono1) {
		this.telefono1 = telefono1;
	}


	public String getTelefono1dom() {
		return this.telefono1dom;
	}

	public void setTelefono1dom(String telefono1dom) {
		this.telefono1dom = telefono1dom;
	}


	public String getTelefono1res() {
		return this.telefono1res;
	}

	public void setTelefono1res(String telefono1res) {
		this.telefono1res = telefono1res;
	}


	public String getTelefono2() {
		return this.telefono2;
	}

	public void setTelefono2(String telefono2) {
		this.telefono2 = telefono2;
	}


	public String getTelefono2dom() {
		return this.telefono2dom;
	}

	public void setTelefono2dom(String telefono2dom) {
		this.telefono2dom = telefono2dom;
	}


	public String getTelefono2res() {
		return this.telefono2res;
	}

	public void setTelefono2res(String telefono2res) {
		this.telefono2res = telefono2res;
	}
	
	@PostLoad
	private void trimStrings(){
		if(acapo!=null)
			acapo = acapo.trim();
		if(ambito!=null)
			ambito = ambito.trim();
		if(asl!=null)
			asl = asl.trim();
		if(capdom!=null)
			capdom = capdom.trim();
		if(capres!=null)
			capres = capres.trim();
		if(categoria!=null)
			categoria = categoria.trim();
		if(codassaft!=null)
			codassaft = codassaft.trim();
		if(codicealternativo!=null)
			codicealternativo = codicealternativo.trim();
		if(codiceambulatorio1!=null)
			codiceambulatorio1 = codiceambulatorio1.trim();
		if(codiceambulatorio2!=null)
			codiceambulatorio2 = codiceambulatorio2.trim();
		if(codiceambulatorio3!=null)
			codiceambulatorio3 = codiceambulatorio3.trim();
		if(codiceambulatorio4!=null)
			codiceambulatorio4 = codiceambulatorio4.trim();
		if(codiceassociazione!=null)
			codiceassociazione = codiceassociazione.trim();
		if(codicefiscale!=null)
			codicefiscale = codicefiscale.trim();
		if(codiceregionale!=null)
			codiceregionale = codiceregionale.trim();
		if(cognome!=null)
			cognome = cognome.trim();
		if(comdom!=null)
			comdom = comdom.trim();
		if(comres!=null)
			comres = comres.trim();
		if(comunenascita!=null)
			comunenascita = comunenascita.trim();
		if(dataLaurea!=null)
			dataLaurea = dataLaurea.trim();
		if(dataassmedico!=null)
			dataassmedico = dataassmedico.trim();
		if(dataassmedicoaft!=null)
			dataassmedicoaft = dataassmedicoaft.trim();
		if(datadecesso!=null)
			datadecesso = datadecesso.trim();
		if(datafinerapporto!=null)
			datafinerapporto = datafinerapporto.trim();
		if(datainiziorapporto!=null)
			datainiziorapporto = datainiziorapporto.trim();
		if(dataiscralbo!=null)
			dataiscralbo = dataiscralbo.trim();
		if(datanascita!=null)
			datanascita = datanascita.trim();
		if(dataspec!=null)
			dataspec = dataspec.trim();
		if(datavaliditarecord!=null)
			datavaliditarecord = datavaliditarecord.trim();
		if(descambito!=null)
			descambito = descambito.trim();
		if(descdistretto!=null)
			descdistretto = descdistretto.trim();
		if(descrassaft!=null)
			descrassaft = descrassaft.trim();
		if(descrizioentelefono2!=null)
			descrizioentelefono2 = descrizioentelefono2.trim();
		if(descrizioneassociazione!=null)
			descrizioneassociazione = descrizioneassociazione.trim();
		if(descrizionetelefono1!=null)
			descrizionetelefono1 = descrizionetelefono1.trim();
		if(distretto!=null)
			distretto = distretto.trim();
		if(email!=null)
			email = email.trim();


		if(inddom!=null)
			inddom = inddom.trim();
		if(indres!=null)
			indres = indres.trim();
		if(nome!=null)
			nome = nome.trim();
		if(numcivdom!=null)
			numcivdom = numcivdom.trim();
		if(numcivres!=null)
			numcivres = numcivres.trim();
		if(sesso!=null)
			sesso = sesso.trim();
		if(specializzazione!=null)
			specializzazione = specializzazione.trim();
		if(telefono1!=null)
			telefono1 = telefono1.trim();
		if(telefono1dom!=null)
			telefono1dom = telefono1dom.trim();
		if(telefono1res!=null)
			telefono1res = telefono1res.trim();
		if(telefono2!=null)
			telefono2 = telefono2.trim();
		if(telefono2dom!=null)
			telefono2dom = telefono2dom.trim();
		if(telefono2res!=null)
			telefono2res = telefono2res.trim();
	}

}