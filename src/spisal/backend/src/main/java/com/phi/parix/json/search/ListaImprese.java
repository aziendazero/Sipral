
package com.phi.parix.json.search;

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
import com.phi.parix.json.detail.EstremiImpresa;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "TOTALE",
    "ESTREMI_IMPRESA"
})
public class ListaImprese {

    @JsonProperty("TOTALE")
    private Integer totale;
    @JsonProperty("ESTREMI_IMPRESA")
    private List<EstremiImpresa> estremiImpresa = new ArrayList<EstremiImpresa>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The totale
     */
    @JsonProperty("TOTALE")
    public Integer getTotale() {
        return totale;
    }

    /**
     * 
     * @param totale
     *     The totale
     */
    @JsonProperty("TOTALE")
    public void setTotale(Integer totale) {
        this.totale = totale;
    }

    /**
     * 
     * @return
     *     The estremiImpresa
     */
    @JsonProperty("ESTREMI_IMPRESA")
    public List<EstremiImpresa> getEstremiImpresa() {
        return estremiImpresa;
    }

    /**
     * 
     * @param estremiImpresa
     *     The estremi_impresa
     */
    @JsonProperty("ESTREMI_IMPRESA")
    public void setEstremiImpresa(List<EstremiImpresa> estremiImpresa) {
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
