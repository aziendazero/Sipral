
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
    "PRESSO_N_REA",
    "PROGRESSIVO_PERSONA",
    "PRESSO_CCIAA",
    "PROGRESSIVO_LOC"
})
public class Identificativo {

    @JsonProperty("PRESSO_N_REA")
    private String pressoNRea;
    @JsonProperty("PROGRESSIVO_PERSONA")
    private String progressivoPersona;
    @JsonProperty("PRESSO_CCIAA")
    private String pressoCciaa;
    @JsonProperty("PROGRESSIVO_LOC")
    private String progressivoLoc;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The pressoNRea
     */
    @JsonProperty("PRESSO_N_REA")
    public String getPressoNRea() {
        return pressoNRea;
    }

    /**
     * 
     * @param pressoNRea
     *     The presso_n_rea
     */
    @JsonProperty("PRESSO_N_REA")
    public void setPressoNRea(String pressoNRea) {
        this.pressoNRea = pressoNRea;
    }

    /**
     * 
     * @return
     *     The progressivoPersona
     */
    @JsonProperty("PROGRESSIVO_PERSONA")
    public String getProgressivoPersona() {
        return progressivoPersona;
    }

    /**
     * 
     * @param progressivoPersona
     *     The progressivo_persona
     */
    @JsonProperty("PROGRESSIVO_PERSONA")
    public void setProgressivoPersona(String progressivoPersona) {
        this.progressivoPersona = progressivoPersona;
    }

    /**
     * 
     * @return
     *     The pressoCciaa
     */
    @JsonProperty("PRESSO_CCIAA")
    public String getPressoCciaa() {
        return pressoCciaa;
    }

    /**
     * 
     * @param pressoCciaa
     *     The presso_cciaa
     */
    @JsonProperty("PRESSO_CCIAA")
    public void setPressoCciaa(String pressoCciaa) {
        this.pressoCciaa = pressoCciaa;
    }

    /**
     * 
     * @return
     *     The progressivoLoc
     */
    @JsonProperty("PROGRESSIVO_LOC")
    public String getProgressivoLoc() {
        return progressivoLoc;
    }

    /**
     * 
     * @param progressivoLoc
     *     The progressivo_loc
     */
    @JsonProperty("PROGRESSIVO_LOC")
    public void setProgressivoLoc(String progressivoLoc) {
        this.progressivoLoc = progressivoLoc;
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
