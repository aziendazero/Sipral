package com.phi.parix.json.detail;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"IMPIANTISTI_LOC",
	"MECCANICI",
	"IMPRESE_PULIZIA",
	"ALTRO_RUOLO_LOC",
	"ALTRO_RUOLO_NON_CCIAA",
	"ALTRO_RUOLO_CCIAA",
	"CESSAZIONE_RUOLO"	
})
public class RuoloLoc {

	@JsonProperty("IMPIANTISTI_LOC")
	private Impiantisti impiantistiLoc;
	@JsonProperty("MECCANICI")
	private Meccanici meccanici;
	@JsonProperty("IMPRESE_PULIZIA")
	private ImpresePulizia impresePulizia ;
	@JsonProperty("ALTRO_RUOLO_LOC")
	private AltroRuoloLoc altroRuoloLoc;
	@JsonProperty("CESSAZIONE_RUOLO")
	private CessazioneRuolo cessazioneRuolo;	
	
	@JsonProperty("IMPIANTISTI_LOC")
    public Impiantisti getImpiantistiLoc() {
		return impiantistiLoc;
	}
	@JsonProperty("IMPIANTISTI_LOC")
	public void setImpiantistiLoc(Impiantisti impiantistiLoc) {
		this.impiantistiLoc = impiantistiLoc;
	}
	@JsonProperty("MECCANICI")
	public Meccanici getMeccanici() {
		return meccanici;
	}
	@JsonProperty("MECCANICI")
	public void setMeccanici(Meccanici meccanici) {
		this.meccanici = meccanici;
	}
	@JsonProperty("IMPRESE_PULIZIA")
	public ImpresePulizia getImpresePulizia() {
		return impresePulizia;
	}
	@JsonProperty("IMPRESE_PULIZIA")
	public void setImpresePulizia(ImpresePulizia impresePulizia) {
		this.impresePulizia = impresePulizia;
	}
	@JsonProperty("ALTRO_RUOLO_LOC")
	public AltroRuoloLoc getAltroRuoloLoc() {
		return altroRuoloLoc;
	}
	@JsonProperty("ALTRO_RUOLO_LOC")
	public void setAltroRuoloLoc(AltroRuoloLoc altroRuoloLoc) {
		this.altroRuoloLoc = altroRuoloLoc;
	}
	@JsonProperty("CESSAZIONE_RUOLO")
	public CessazioneRuolo getCessazioneRuolo() {
		return cessazioneRuolo;
	}
	@JsonProperty("CESSAZIONE_RUOLO")
	public void setCessazioneRuolo(CessazioneRuolo cessazioneRuolo) {
		this.cessazioneRuolo = cessazioneRuolo;
	}

	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
