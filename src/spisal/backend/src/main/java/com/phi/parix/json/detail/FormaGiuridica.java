
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
    "C_FORMA_GIURIDICA",
    "DESCRIZIONE"
})
public class FormaGiuridica {

    @JsonProperty("C_FORMA_GIURIDICA")
    private String cformaGiuridica;
    @JsonProperty("DESCRIZIONE")
    private String descrizione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cFormaGiuridica
     */
    @JsonProperty("C_FORMA_GIURIDICA")
    public String getCformaGiuridica() {
        return cformaGiuridica;
    }

    /**
     * 
     * @param cFormaGiuridica
     *     The c_forma_giuridica
     */
    @JsonProperty("C_FORMA_GIURIDICA")
    public void setCformaGiuridica(String cformaGiuridica) {
        this.cformaGiuridica = cformaGiuridica;
    }

    /**
     * 
     * @return
     *     The descrizione
     */
    @JsonProperty("DESCRIZIONE")
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * 
     * @param descrizione
     *     The descrizione
     */
    @JsonProperty("DESCRIZIONE")
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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
