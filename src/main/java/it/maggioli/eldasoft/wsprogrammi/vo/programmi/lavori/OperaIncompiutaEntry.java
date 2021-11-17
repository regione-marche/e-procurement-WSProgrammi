/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un'opera incompiuta.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"cup",
		"descrizione", 
		"determinazioneAmministrazione", 
		"ambito", 
		"anno",
		"importoIntervento", 
		"importoLavori", 
		"oneri", 
		"importoAvanzamento", 
		"percentualeAvanzamento", 
		"causa", 
		"stato",
		"infrastruttura",
		"discontinuitaRete",
		"fruibile",
		"ridimensionato",
		"destinazioneUso",
		"cessione",
		"previstaVendita",
		"demolizione",
		"oneriSito",
		"altriDati",
		"immobili"
		 })
@ApiModel(description = "Contenitore per i dati di un'opera incompiuta")
public class OperaIncompiutaEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long numoi;
	
	@ApiModelProperty(value = "Codice CUP", required=true)
	@Size(max=15)
	private String cup;
	
	@ApiModelProperty(value = "Descrizione opera", required=true)
	@Size(max=2000)
	private String descrizione;
	
	@ApiModelProperty(value="Determinazioni dell'amministrazione (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Determinazioni)")
	@Size(max=5)
	private String determinazioneAmministrazione;
	  
	@ApiModelProperty(value="Ambito di interesse dell'opera (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Ambito)")
	@Size(max=5)
	private String ambito;
	  
	@ApiModelProperty(value = "Anno ultimo q.e. approvato")
	private Long anno;

	@ApiModelProperty(value="Importo complessivo dell'intervento")
	private Double importoIntervento;
	
	@ApiModelProperty(value="Importo complessivo lavori")
	private Double importoLavori;
		
	@ApiModelProperty(value="Oneri necessari per l'ultimazione dei lavori")
	private Double oneri;
		
	@ApiModelProperty(value="Importo ultimo SAL")
	private Double importoAvanzamento;
		
	@ApiModelProperty(value="Percentuale avanzamento lavori %")
	private Double percentualeAvanzamento;
		
	@ApiModelProperty(value="Causa per la quale l'opera è incompiuta (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Causa)")
	@Size(max=5)
	private String causa;
		  
	@ApiModelProperty(value="Stato di realizzazione (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=StatoRealizzazione)")
	@Size(max=5)
	private String stato;
 
	@ApiModelProperty(value="Parte di infrastruttura di rete? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String infrastruttura;
		  
	@ApiModelProperty(value="Crea discontinuità nella rete? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String discontinuitaRete;
	
	@ApiModelProperty(value="Opera fruibile parzialmente? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String fruibile;
		  
	@ApiModelProperty(value="Utilizzo ridimensionato? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String ridimensionato;
		  
	@ApiModelProperty(value="Destinazione d'uso (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=DestinazioneUso)")
	@Size(max=5)
	private String destinazioneUso;

	@ApiModelProperty(value="Cessione per realizzazione di altra opera? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String cessione;
		  
	@ApiModelProperty(value="Prevista la vendita? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String previstaVendita;
		  
	@ApiModelProperty(value="Opera da demolire? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String demolizione;
		  
	@ApiModelProperty(value="Oneri Sito")
	private Double oneriSito;

	@ApiModelProperty(value="Altri dati")
	private AltriDatiOperaIncompiutaEntry altriDati;

	@ApiModelProperty(value="Immobili da trasferire")
	@Size(min=0)
    private List<ImmobileEntry> immobili = new ArrayList<ImmobileEntry>();

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDeterminazioneAmministrazione(
			String determinazioneAmministrazione) {
		this.determinazioneAmministrazione = StringUtils.stripToNull(determinazioneAmministrazione);
	}

	public String getDeterminazioneAmministrazione() {
		return determinazioneAmministrazione;
	}

	public void setAmbito(String ambito) {
		this.ambito = StringUtils.stripToNull(ambito);
	}

	public String getAmbito() {
		return ambito;
	}

	public void setAnno(Long anno) {
		this.anno = anno;
	}

	public Long getAnno() {
		return anno;
	}

	public void setImportoIntervento(Double importoIntervento) {
		this.importoIntervento = importoIntervento;
	}

	public Double getImportoIntervento() {
		return importoIntervento;
	}

	public void setImportoLavori(Double importoLavori) {
		this.importoLavori = importoLavori;
	}

	public Double getImportoLavori() {
		return importoLavori;
	}

	public void setOneri(Double oneri) {
		this.oneri = oneri;
	}

	public Double getOneri() {
		return oneri;
	}

	public void setImportoAvanzamento(Double importoAvanzamento) {
		this.importoAvanzamento = importoAvanzamento;
	}

	public Double getImportoAvanzamento() {
		return importoAvanzamento;
	}

	public void setPercentualeAvanzamento(Double percentualeAvanzamento) {
		this.percentualeAvanzamento = percentualeAvanzamento;
	}

	public Double getPercentualeAvanzamento() {
		return percentualeAvanzamento;
	}

	public void setCausa(String causa) {
		this.causa = StringUtils.stripToNull(causa);
	}

	public String getCausa() {
		return causa;
	}

	public void setStato(String stato) {
		this.stato = StringUtils.stripToNull(stato);
	}

	public String getStato() {
		return stato;
	}

	public void setInfrastruttura(String infrastruttura) {
		this.infrastruttura = StringUtils.stripToNull(infrastruttura);
	}

	public String getInfrastruttura() {
		return infrastruttura;
	}

	public void setDiscontinuitaRete(String discontinuitaRete) {
		this.discontinuitaRete = StringUtils.stripToNull(discontinuitaRete);
	}
	
	public String getDiscontinuitaRete() {
		return discontinuitaRete;
	}
	
	public void setFruibile(String fruibile) {
		this.fruibile = StringUtils.stripToNull(fruibile);
	}

	public String getFruibile() {
		return fruibile;
	}

	public void setRidimensionato(String ridimensionato) {
		this.ridimensionato = StringUtils.stripToNull(ridimensionato);
	}

	public String getRidimensionato() {
		return ridimensionato;
	}

	public void setDestinazioneUso(String destinazioneUso) {
		this.destinazioneUso = StringUtils.stripToNull(destinazioneUso);
	}

	public String getDestinazioneUso() {
		return destinazioneUso;
	}

	public void setCessione(String cessione) {
		this.cessione = StringUtils.stripToNull(cessione);
	}

	public String getCessione() {
		return cessione;
	}

	public void setPrevistaVendita(String previstaVendita) {
		this.previstaVendita = StringUtils.stripToNull(previstaVendita);
	}

	public String getPrevistaVendita() {
		return previstaVendita;
	}

	public void setDemolizione(String demolizione) {
		this.demolizione = StringUtils.stripToNull(demolizione);
	}

	public String getDemolizione() {
		return demolizione;
	}

	public void setOneriSito(Double oneriSito) {
		this.oneriSito = oneriSito;
	}

	public Double getOneriSito() {
		return oneriSito;
	}

	public void setAltriDati(AltriDatiOperaIncompiutaEntry altriDati) {
		this.altriDati = altriDati;
	}

	public AltriDatiOperaIncompiutaEntry getAltriDati() {
		return altriDati;
	}

	public void setImmobili(List<ImmobileEntry> immobili) {
		this.immobili = immobili;
	}

	public List<ImmobileEntry> getImmobili() {
		return immobili;
	}

	public void setContri(Long contri) {
		this.contri = contri;
	}

	@XmlTransient
	public Long getContri() {
		return contri;
	}

	public void setNumoi(Long numoi) {
		this.numoi = numoi;
	}

	@XmlTransient
	public Long getNumoi() {
		return numoi;
	}
	
	
}
