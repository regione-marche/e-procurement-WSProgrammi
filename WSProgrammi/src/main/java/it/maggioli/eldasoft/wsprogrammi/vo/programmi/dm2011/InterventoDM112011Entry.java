/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wsprogrammi.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un intervento.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"numeroProgressivo",
		"codiceInterno", 
		"descrizione", 
		"anno", 
		"tipologia",
		"categoria",
		"subCategoria",
		"istat", 
		"nuts", 
		"esenteCup",
		"cup", 
		"cpv",
		"annuale",
		"finalita",
		"priorita", 
		"conformitaUrbanistica",
		"conformitaAmbientale",
		"statoProgettazione",
		"annoInizioLavori",
		"trimestreInizioLavori",
		"annoFineLavori",
		"trimestreFineLavori",
		"risorseVincolatePerLegge1",
		"risorseVincolatePerLegge2",
		"risorseVincolatePerLegge3",
		"risorseMutuo1",
		"risorseMutuo2",
		"risorseMutuo3",
		"risorsePrivati1",
		"risorsePrivati2",
		"risorsePrivati3",
		"risorseBilancio1",
		"risorseBilancio2",
		"risorseBilancio3",
		"risorseImmobili1",
		"risorseImmobili2",
		"risorseImmobili3",
		"risorseAltro1",
		"risorseAltro2",
		"risorseAltro3",
		"tipologiaCapitalePrivato",
		"rup", 
		"immobili"
		 })
