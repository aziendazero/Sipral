
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
    "CAPITALI",
    "PERSONE_SEDE",
    "ESTREMI_IMPRESA",
    "INFORMAZIONI_SEDE",
    "OGGETTO_SOCIALE",
    "DURATA_SOCIETA",
    "LOCALIZZAZIONI"
})
public class DatiImpresa {

    @JsonProperty("CAPITALI")
    private Capitali capitali;
    @JsonProperty("PERSONE_SEDE")
    private PersoneSede personeSede;
    @JsonProperty("ESTREMI_IMPRESA")
    private EstremiImpresa estremiImpresa;
    @JsonProperty("INFORMAZIONI_SEDE")
    private InformazioniSede informazioniSede;
    @JsonProperty("OGGETTO_SOCIALE")
    private String oggettoSociale;
    @JsonProperty("DURATA_SOCIETA")
    private DurataSocieta durataSocieta;
    @JsonProperty("LOCALIZZAZIONI")
    private List<Localizzazioni> localizzazioni = new ArrayList<Localizzazioni>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The capitali
     */
    @JsonProperty("CAPITALI")
    public Capitali getCapitali() {
        return capitali;
    }

    /**
     * 
     * @param capitali
     *     The capitali
     */
    @JsonProperty("CAPITALI")
    public void setCapitali(Capitali capitali) {
        this.capitali = capitali;
    }

    /**
     * 
     * @return
     *     The personeSede
     */
    @JsonProperty("PERSONE_SEDE")
    public PersoneSede getPersoneSede() {
        return personeSede;
    }

    /**
     * 
     * @param personeSede
     *     The persone_sede
     */
    @JsonProperty("PERSONE_SEDE")
    public void setPersoneSede(PersoneSede personeSede) {
        this.personeSede = personeSede;
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

    /**
     * 
     * @return
     *     The informazioniSede
     */
    @JsonProperty("INFORMAZIONI_SEDE")
    public InformazioniSede getInformazioniSede() {
        return informazioniSede;
    }

    /**
     * 
     * @param informazioniSede
     *     The informazioni_sede
     */
    @JsonProperty("INFORMAZIONI_SEDE")
    public void setInformazioniSede(InformazioniSede informazioniSede) {
        this.informazioniSede = informazioniSede;
    }

    /**
     * 
     * @return
     *     The oggettoSociale
     */
    @JsonProperty("OGGETTO_SOCIALE")
    public String getOggettoSociale() {
        return oggettoSociale;
    }

    /**
     * 
     * @param oggettoSociale
     *     The oggetto_sociale
     */
    @JsonProperty("OGGETTO_SOCIALE")
    public void setOggettoSociale(String oggettoSociale) {
        this.oggettoSociale = oggettoSociale;
    }

    /**
     * 
     * @return
     *     The durataSocieta
     */
    @JsonProperty("DURATA_SOCIETA")
    public DurataSocieta getDurataSocieta() {
        return durataSocieta;
    }

    /**
     * 
     * @param durataSocieta
     *     The durata_societa
     */
    @JsonProperty("DURATA_SOCIETA")
    public void setDurataSocieta(DurataSocieta durataSocieta) {
        this.durataSocieta = durataSocieta;
    }

    /**
     * 
     * @return
     *     The localizzazioni
     */
    @JsonProperty("LOCALIZZAZIONI")
    public List<Localizzazioni> getLocalizzazioni() {
        return localizzazioni;
    }

    /**
     * 
     * @param localizzazioni
     *     The localizzazioni
     */
    @JsonProperty("LOCALIZZAZIONI")
    public void setLocalizzazioni(List<Localizzazioni> localizzazioni) {
        this.localizzazioni = localizzazioni;
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
