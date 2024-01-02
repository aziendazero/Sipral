
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
    "CAP",
    "STATO",
    "ALTRE_INDICAZIONI",
    "PROVINCIA",
    "INDIRIZZO_PEC",
    "C_COMUNE",
    "TOPONIMO",
    "COMUNE",
    "N_CIVICO",
    "VIA"
})
public class Indirizzo {

    @JsonProperty("CAP")
    private String cap;
    @JsonProperty("STATO")
    private String stato;
    @JsonProperty("ALTRE_INDICAZIONI")
    private String altreIndicazioni;
    @JsonProperty("PROVINCIA")
    private String provincia;
    @JsonProperty("INDIRIZZO_PEC")
    private String indirizzoPec;
    @JsonProperty("C_COMUNE")
    private String ccomune;
    @JsonProperty("TOPONIMO")
    private String toponimo;
    @JsonProperty("COMUNE")
    private String comune;
    @JsonProperty("N_CIVICO")
    private String ncivico;
    @JsonProperty("VIA")
    private String via;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cap
     */
    @JsonProperty("CAP")
    public String getCap() {
        return cap;
    }

    /**
     * 
     * @param cap
     *     The cap
     */
    @JsonProperty("CAP")
    public void setCap(String cap) {
        this.cap = cap;
    }

    /**
     * 
     * @return
     *     The stato
     */
    @JsonProperty("STATO")
    public String getStato() {
        return stato;
    }

    /**
     * 
     * @param stato
     *     The stato
     */
    @JsonProperty("STATO")
    public void setStato(String stato) {
        this.stato = stato;
    }

    /**
     * 
     * @return
     *     The altreIndicazioni
     */
    @JsonProperty("ALTRE_INDICAZIONI")
    public String getAltreIndicazioni() {
        return altreIndicazioni;
    }

    /**
     * 
     * @param altreIndicazioni
     *     The altre_indicazioni
     */
    @JsonProperty("ALTRE_INDICAZIONI")
    public void setAltreIndicazioni(String altreIndicazioni) {
        this.altreIndicazioni = altreIndicazioni;
    }

    /**
     * 
     * @return
     *     The provincia
     */
    @JsonProperty("PROVINCIA")
    public String getProvincia() {
        return provincia;
    }

    /**
     * 
     * @param provincia
     *     The provincia
     */
    @JsonProperty("PROVINCIA")
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * 
     * @return
     *     The indirizzoPec
     */
    @JsonProperty("INDIRIZZO_PEC")
    public String getIndirizzoPec() {
        return indirizzoPec;
    }

    /**
     * 
     * @param indirizzoPec
     *     The indirizzo_pec
     */
    @JsonProperty("INDIRIZZO_PEC")
    public void setIndirizzoPec(String indirizzoPec) {
        this.indirizzoPec = indirizzoPec;
    }

    /**
     * 
     * @return
     *     The cComune
     */
    @JsonProperty("C_COMUNE")
    public String getCcomune() {
        return ccomune;
    }

    /**
     * 
     * @param ccomune
     *     The c_comune
     */
    @JsonProperty("C_COMUNE")
    public void setCcomune(String ccomune) {
        this.ccomune = ccomune;
    }

    /**
     * 
     * @return
     *     The toponimo
     */
    @JsonProperty("TOPONIMO")
    public String getToponimo() {
        return toponimo;
    }

    /**
     * 
     * @param toponimo
     *     The toponimo
     */
    @JsonProperty("TOPONIMO")
    public void setToponimo(String toponimo) {
        this.toponimo = toponimo;
    }

    /**
     * 
     * @return
     *     The comune
     */
    @JsonProperty("COMUNE")
    public String getComune() {
        return comune;
    }

    /**
     * 
     * @param comune
     *     The comune
     */
    @JsonProperty("COMUNE")
    public void setComune(String comune) {
        this.comune = comune;
    }

    /**
     * 
     * @return
     *     The ncivico
     */
    @JsonProperty("N_CIVICO")
    public String getNcivico() {
        return ncivico;
    }

    /**
     * 
     * @param ncivico
     *     The n_civico
     */
    @JsonProperty("N_CIVICO")
    public void setNcivico(String ncivico) {
        this.ncivico = ncivico;
    }

    /**
     * 
     * @return
     *     The via
     */
    @JsonProperty("VIA")
    public String getVia() {
        return via;
    }

    /**
     * 
     * @param via
     *     The via
     */
    @JsonProperty("VIA")
    public void setVia(String via) {
        this.via = via;
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
