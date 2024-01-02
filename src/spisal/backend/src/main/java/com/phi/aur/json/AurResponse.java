
package com.phi.aur.json;

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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "result",
    "persons",
    "errors",
    "internalId"
})
public class AurResponse {

    @JsonProperty("result")
    private String result;
    @JsonProperty("persons")
    private List<Person> persons = new ArrayList<Person>();
    @JsonProperty("errors")
    private List<Error> errors = new ArrayList<Error>();
    @JsonProperty("internalId")
    private String internalId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The result
     */
    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    /**
     * 
     * @param result
     *     The result
     */
    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 
     * @return
     *     The persons
     */
    @JsonProperty("persons")
    public List<Person> getPersons() {
        return persons;
    }

    /**
     * 
     * @param persons
     *     The persons
     */
    @JsonProperty("persons")
    public void setPersons(List<Person> persons) {
        this.persons = persons;
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
    
    /**
     * 
     * @return
     *     The internalId
     */
    @JsonProperty("internalId")
    public String getInternalId() {
        return internalId;
    }

    /**
     * 
     * @param internalId
     *     The internalId
     */
    @JsonProperty("internalId")
    public void setInternalId(String internalId) {
        this.internalId = internalId;
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
