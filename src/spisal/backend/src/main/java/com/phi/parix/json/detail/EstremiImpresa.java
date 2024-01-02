
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
    "DATI_ISCRIZIONE_RI",
    "PARTITA_IVA",
    "elemento",
    "FORMA_GIURIDICA",
    "CODICE_FISCALE",
    "DENOMINAZIONE",
    "DATI_ISCRIZIONE_REA"
})
public class EstremiImpresa {

    @JsonProperty("DATI_ISCRIZIONE_RI")
    private DatiIscrizioneRi datiIscrizioneRi;
    @JsonProperty("PARTITA_IVA")
    private String partitaIva;
    @JsonProperty("elemento")
    private String elemento;
    @JsonProperty("FORMA_GIURIDICA")
    private FormaGiuridica formaGiuridica;
    @JsonProperty("CODICE_FISCALE")
    private String codiceFiscale;
    @JsonProperty("DENOMINAZIONE")
    private String denominazione;
    @JsonProperty("DATI_ISCRIZIONE_REA")
    private List<DatiIscrizioneRea> datiIscrizioneRea = new ArrayList<DatiIscrizioneRea>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The datiIscrizioneRi
     */
    @JsonProperty("DATI_ISCRIZIONE_RI")
    public DatiIscrizioneRi getDatiIscrizioneRi() {
        return datiIscrizioneRi;
    }

    /**
     * 
     * @param datiIscrizioneRi
     *     The dati_iscrizione_ri
     */
    @JsonProperty("DATI_ISCRIZIONE_RI")
    public void setDatiIscrizioneRi(DatiIscrizioneRi datiIscrizioneRi) {
        this.datiIscrizioneRi = datiIscrizioneRi;
    }

    /**
     * 
     * @return
     *     The partitaIva
     */
    @JsonProperty("PARTITA_IVA")
    public String getPartitaIva() {
        return partitaIva;
    }

    /**
     * 
     * @param partitaIva
     *     The partita_iva
     */
    @JsonProperty("PARTITA_IVA")
    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
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
     *     The formaGiuridica
     */
    @JsonProperty("FORMA_GIURIDICA")
    public FormaGiuridica getFormaGiuridica() {
        return formaGiuridica;
    }

    /**
     * 
     * @param formaGiuridica
     *     The forma_giuridica
     */
    @JsonProperty("FORMA_GIURIDICA")
    public void setFormaGiuridica(FormaGiuridica formaGiuridica) {
        this.formaGiuridica = formaGiuridica;
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
     *     The denominazione
     */
    @JsonProperty("DENOMINAZIONE")
    public String getDenominazione() {
        return denominazione;
    }

    /**
     * 
     * @param denominazione
     *     The denominazione
     */
    @JsonProperty("DENOMINAZIONE")
    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    /**
     * 
     * @return
     *     The datiIscrizioneRea
     */
    @JsonProperty("DATI_ISCRIZIONE_REA")
    public List<DatiIscrizioneRea> getDatiIscrizioneRea() {
        return datiIscrizioneRea;
    }

    /**
     * 
     * @param datiIscrizioneRea
     *     The dati_iscrizione_rea
     */
    @JsonProperty("DATI_ISCRIZIONE_REA")
    public void setDatiIscrizioneRea(List<DatiIscrizioneRea> datiIscrizioneRea) {
        this.datiIscrizioneRea = datiIscrizioneRea;
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
