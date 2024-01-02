
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
    "CITTADINANZA",
    "INDIRIZZO",
    "ESTREMI_NASCITA",
    "COGNOME",
    "CODICE_FISCALE",
    "NOME",
    "SESSO"
})
public class PersonaFisica {

    @JsonProperty("CITTADINANZA")
    private String cittadinanza;
    @JsonProperty("INDIRIZZO")
    private Indirizzo indirizzo;
    @JsonProperty("ESTREMI_NASCITA")
    private EstremiNascita estremiNascita;
    @JsonProperty("COGNOME")
    private String cognome;
    @JsonProperty("CODICE_FISCALE")
    private String codiceFiscale;
    @JsonProperty("NOME")
    private String nome;
    @JsonProperty("SESSO")
    private String sesso;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cittadinanza
     */
    @JsonProperty("CITTADINANZA")
    public String getCittadinanza() {
        return cittadinanza;
    }

    /**
     * 
     * @param cittadinanza
     *     The cittadinanza
     */
    @JsonProperty("CITTADINANZA")
    public void setCittadinanza(String cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    /**
     * 
     * @return
     *     The indirizzo
     */
    @JsonProperty("INDIRIZZO")
    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    /**
     * 
     * @param indirizzo
     *     The indirizzo
     */
    @JsonProperty("INDIRIZZO")
    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    /**
     * 
     * @return
     *     The estremiNascita
     */
    @JsonProperty("ESTREMI_NASCITA")
    public EstremiNascita getEstremiNascita() {
        return estremiNascita;
    }

    /**
     * 
     * @param estremiNascita
     *     The estremi_nascita
     */
    @JsonProperty("ESTREMI_NASCITA")
    public void setEstremiNascita(EstremiNascita estremiNascita) {
        this.estremiNascita = estremiNascita;
    }

    /**
     * 
     * @return
     *     The cognome
     */
    @JsonProperty("COGNOME")
    public String getCognome() {
        return cognome;
    }

    /**
     * 
     * @param cognome
     *     The cognome
     */
    @JsonProperty("COGNOME")
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * 
     * @return
     *     The codiceFiscale
     */
    @JsonProperty("CODICE_FISCALE")
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * 
     * @param codiceFiscale
     *     The codice_fiscale
     */
    @JsonProperty("CODICE_FISCALE")
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    /**
     * 
     * @return
     *     The nome
     */
    @JsonProperty("NOME")
    public String getNome() {
        return nome;
    }

    /**
     * 
     * @param nome
     *     The nome
     */
    @JsonProperty("NOME")
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * 
     * @return
     *     The sesso
     */
    @JsonProperty("SESSO")
    public String getSesso() {
        return sesso;
    }

    /**
     * 
     * @param sesso
     *     The sesso
     */
    @JsonProperty("SESSO")
    public void setSesso(String sesso) {
        this.sesso = sesso;
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
