
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
    "LOCALIZZAZIONE",
    "elemento",
    "PROVINCIA"
})
public class Localizzazioni {

    @JsonProperty("LOCALIZZAZIONE")
    private List<Localizzazione> localizzazione = new ArrayList<Localizzazione>();
    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("PROVINCIA")
    private String provincia;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The localizzazione
     */
    @JsonProperty("LOCALIZZAZIONE")
    public List<Localizzazione> getLocalizzazione() {
        return localizzazione;
    }

    /**
     * 
     * @param localizzazione
     *     The localizzazione
     */
    @JsonProperty("LOCALIZZAZIONE")
    public void setLocalizzazione(List<Localizzazione> localizzazione) {
        this.localizzazione = localizzazione;
    }

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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
