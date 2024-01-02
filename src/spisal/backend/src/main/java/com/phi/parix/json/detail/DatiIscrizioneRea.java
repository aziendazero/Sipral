
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
    "NREA",
    "DATA",
    "CCIAA",
    "C_FONTE",
    "FLAG_SEDE",
    "DT_ULT_AGGIORNAMENTO",
    "CESSAZIONE"
})
public class DatiIscrizioneRea {

    @JsonProperty("NREA")
    private Long nrea;
    @JsonProperty("DATA")
    private String data;
    @JsonProperty("CCIAA")
    private String cciaa;
    @JsonProperty("C_FONTE")
    private String cfonte;
    @JsonProperty("FLAG_SEDE")
    private String flagSede;
    @JsonProperty("DT_ULT_AGGIORNAMENTO")
    private Long dtUltAggiornamento;
    @JsonProperty("CESSAZIONE")
    private Cessazione cessazione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The nrea
     */
    @JsonProperty("NREA")
    public Long getNrea() {
        return nrea;
    }

    /**
     * 
     * @param nrea
     *     The nrea
     */
    @JsonProperty("NREA")
    public void setNrea(Long nrea) {
        this.nrea = nrea;
    }

    /**
     * 
     * @return
     *     The data
     */
    @JsonProperty("DATA")
    public String getData() {
        return data;
    }

    /**
     * 
     * @param data
     *     The data
     */
    @JsonProperty("DATA")
    public void setData(String data) {
        this.data = data;
    }

    /**
     * 
     * @return
     *     The cciaa
     */
    @JsonProperty("CCIAA")
    public String getCciaa() {
        return cciaa;
    }

    /**
     * 
     * @param cciaa
     *     The cciaa
     */
    @JsonProperty("CCIAA")
    public void setCciaa(String cciaa) {
        this.cciaa = cciaa;
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
     *     The flagSede
     */
    @JsonProperty("FLAG_SEDE")
    public String getFlagSede() {
        return flagSede;
    }

    /**
     * 
     * @param flagSede
     *     The flag_sede
     */
    @JsonProperty("FLAG_SEDE")
    public void setFlagSede(String flagSede) {
        this.flagSede = flagSede;
    }

    /**
     * 
     * @return
     *     The dtUltAggiornamento
     */
    @JsonProperty("DT_ULT_AGGIORNAMENTO")
    public Long getDtUltAggiornamento() {
        return dtUltAggiornamento;
    }

    /**
     * 
     * @param dtUltAggiornamento
     *     The dt_ult_aggiornamento
     */
    @JsonProperty("DT_ULT_AGGIORNAMENTO")
    public void setDtUltAggiornamento(Long dtUltAggiornamento) {
        this.dtUltAggiornamento = dtUltAggiornamento;
    }

    /**
     * 
     * @return
     *     The cessazione
     */
    @JsonProperty("CESSAZIONE")
    public Cessazione getCessazione() {
        return cessazione;
    }

    /**
     * 
     * @param cessazione
     *     The cessazione
     */
    @JsonProperty("CESSAZIONE")
    public void setCessazione(Cessazione cessazione) {
        this.cessazione = cessazione;
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