@ApiModel(description = "Contenitore per i dati di un intervento per lavori per Programma DM 11/2011")
public class InterventoDM112011Entry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden=true)
	private Long contri;
	
	@ApiModelProperty(hidden=true)
	private Long conint;
	
	@ApiModelProperty(hidden=true)
	private String codiceRup;
	
	@ApiModelProperty(value = "Numero progressivo intervento", required = true)
	private Long numeroProgressivo;
	
	@ApiModelProperty(value = "Codice interno attribuito dall'amministrazione")
	@Size(max=20)
	private String codiceInterno;
	
	@ApiModelProperty(value = "Descrizione intervento", required = true)
	@Size(max=2000)
	private String descrizione;
	
	@ApiModelProperty(value = "Annualità di riferimento dell'intervento (1°,2°,3° anno)",allowableValues="range[1, 3]", required = true)
	private Long annualitaRiferimento;
	
	@ApiModelProperty(value="Classificazione intervento: Tipologia (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaInterventoDM112011)")
	@Size(max=5)
	private String tipologia;
	
	@ApiModelProperty(value="Classificazione intervento: Categoria (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=CategoriaInterventoDM112011)")
	@Size(max=6)
	private String categoria;
	
	@ApiModelProperty(value="Subcategoria intervento")
	@Size(max=2)
	private String subCategoria;
	
	@ApiModelProperty(value="Localizzazione - Codice ISTAT del Comune")
	@Size(max=9)
	private String istat;

	@ApiModelProperty(value="Localizzazione - NUTS")
	@Size(max=20)
	private String nuts;
	
	@ApiModelProperty(value="Esente dalla richiesta CUP? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String esenteCup;
	
	@ApiModelProperty(value = "Codice CUP di progetto (assegnato da CIPE)")
	@Size(max=15)
	private String cup;
	
	@ApiModelProperty(value = "Codice CPV")
	@Size(max=20)
	private String cpv;
	
	@ApiModelProperty(value="L'intervento è stato inserito nel Piano annuale? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String annuale;
	
	@ApiModelProperty(value="Finalità dell'intervento (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=FinalitaDM112011)")
	@Size(max=5)
	private String finalita;
	
	@ApiModelProperty(value="Livello di Priorità (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Priorita)")
	private Long priorita;
	
	@ApiModelProperty(value="Svolta verifica conformità urbanistica? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String conformitaUrbanistica;
	
	@ApiModelProperty(value="Svolta verifica conformità vincoli ambientali? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String conformitaAmbientale;
	
	@ApiModelProperty(value="Stato Progettazione approvata (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=StatoProgettazioneDM112011)")
	@Size(max=2)
	private String statoProgettazione;
	
	@ApiModelProperty(value = "Tempi di esecuzione - Anno inizio lavori")
	private Long annoInizioLavori;
	
	@ApiModelProperty(value = "Tempi di esecuzione - Trimestre inizio lavori",allowableValues="range[1, 4]")
	private Long trimestreInizioLavori;
	
	@ApiModelProperty(value = "Tempi di esecuzione - Anno fine lavori")
	private Long annoFineLavori;
	
	@ApiModelProperty(value = "Tempi di esecuzione - Trimestre fine lavori",allowableValues="range[1, 4]")
	private Long trimestreFineLavori;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Primo anno")
	private Double risorseVincolatePerLegge1;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Secondo anno")
	private Double risorseVincolatePerLegge2;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Terzo anno")
	private Double risorseVincolatePerLegge3;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Primo anno")
	private Double risorseMutuo1;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Secondo anno")
	private Double risorseMutuo2;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Terzo anno")
	private Double risorseMutuo3;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Primo anno")
	private Double risorseImmobili1;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Secondo anno")
	private Double risorseImmobili2;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Terzo anno")
	private Double risorseImmobili3;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Primo anno")
	private Double risorseBilancio1;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Secondo anno")
	private Double risorseBilancio2;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Terzo anno")
	private Double risorseBilancio3;
	
	@ApiModelProperty(value="Altra tipologia - Primo anno")
	private Double risorseAltro1;
	
	@ApiModelProperty(value="Altra tipologia - Secondo anno")
	private Double risorseAltro2;
	
	@ApiModelProperty(value="Altra tipologia - Terzo anno")
	private Double risorseAltro3;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Primo anno")
	private Double risorsePrivati1;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Secondo anno")
	private Double risorsePrivati2;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Terzo anno")
	private Double risorsePrivati3;

	@ApiModelProperty(value="Lotto funzionale? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaCapitalePrivatoDM112011)")
	@Size(max=2)
	private String tipologiaCapitalePrivato;
	
	@ApiModelProperty(value = "Responsabile unico RUP")
	private DatiGeneraliTecnicoEntry rup;
	
	@ApiModelProperty(value="Immobili da trasferire")
	@Size(min=0)
    private List<ImmobileDM112011Entry> immobili = new ArrayList<ImmobileDM112011Entry>();

	public void setCodiceInterno(String codiceInterno) {
		this.codiceInterno = StringUtils.stripToNull(codiceInterno);
	}

	public String getCodiceInterno() {
		return codiceInterno;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setEsenteCup(String esenteCup) {
		this.esenteCup = StringUtils.stripToNull(esenteCup);
	}

	public String getEsenteCup() {
		return esenteCup;
	}

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setCpv(String cpv) {
		this.cpv = StringUtils.stripToNull(cpv);
	}

	public String getCpv() {
		return cpv;
	}

	public void setIstat(String istat) {
		this.istat = StringUtils.stripToNull(istat);
	}

	public String getIstat() {
		return istat;
	}

	public void setNuts(String nuts) {
		this.nuts = StringUtils.stripToNull(nuts);
	}

	public String getNuts() {
		return nuts;
	}

	public void setPriorita(Long priorita) {
		this.priorita = priorita;
	}

	public Long getPriorita() {
		return priorita;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = StringUtils.stripToNull(tipologia);
	}

	public String getTipologia() {
		return tipologia;
	}

	public void setCategoria(String categoria) {
		this.categoria = StringUtils.stripToNull(categoria);
	}

	public String getCategoria() {
		return categoria;
	}

	public void setFinalita(String finalita) {
		this.finalita = StringUtils.stripToNull(finalita);
	}

	public String getFinalita() {
		return finalita;
	}

	public void setConformitaUrbanistica(String conformitaUrbanistica) {
		this.conformitaUrbanistica = StringUtils.stripToNull(conformitaUrbanistica);
	}

	public String getConformitaUrbanistica() {
		return conformitaUrbanistica;
	}

	public void setConformitaAmbientale(String conformitaAmbientale) {
		this.conformitaAmbientale = StringUtils.stripToNull(conformitaAmbientale);
	}

	public String getConformitaAmbientale() {
		return conformitaAmbientale;
	}

	public void setStatoProgettazione(String statoProgettazione) {
		this.statoProgettazione = StringUtils.stripToNull(statoProgettazione);
	}

	public String getStatoProgettazione() {
		return statoProgettazione;
	}

	public void setRisorseVincolatePerLegge1(Double risorseVincolatePerLegge1) {
		this.risorseVincolatePerLegge1 = risorseVincolatePerLegge1;
	}

	public Double getRisorseVincolatePerLegge1() {
		return risorseVincolatePerLegge1;
	}

	public void setRisorseVincolatePerLegge2(Double risorseVincolatePerLegge2) {
		this.risorseVincolatePerLegge2 = risorseVincolatePerLegge2;
	}

	public Double getRisorseVincolatePerLegge2() {
		return risorseVincolatePerLegge2;
	}

	public void setRisorseVincolatePerLegge3(Double risorseVincolatePerLegge3) {
		this.risorseVincolatePerLegge3 = risorseVincolatePerLegge3;
	}

	public Double getRisorseVincolatePerLegge3() {
		return risorseVincolatePerLegge3;
	}

	public void setRisorseMutuo1(Double risorseMutuo1) {
		this.risorseMutuo1 = risorseMutuo1;
	}

	public Double getRisorseMutuo1() {
		return risorseMutuo1;
	}

	public void setRisorseMutuo2(Double risorseMutuo2) {
		this.risorseMutuo2 = risorseMutuo2;
	}

	public Double getRisorseMutuo2() {
		return risorseMutuo2;
	}

	public void setRisorseMutuo3(Double risorseMutuo3) {
		this.risorseMutuo3 = risorseMutuo3;
	}

	public Double getRisorseMutuo3() {
		return risorseMutuo3;
	}

	public void setRisorsePrivati1(Double risorsePrivati1) {
		this.risorsePrivati1 = risorsePrivati1;
	}

	public Double getRisorsePrivati1() {
		return risorsePrivati1;
	}

	public void setRisorsePrivati2(Double risorsePrivati2) {
		this.risorsePrivati2 = risorsePrivati2;
	}

	public Double getRisorsePrivati2() {
		return risorsePrivati2;
	}

	public void setRisorsePrivati3(Double risorsePrivati3) {
		this.risorsePrivati3 = risorsePrivati3;
	}

	public Double getRisorsePrivati3() {
		return risorsePrivati3;
	}

	public void setRisorseBilancio1(Double risorseBilancio1) {
		this.risorseBilancio1 = risorseBilancio1;
	}

	public Double getRisorseBilancio1() {
		return risorseBilancio1;
	}

	public void setRisorseBilancio2(Double risorseBilancio2) {
		this.risorseBilancio2 = risorseBilancio2;
	}

	public Double getRisorseBilancio2() {
		return risorseBilancio2;
	}

	public void setRisorseBilancio3(Double risorseBilancio3) {
		this.risorseBilancio3 = risorseBilancio3;
	}

	public Double getRisorseBilancio3() {
		return risorseBilancio3;
	}

	public void setRisorseImmobili1(Double risorseImmobili1) {
		this.risorseImmobili1 = risorseImmobili1;
	}

	public Double getRisorseImmobili1() {
		return risorseImmobili1;
	}

	public void setRisorseImmobili2(Double risorseImmobili2) {
		this.risorseImmobili2 = risorseImmobili2;
	}

	public Double getRisorseImmobili2() {
		return risorseImmobili2;
	}

	public void setRisorseImmobili3(Double risorseImmobili3) {
		this.risorseImmobili3 = risorseImmobili3;
	}

	public Double getRisorseImmobili3() {
		return risorseImmobili3;
	}

	public void setRisorseAltro1(Double risorseAltro1) {
		this.risorseAltro1 = risorseAltro1;
	}

	public Double getRisorseAltro1() {
		return risorseAltro1;
	}

	public void setRisorseAltro2(Double risorseAltro2) {
		this.risorseAltro2 = risorseAltro2;
	}

	public Double getRisorseAltro2() {
		return risorseAltro2;
	}

	public void setRisorseAltro3(Double risorseAltro3) {
		this.risorseAltro3 = risorseAltro3;
	}

	public Double getRisorseAltro3() {
		return risorseAltro3;
	}

	public void setRup(DatiGeneraliTecnicoEntry rup) {
		this.rup = rup;
	}

	public DatiGeneraliTecnicoEntry getRup() {
		return rup;
	}

	public void setSubCategoria(String subCategoria) {
		this.subCategoria = StringUtils.stripToNull(subCategoria);
	}

	public String getSubCategoria() {
		return subCategoria;
	}

	public void setAnnoInizioLavori(Long annoInizioLavori) {
		this.annoInizioLavori = annoInizioLavori;
	}

	public Long getAnnoInizioLavori() {
		return annoInizioLavori;
	}

	public void setTrimestreInizioLavori(Long trimestreInizioLavori) {
		this.trimestreInizioLavori = trimestreInizioLavori;
	}

	public Long getTrimestreInizioLavori() {
		return trimestreInizioLavori;
	}

	public void setAnnoFineLavori(Long annoFineLavori) {
		this.annoFineLavori = annoFineLavori;
	}

	public Long getAnnoFineLavori() {
		return annoFineLavori;
	}

	public void setTrimestreFineLavori(Long trimestreFineLavori) {
		this.trimestreFineLavori = trimestreFineLavori;
	}

	public Long getTrimestreFineLavori() {
		return trimestreFineLavori;
	}

	public void setTipologiaCapitalePrivato(String tipologiaCapitalePrivato) {
		this.tipologiaCapitalePrivato = StringUtils.stripToNull(tipologiaCapitalePrivato);
	}

	public String getTipologiaCapitalePrivato() {
		return tipologiaCapitalePrivato;
	}

	public void setAnnualitaRiferimento(Long annualitaRiferimento) {
		this.annualitaRiferimento = annualitaRiferimento;
	}

	public Long getAnnualitaRiferimento() {
		return annualitaRiferimento;
	}

	public void setAnnuale(String annuale) {
		this.annuale = StringUtils.stripToNull(annuale);
	}

	public String getAnnuale() {
		return annuale;
	}

	public void setImmobili(List<ImmobileDM112011Entry> immobili) {
		this.immobili = immobili;
	}

	public List<ImmobileDM112011Entry> getImmobili() {
		return immobili;
	}

	public void setContri(Long contri) {
		this.contri = contri;
	}

	@XmlTransient
	public Long getContri() {
		return contri;
	}

	public void setConint(Long conint) {
		this.conint = conint;
	}

	@XmlTransient
	public Long getConint() {
		return conint;
	}

	public void setNumeroProgressivo(Long numeroProgressivo) {
		this.numeroProgressivo = numeroProgressivo;
	}

	public Long getNumeroProgressivo() {
		return numeroProgressivo;
	}

	public void setCodiceRup(String codiceRup) {
		this.codiceRup = StringUtils.stripToNull(codiceRup);
	}

	@XmlTransient
	public String getCodiceRup() {
		return codiceRup;
	}
	
}
