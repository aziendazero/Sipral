
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
    "DT_DENUNCIA_CESS",
    "DT_CESSAZIONE",
    "CAUSALE"
})
public class CessazioneLoc {

	@JsonProperty("DT_CANCELLAZIONE")
    private Long dtCancellazione;
    @JsonProperty("DT_DENUNCIA_CESS")
    private Long dtDenunciaCess;
    @JsonProperty("DT_CESSAZIONE")
    private Long dtCessazione;
    @JsonProperty("CAUSALE")
    private String causale;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The DT_CANCELLAZIONE
     */
    @JsonProperty("DT_CANCELLAZIONE")
    public Long getDtCancellazione() {
        return dtCancellazione;
    }

    /**
     * 
     * @param dtCancellazione
     *     The DT_CANCELLAZIONE
     */
    @JsonProperty("DT_CANCELLAZIONE")
    public void setDtCancellazione(Long dtCancellazione) {
        this.dtCancellazione = dtCancellazione;
    }
    
    /**
     * 
     * @return
     *     The dtDenunciaCess
     */
    @JsonProperty("DT_DENUNCIA_CESS")
    public Long getDtDenunciaCess() {
        return dtDenunciaCess;
    }

    /**
     * 
     * @param dtDenunciaCess
     *     The dt_denuncia_cess
     */
    @JsonProperty("DT_DENUNCIA_CESS")
    public void setDtDenunciaCess(Long dtDenunciaCess) {
        this.dtDenunciaCess = dtDenunciaCess;
    }

    /**
     * 
     * @return
     *     The dtCessazione
     */
    @JsonProperty("DT_CESSAZIONE")
    public Long getDtCessazione() {
        return dtCessazione;
    }

    /**
     * 
     * @param dtCessazione
     *     The dt_cessazione
     */
    @JsonProperty("DT_CESSAZIONE")
    public void setDtCessazione(Long dtCessazione) {
        this.dtCessazione = dtCessazione;
    }

    /**
     * 
     * @return
     *     The causale
     */
    @JsonProperty("CAUSALE")
    public String getCausale() {
        return causale;
    }

    /**
     * 
     * @param causale
     *     The causale
     */
    @JsonProperty("CAUSALE")
    public void setCausale(String causale) {
        this.causale = causale;
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
