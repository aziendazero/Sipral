
package com.phi.parix.json.search;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.phi.parix.json.search.EstremiImpresa;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "elemento",
    "PERSONA",
    "ESTREMI_IMPRESA"
})
public class SchedaPersona {

    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("PERSONA")
    private Persona persona;
    @JsonProperty("ESTREMI_IMPRESA")
    private EstremiImpresa estremiImpresa;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    /**
     * 
     * @return
     *     The persona
     */
    @JsonProperty("PERSONA")
    public Persona getPersona() {
        return persona;
    }

    /**
     * 
     * @param persona
     *     The persona
     */
    @JsonProperty("PERSONA")
    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    /**
     * 
     * @return
     *     The estremiImpresa
     */
    @JsonProperty("ESTREMI_IMPRESA")
    public EstremiImpresa getEstremiImpresa() {
        return estremiImpresa;
    }

    /**
     * 
     * @param estremiImpresa
     *     The estremi_impresa
     */
    @JsonProperty("ESTREMI_IMPRESA")
    public void setEstremiImpresa(EstremiImpresa estremiImpresa) {
        this.estremiImpresa = estremiImpresa;
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
