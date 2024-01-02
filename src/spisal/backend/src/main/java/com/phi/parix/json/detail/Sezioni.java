
package com.phi.parix.json.detail;

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
    "SEZIONE"
})
public class Sezioni {

    @JsonProperty("SEZIONE")
    private List<Sezione> sezione = new ArrayList<Sezione>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The sezione
     */
    @JsonProperty("SEZIONE")
    public List<Sezione> getSezione() {
        return sezione;
    }

    /**
     * 
     * @param sezione
     *     The sezione
     */
    @JsonProperty("SEZIONE")
    public void setSezione(List<Sezione> sezione) {
        this.sezione = sezione;
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
