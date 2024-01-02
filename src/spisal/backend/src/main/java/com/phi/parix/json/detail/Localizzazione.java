
package com.phi.parix.json.detail;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "NUMERO_TIPO",
    "INDIRIZZO",
    "elemento",
    "ATTIVITA",
    "DT_APERTURA",
    "RUOLI_LOC",
    "CESSAZIONE_LOC",
    "CODICE_ATECO_UL"
})
public class Localizzazione {

    @JsonProperty("NUMERO_TIPO")
    private NumeroTipo numeroTipo;
    @JsonProperty("INDIRIZZO")
    private Indirizzo indirizzo;
    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("ATTIVITA")
    private String attivita;
    @JsonProperty("DENOMINAZIONE")
    private String denominazione;
    @JsonProperty("INSEGNA")
    private String insegna;
    
    @JsonProperty("DT_APERTURA")
    private Long dtApertura;
    @JsonProperty("RUOLI_LOC")
    private RuoliLoc ruoliLoc;
    @JsonProperty("CESSAZIONE_LOC")
    private CessazioneLoc cessazioneLoc;
    @JsonProperty("CODICE_ATECO_UL")
    private CodiceAtecoUl codiceAtecoUl;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    private Long internalId;
    private boolean locale = false;
    
    public boolean isLocale() {
		return locale;
	}
	public void setLocale(boolean locale) {
		this.locale = locale;
	}
	
	@JsonProperty("DENOMINAZIONE")
    public String getDenominazione() {
		return denominazione;
	}
    @JsonProperty("DENOMINAZIONE")
	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}
    @JsonProperty("INSEGNA")
	public String getInsegna() {
		return insegna;
	}
	@JsonProperty("INSEGNA")
	public void setInsegna(String insegna) {
		this.insegna = insegna;
	}

	/**
     * 
     * @return
     *     The numeroTipo
     */
    @JsonProperty("NUMERO_TIPO")
    public NumeroTipo getNumeroTipo() {
        return numeroTipo;
    }

    /**
     * 
     * @param numeroTipo
     *     The numero_tipo
     */
    @JsonProperty("NUMERO_TIPO")
    public void setNumeroTipo(NumeroTipo numeroTipo) {
        this.numeroTipo = numeroTipo;
    }

    /**
     * 
     * @return
     *     The indirizzo
     */
    @JsonProperty("INDIRIZZO")
    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    /**
     * 
     * @param indirizzo
     *     The indirizzo
     */
    @JsonProperty("INDIRIZZO")
    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * 
     * @return
     *     The elemento
     */
    @JsonProperty("elemento")
    public String getElemento() {
        return elemento;
    }

    /**
     * 
     * @param elemento
     *     The elemento
     */
    @JsonProperty("elemento")
    public void setElemento(String  elemento) {
        this.elemento = elemento;
    }

    /**
     * 
     * @return
     *     The attivita
     */
    @JsonProperty("ATTIVITA")
    public String getAttivita() {
        return attivita;
    }

    /**
     * 
     * @param attivita
     *     The attivita
     */
    @JsonProperty("ATTIVITA")
    public void setAttivita(String attivita) {
        this.attivita = attivita;
    }

    /**
     * 
     * @return
     *     The dtApertura
     */
    @JsonProperty("DT_APERTURA")
    public Long getDtApertura() {
        return dtApertura;
    }

    /**
     * 
     * @param dtApertura
     *     The dt_apertura
     */
    @JsonProperty("DT_APERTURA")
    public void setDtApertura(Long dtApertura) {
        this.dtApertura = dtApertura;
    }

    /**
     * 
     * @return
     *     The ruoliLoc
     */
    @JsonProperty("RUOLI_LOC")
    public RuoliLoc getRuoliLoc() {
        return ruoliLoc;
    }

    /**
     * 
     * @param ruoliLoc
     *     The cessazione_loc
     */
    @JsonProperty("RUOLI_LOC")
    public void setRuoliLoc(RuoliLoc ruoliLoc) {
        this.ruoliLoc = ruoliLoc;
    }

    
    /**
     * 
     * @return
     *     The cessazioneLoc
     */
    @JsonProperty("CESSAZIONE_LOC")
    public CessazioneLoc getCessazioneLoc() {
        return cessazioneLoc;
    }

    /**
     * 
     * @param cessazioneLoc
     *     The cessazione_loc
     */
    @JsonProperty("CESSAZIONE_LOC")
    public void setCessazioneLoc(CessazioneLoc cessazioneLoc) {
        this.cessazioneLoc = cessazioneLoc;
    }

    /**
     * 
     * @return
     *     The codiceAtecoUl
     */
    @JsonProperty("CODICE_ATECO_UL")
    public CodiceAtecoUl getCodiceAtecoUl() {
        return codiceAtecoUl;
    }

    /**
     * 
     * @param codiceAtecoUl
     *     The codice_ateco_ul
     */
    @JsonProperty("CODICE_ATECO_UL")
    public void setCodiceAtecoUl(CodiceAtecoUl codiceAtecoUl) {
        this.codiceAtecoUl = codiceAtecoUl;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    @JsonIgnore
	public Long getInternalId() {
		return internalId;
	}
    @JsonIgnore
	public void setInternalId(Long internalId) {
		this.internalId = internalId;
	}

}
