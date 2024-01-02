
package com.phi.parix.json;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.phi.parix.json.detail.DatiImpresa;
import com.phi.parix.json.search.ListaImprese;
import com.phi.parix.json.search.ListaPersone;
import com.phi.parix.json.search.Righe;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "LISTA_IMPRESE",
    "LISTA_PERSONE",
    "DATI_IMPRESA",
    "Righe",
    "NumRigheEstratte",
    "ERRORE"
})
public class Dati {

    @JsonProperty("LISTA_IMPRESE")
    private ListaImprese listaImprese;
    @JsonProperty("LISTA_PERSONE")
    private ListaPersone listaPersone;
    @JsonProperty("DATI_IMPRESA")
    private DatiImpresa datiImpresa;
    @JsonProperty("Righe")
    private Righe righe;
    @JsonProperty("NumRigheEstratte")
    private String numRigheEstratte;
    
    
    @JsonProperty("ERRORE")
    private Errore errore;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The listaImprese
     */
    @JsonProperty("LISTA_IMPRESE")
    public ListaImprese getListaImprese() {
        return listaImprese;
    }

    /**
     * 
     * @param listaImprese
     *     The lista_imprese
     */
    @JsonProperty("LISTA_IMPRESE")
    public void setListaImprese(ListaImprese listaImprese) {
        this.listaImprese = listaImprese;
    }
    
    /**
     * 
     * @return
     *     The listaPersone
     */
    @JsonProperty("LISTA_PERSONE")
    public ListaPersone getListaPersone() {
        return listaPersone;
    }

    /**
     * 
     * @param listaPersone
     *     The lista_persone
     */
    @JsonProperty("LISTA_PERSONE")
    public void setListaPersone(ListaPersone listaPersone) {
        this.listaPersone = listaPersone;
    }
    
    /**
     * 
     * @return
     *     The datiImpresa
     */
    @JsonProperty("DATI_IMPRESA")
    public DatiImpresa getDatiImpresa() {
        return datiImpresa;
    }

    /**
     * 
     * @param datiImpresa
     *     The dati_impresa
     */
    @JsonProperty("DATI_IMPRESA")
    public void setDatiImpresa(DatiImpresa datiImpresa) {
        this.datiImpresa = datiImpresa;
    }

    
    @JsonProperty("Righe")
    public Righe getRighe() {
    	return righe;
    }

    @JsonProperty("Righe")
    public void setRighe(Righe righe) {
    	this.righe = righe;
    }

    @JsonProperty("NumRigheEstratte")
    public String getNumRigheEstratte() {
    	return numRigheEstratte;
    }

    @JsonProperty("NumRigheEstratte")
    public void setNumRigheEstratte(String numRigheEstratte) {
    	this.numRigheEstratte = numRigheEstratte;
    }
    /**
     * 
     * @return
     *     The listaImprese
     */
    @JsonProperty("ERRORE")
    public Errore getErrore() {
        return errore;
    }

    /**
     * 
     * @param errore
     *     The lista_imprese
     */
    @JsonProperty("ERRORE")
    public void setErrore(Errore errore) {
        this.errore = errore;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
