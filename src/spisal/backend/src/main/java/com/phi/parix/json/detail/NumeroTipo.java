
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
    "CCIAA",
    "NUMERO",
    "TIPO_1",
    "N_REA"
})
public class NumeroTipo {

    @JsonProperty("CCIAA")
    private String cciaa;
    @JsonProperty("NUMERO")
    private Long numero;
    @JsonProperty("TIPO_1")
    private String tipo1;
    @JsonProperty("TIPO_2")
    private String tipo2;
    @JsonProperty("TIPO_3")
    private String tipo3;
    @JsonProperty("TIPO_4")
    private String tipo4;
    @JsonProperty("TIPO_5")
    private String tipo5;
    @JsonProperty("N_REA")
    private Long nrea;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
     *     The numero
     */
    @JsonProperty("NUMERO")
    public Long getNumero() {
        return numero;
    }

    /**
     * 
     * @param numero
     *     The numero
     */
    @JsonProperty("NUMERO")
    public void setNumero(Long numero) {
        this.numero = numero;
    }
    
    
    /**
     * 
     * @return
     *     The tIPO1
     */
    @JsonProperty("TIPO_1")
    public String getTipo1() {
        return tipo1;
    }

    /**
     * 
     * @param tipo1
     *     The TIPO_1
     */
    @JsonProperty("TIPO_1")
    public void setTipo1(String tipo1) {
        this.tipo1 = tipo1;
    }


    /**
     * 
     * @return
     *     The tIPO2
     */
    @JsonProperty("TIPO_2")
    public String getTipo2() {
        return tipo2;
    }

    /**
     * 
     * @param tipo2
     *     The TIPO_2
     */
    @JsonProperty("TIPO_2")
    public void setTipo2(String tipo2) {
        this.tipo2 = tipo2;
    }
    
    /**
     * 
     * @return
     *     The tIPO3
     */
    @JsonProperty("TIPO_3")
    public String getTipo3() {
        return tipo3;
    }

    /**
     * 
     * @param tipo3
     *     The TIPO_3
     */
    @JsonProperty("TIPO_3")
    public void setTipo3(String tipo3) {
        this.tipo3 = tipo3;
    }
    
    /**
     * 
     * @return
     *     The tIPO4
     */
    @JsonProperty("TIPO_4")
    public String getTipo4() {
        return tipo4;
    }

    /**
     * 
     * @param tipo4
     *     The TIPO_4
     */
    @JsonProperty("TIPO_4")
    public void setTipo4(String tipo4) {
        this.tipo4 = tipo4;
    }
    
    /**
     * 
     * @return
     *     The tIPO5
     */
    @JsonProperty("TIPO_5")
    public String getTipo5() {
        return tipo5;
    }

    /**
     * 
     * @param tipo5
     *     The TIPO_5
     */
    @JsonProperty("TIPO_5")
    public void setTipo5(String tipo5) {
        this.tipo5 = tipo5;
    }

    /**
     * 
     * @return
     *     The nRea
     */
    @JsonProperty("N_REA")
    public Long getNrea() {
        return nrea;
    }

    /**
     * 
     * @param nrea
     *     The n_rea
     */
    @JsonProperty("N_REA")
    public void setNrea(Long nrea) {
        this.nrea = nrea;
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
