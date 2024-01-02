
package com.phi.parix.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.phi.aur.json.Error;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "RISPOSTA"
})
public class ParixResponse {

    @JsonProperty("RISPOSTA")
    private Risposta risposta;
    
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonProperty("errors")
    private List<Error> errors = new ArrayList<Error>();

    /**
     * 
     * @return
     *     The risposta
     */
    @JsonProperty("RISPOSTA")
    public Risposta getRisposta() {
        return risposta;
    }

    /**
     * 
     * @param risposta
     *     The risposta
     */
    @JsonProperty("RISPOSTA")
    public void setRisposta(Risposta risposta) {
        this.risposta = risposta;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    
    /**
     * 
     * @return
     *     The errors
     */
    @JsonProperty("errors")
    public List<Error> getErrors() {
        return errors;
    }

    /**
     * 
     * @param errors
     *     The errors
     */
    @JsonProperty("errors")
    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
