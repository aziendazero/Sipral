
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
    "PERSONA_FISICA",
    "RAPPRESENTANTE",
    "elemento",
    "CARICHE",
    "IDENTIFICATIVO",
    "PERSONA_GIURIDICA"
})
public class Persona {

    @JsonProperty("PERSONA_FISICA")
    private PersonaFisica personaFisica;
    @JsonProperty("RAPPRESENTANTE")
    private String rappresentante;
    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("CARICHE")
    private Cariche cariche;
    @JsonProperty("IDENTIFICATIVO")
    private Identificativo identificativo;
    @JsonProperty("PERSONA_GIURIDICA")
    private PersonaGiuridica personaGiuridica;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The personaFisica
     */
    @JsonProperty("PERSONA_FISICA")
    public PersonaFisica getPersonaFisica() {
        return personaFisica;
    }

    /**
     * 
     * @param personaFisica
     *     The persona_fisica
     */
    @JsonProperty("PERSONA_FISICA")
    public void setPersonaFisica(PersonaFisica personaFisica) {
        this.personaFisica = personaFisica;
    }

    /**
     * 
     * @return
     *     The rappresentante
     */
    @JsonProperty("RAPPRESENTANTE")
    public String getRappresentante() {
        return rappresentante;
    }

    /**
     * 
     * @param rappresentante
     *     The rappresentante
     */
    @JsonProperty("RAPPRESENTANTE")
    public void setRappresentante(String rappresentante) {
        this.rappresentante = rappresentante;
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
     *     The cariche
     */
    @JsonProperty("CARICHE")
    public Cariche getCariche() {
        return cariche;
    }

    /**
     * 
     * @param cariche
     *     The cariche
     */
    @JsonProperty("CARICHE")
    public void setCariche(Cariche cariche) {
        this.cariche = cariche;
    }

    /**
     * 
     * @return
     *     The identificativo
     */
    @JsonProperty("IDENTIFICATIVO")
    public Identificativo getIdentificativo() {
        return identificativo;
    }

    /**
     * 
     * @param identificativo
     *     The identificativo
     */
    @JsonProperty("IDENTIFICATIVO")
    public void setIdentificativo(Identificativo identificativo) {
        this.identificativo = identificativo;
    }

    /**
     * 
     * @return
     *     The personaGiuridica
     */
    @JsonProperty("PERSONA_GIURIDICA")
    public PersonaGiuridica getPersonaGiuridica() {
        return personaGiuridica;
    }

    /**
     * 
     * @param personaGiuridica
     *     The persona_giuridica
     */
    @JsonProperty("PERSONA_GIURIDICA")
    public void setPersonaGiuridica(PersonaGiuridica personaGiuridica) {
        this.personaGiuridica = personaGiuridica;
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
