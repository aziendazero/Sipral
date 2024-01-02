
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
    "T_CODIFICA",
    "DESC_ATTIVITA",
    "DT_INIZIO_ATTIVITA",
    "C_IMPORTANZA",
    "C_ATTIVITA"
})
public class AttivitaIstat {

    @JsonProperty("T_CODIFICA")
    private Long tcodifica;
    @JsonProperty("DESC_ATTIVITA")
    private String descAttivita;
    @JsonProperty("DT_INIZIO_ATTIVITA")
    private Long dtInizioAttivita;
    @JsonProperty("C_IMPORTANZA")
    private String cimportanza;
    @JsonProperty("C_FONTE")
    private String cfonte;
    @JsonProperty("C_ATTIVITA")
    private String cattivita;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The tCodifica
     */
    @JsonProperty("T_CODIFICA")
    public Long getTcodifica() {
        return tcodifica;
    }

    /**
     * 
     * @param tcodifica
     *     The t_codifica
     */
    @JsonProperty("T_CODIFICA")
    public void setTcodifica(Long tcodifica) {
        this.tcodifica = tcodifica;
    }

    /**
     * 
     * @return
     *     The descAttivita
     */
    @JsonProperty("DESC_ATTIVITA")
    public String getDescAttivita() {
        return descAttivita;
    }

    /**
     * 
     * @param descAttivita
     *     The desc_attivita
     */
    @JsonProperty("DESC_ATTIVITA")
    public void setDescAttivita(String descAttivita) {
        this.descAttivita = descAttivita;
    }

    /**
     * 
     * @return
     *     The dtInizioAttivita
     */
    @JsonProperty("DT_INIZIO_ATTIVITA")
    public Long getDtInizioAttivita() {
        return dtInizioAttivita;
    }

    /**
     * 
     * @param dtInizioAttivita
     *     The dt_inizio_attivita
     */
    @JsonProperty("DT_INIZIO_ATTIVITA")
    public void setDtInizioAttivita(Long dtInizioAttivita) {
        this.dtInizioAttivita = dtInizioAttivita;
    }

    /**
     * 
     * @return
     *     The cFonte
     */
    @JsonProperty("C_FONTE")
    public String getCfonte() {
        return cfonte;
    }

    /**
     * 
     * @param cFonte
     *     The c_fonte
     */
    @JsonProperty("C_FONTE")
    public void setCfonte(String cfonte) {
        this.cfonte = cfonte;
    }
    
    /**
     * 
     * @return
     *     The cImportanza
     */
    @JsonProperty("C_IMPORTANZA")
    public String getCimportanza() {
        return cimportanza;
    }

    /**
     * 
     * @param cImportanza
     *     The c_importanza
     */
    @JsonProperty("C_IMPORTANZA")
    public void setCimportanza(String cimportanza) {
        this.cimportanza = cimportanza;
    }

    /**
     * 
     * @return
     *     The cAttivita
     */
    @JsonProperty("C_ATTIVITA")
    public String getCattivita() {
        return cattivita;
    }

    /**
     * 
     * @param cAttivita
     *     The c_attivita
     */
    @JsonProperty("C_ATTIVITA")
    public void setCattivita(String cattivita) {
        this.cattivita = cattivita;
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
