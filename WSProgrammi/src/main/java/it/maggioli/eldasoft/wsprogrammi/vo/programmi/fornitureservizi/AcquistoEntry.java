/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wsprogrammi.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati di un acquisto.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"cui",
		"settore",
		"codiceInterno", 
		"descrizione", 
		"anno", 
		"esenteCup",
		"cup", 
		"acquistoRicompreso",
		"cuiCollegato",
		"cpv",
		"istat", 
		"nuts", 
		"quantita",
		"unitaMisura",
		"priorita", 
		"lottoFunzionale", 
		"durataInMesi",
		"nuovoAffidamento",
		"risorseVincolatePerLegge1",
		"risorseVincolatePerLegge2",
		"risorseVincolatePerLeggeSucc",
		"risorseMutuo1",
		"risorseMutuo2",
		"risorseMutuoSucc",
		"risorsePrivati1",
		"risorsePrivati2",
		"risorsePrivatiSucc",
		"risorseBilancio1",
		"risorseBilancio2",
		"risorseBilancioSucc",
		"risorseArt3_1",
		"risorseArt3_2",
		"risorseArt3_Succ",
		"risorseImmobili1",
		"risorseImmobili2",
		"risorseImmobiliSucc",
		"risorseAltro1",
		"risorseAltro2",
		"risorseAltroSucc",
		"speseSostenute",
		"tipologiaCapitalePrivato",
		"importoRisorseFinanziarie",
		"importoRisorseFinanziarieRegionali",
		"importoRisorseFinanziarieAltro",
		"meseAvvioProcedura",
		"delega",
		"codiceSoggettoDelegato",
		"nomeSoggettoDelegato",
		"variato",
		"note",
		"direzioneGenerale",
		"strutturaOperativa",
		"referenteDati",
		"dirigenteResponsabile",
		"proceduraAffidamento",
		"acquistoVerdi",
		"normativaRiferimento",
		"oggettoVerdi",
		"cpvVerdi",
		"importoNettoIvaVerdi",
		"importoIvaVerdi",
		"importoTotVerdi",
		"acquistoMaterialiRiciclati",
		"oggettoMatRic",
		"cpvMatRic",
		"importoNettoIvaMatRic",
		"importoIvaMatRic",
		"importoTotMatRic",
		"importoIva1",
		"importoIva2",
		"importoIvaSucc",
		"coperturaFinanziaria",
		"valutazione",
		"rup" 
		 })
@ApiModel(description = "Contenitore per i dati di un intervento")
public class AcquistoEntry implements Serializable {
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
	
	@ApiModelProperty(value = "Numero intervento CUI (‘F’ + CodiceFiscale Stazione Appaltante(11 char) + anno (4 char) + progressivo (5 char))", required=true)
	@Size(max=25)
	private String cui;
	
	@ApiModelProperty(value="Settore F=Forniture, S=Servizi", allowableValues="F,S", required=true)
	@Size(max=1)
	private String settore;
	
	@ApiModelProperty(value = "Codice interno attribuito dall'amministrazione")
	@Size(max=20)
	private String codiceInterno;
	
	@ApiModelProperty(value = "Descrizione acquisto",required=true)
	@Size(max=2000)
	private String descrizione;
	
	@ApiModelProperty(value = "Annualità di riferimento avvio procedura di affidamento (1°,2°,3° anno)",allowableValues="range[1, 3]",required=true)
	private Long anno;
	
	@ApiModelProperty(value="Esente dalla richiesta CUP? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String esenteCup;
	
	@ApiModelProperty(value = "Codice CUP di progetto (assegnato da CIPE)")
	@Size(max=15)
	private String cup;
	
	@ApiModelProperty(value="Acquisto ricompreso nell'importo di lavoro o altra acquisizione nel programma? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=AcquistoRicompreso)")
	private Long acquistoRicompreso;
	
	@ApiModelProperty(value = "Codice CUI dell'intervento collegato")
	@Size(max=25)
	private String cuiCollegato;
	
	@ApiModelProperty(value = "Codice CPV")
	@Size(max=20)
	private String cpv;
	
	@ApiModelProperty(value="Localizzazione - Codice ISTAT del Comune")
	@Size(max=9)
	private String istat;

	@ApiModelProperty(value="Localizzazione - NUTS")
	@Size(max=20)
	private String nuts;
	
	@ApiModelProperty(value="Quantità")
	private Double quantita;
	
	@ApiModelProperty(value="Unità di misura (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=UnitaMisura)")
	private Long unitaMisura;
	
	@ApiModelProperty(value="Livello di Priorità (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Priorita)")
	private Long priorita;
	
	@ApiModelProperty(value="Lotto funzionale? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String lottoFunzionale;
	
	@ApiModelProperty(value = "Durata del contratto (mesi)")
	private Long durataInMesi;
	
	@ApiModelProperty(value="Nuovo affidamento contratto in essere? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String nuovoAffidamento;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Primo anno")
	private Double risorseVincolatePerLegge1;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Secondo anno")
	private Double risorseVincolatePerLegge2;
	
	@ApiModelProperty(value="Risorse derivanti da entrate aventi destinazione vincolata per legge - Annualità successive")
	private Double risorseVincolatePerLeggeSucc;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Primo anno")
	private Double risorseMutuo1;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Secondo anno")
	private Double risorseMutuo2;
	
	@ApiModelProperty(value="Risorse derivanti da entrate acquisite mediante contrazione di mutuo - Annualità successive")
	private Double risorseMutuoSucc;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Primo anno")
	private Double risorsePrivati1;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Secondo anno")
	private Double risorsePrivati2;
	
	@ApiModelProperty(value="Risorse acquisite mediante apporti di capitale privato - Annualità successive")
	private Double risorsePrivatiSucc;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Primo anno")
	private Double risorseBilancio1;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Secondo anno")
	private Double risorseBilancio2;
	
	@ApiModelProperty(value="Stanziamenti di bilancio - Annualità successive")
	private Double risorseBilancioSucc;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Primo anno")
	private Double risorseArt3_1;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Secondo anno")
	private Double risorseArt3_2;
	
	@ApiModelProperty(value="Finanziamenti art. 3 DL 310/1990 - Annualità successive")
	private Double risorseArt3_Succ;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Primo anno")
	private Double risorseImmobili1;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Secondo anno")
	private Double risorseImmobili2;
	
	@ApiModelProperty(value="Risorse derivanti da trasferimento immobili - Annualità successive")
	private Double risorseImmobiliSucc;
	
	@ApiModelProperty(value="Altra tipologia - Primo anno")
	private Double risorseAltro1;
	
	@ApiModelProperty(value="Altra tipologia - Secondo anno")
	private Double risorseAltro2;
	
	@ApiModelProperty(value="Altra tipologia - Annualità successive")
	private Double risorseAltroSucc;

	@ApiModelProperty(value="Spese già sostenute")
	private Double speseSostenute;
	
	@ApiModelProperty(value="Tipologia apporto di capitale privato (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaCapitalePrivato)")
	@Size(max=2)
	private String tipologiaCapitalePrivato;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo risorse finanziarie stato/UE")
	private Double importoRisorseFinanziarie;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo risorse finanziarie regionali")
	private Double importoRisorseFinanziarieRegionali;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo risorse finanziarie altro")
	private Double importoRisorseFinanziarieAltro;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Mese previsto per avvio procedura contrattuale")
	private Long meseAvvioProcedura;
	
	@ApiModelProperty(value="Si intende delegare la procedura di affidamento? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String delega;
	
	@ApiModelProperty(value = "Codice AUSA Centrale di Committenza o Soggetto Aggregatore")
	@Size(max=20)
	private String codiceSoggettoDelegato;
	
	@ApiModelProperty(value = "Denominazione Centrale di Committenza o Soggetto Aggregatore")
	@Size(max=160)
	private String nomeSoggettoDelegato;
	
	@ApiModelProperty(value="Intervento variato a seguito di modifica programma (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=VariatoAcquisti)")
	private Long variato;
	
	@ApiModelProperty(value = "Eventuali note")
	@Size(max=2000)
	private String note;

	@ApiModelProperty(value = "[Attributo aggiuntivo, previsto in ambiti regionali] Direzione generale")
	@Size(max=160)
	private String direzioneGenerale;
	
	@ApiModelProperty(value = "[Attributo aggiuntivo, previsto in ambiti regionali] Struttura operativa")
	@Size(max=160)
	private String strutturaOperativa;
	
	@ApiModelProperty(value = "[Attributo aggiuntivo, previsto in ambiti regionali] Referente per i dati comunicati")
	@Size(max=160)
	private String referenteDati;
	
	@ApiModelProperty(value = "[Attributo aggiuntivo, previsto in ambiti regionali] Dirigente responsabile d'ufficio")
	@Size(max=160)
	private String dirigenteResponsabile;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Procedura affidamento (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=ProceduraAffidamento)")
	private Long proceduraAffidamento;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Sono presenti acq. verdi art. 34 Dlgs 50/2016 (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Acquisto)")
	private Long acquistoVerdi;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Normativa di riferimento")
	@Size(max=200)
	private String normativaRiferimento;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Oggetto verdi")
	@Size(max=500)
	private String oggettoVerdi;
	
	@ApiModelProperty(value = "[Attributo aggiuntivo, previsto in ambiti regionali] Codice CPV verdi")
	@Size(max=12)
	private String cpvVerdi;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo al netto dell'IVA verdi")
	private Double importoNettoIvaVerdi;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo IVA verdi")
	private Double importoIvaVerdi;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo Totale verdi")
	private Double importoTotVerdi;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Acquisto di beni realizzati con materiali riciclati? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Acquisto)")
	private Long acquistoMaterialiRiciclati;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Oggetto materiali riciclati")
	@Size(max=500)
	private String oggettoMatRic;
	
	@ApiModelProperty(value = "[Attributo aggiuntivo, previsto in ambiti regionali] Codice CPV materiali riciclati")
	@Size(max=12)
	private String cpvMatRic;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo al netto dell'IVA materiali riciclati")
	private Double importoNettoIvaMatRic;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo IVA materiali riciclati")
	private Double importoIvaMatRic;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo Totale materiali riciclati")
	private Double importoTotMatRic;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo IVA - Primo anno")
	private Double importoIva1;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo IVA - Secondo anno")
	private Double importoIva2;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Importo IVA - Annualità successive")
	private Double importoIvaSucc;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] L'acquisto ha copertura finanziaria? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String coperturaFinanziaria;
	
	@ApiModelProperty(value="[Attributo aggiuntivo, previsto in ambiti regionali] Valutazione del responsabile dl programma (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Valutazione)")
	private Long valutazione;
	
	@ApiModelProperty(value = "Responsabile unico RUP")
	private DatiGeneraliTecnicoEntry rup;
	
	public void setCui(String cui) {
		this.cui = StringUtils.stripToNull(cui);
	}

	public String getCui() {
		return cui;
	}

	public void setSettore(String settore) {
		this.settore = StringUtils.stripToNull(settore);
	}

	public String getSettore() {
		return settore;
	}

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

	public void setAnno(Long anno) {
		this.anno = anno;
	}

	public Long getAnno() {
		return anno;
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

	public void setAcquistoRicompreso(Long acquistoRicompreso) {
		this.acquistoRicompreso = acquistoRicompreso;
	}

	public Long getAcquistoRicompreso() {
		return acquistoRicompreso;
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

	public void setLottoFunzionale(String lottoFunzionale) {
		this.lottoFunzionale = StringUtils.stripToNull(lottoFunzionale);
	}

	public String getLottoFunzionale() {
		return lottoFunzionale;
	}

	public void setDurataInMesi(Long durataInMesi) {
		this.durataInMesi = durataInMesi;
	}

	public Long getDurataInMesi() {
		return durataInMesi;
	}

	public void setNuovoAffidamento(String nuovoAffidamento) {
		this.nuovoAffidamento = StringUtils.stripToNull(nuovoAffidamento);
	}

	public String getNuovoAffidamento() {
		return nuovoAffidamento;
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

	public void setRisorseVincolatePerLeggeSucc(
			Double risorseVincolatePerLeggeSucc) {
		this.risorseVincolatePerLeggeSucc = risorseVincolatePerLeggeSucc;
	}

	public Double getRisorseVincolatePerLeggeSucc() {
		return risorseVincolatePerLeggeSucc;
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

	public void setRisorseMutuoSucc(Double risorseMutuoSucc) {
		this.risorseMutuoSucc = risorseMutuoSucc;
	}

	public Double getRisorseMutuoSucc() {
		return risorseMutuoSucc;
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

	public void setRisorsePrivatiSucc(Double risorsePrivatiSucc) {
		this.risorsePrivatiSucc = risorsePrivatiSucc;
	}

	public Double getRisorsePrivatiSucc() {
		return risorsePrivatiSucc;
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

	public void setRisorseBilancioSucc(Double risorseBilancioSucc) {
		this.risorseBilancioSucc = risorseBilancioSucc;
	}

	public Double getRisorseBilancioSucc() {
		return risorseBilancioSucc;
	}

	public void setRisorseArt3_1(Double risorseArt3_1) {
		this.risorseArt3_1 = risorseArt3_1;
	}

	public Double getRisorseArt3_1() {
		return risorseArt3_1;
	}

	public void setRisorseArt3_2(Double risorseArt3_2) {
		this.risorseArt3_2 = risorseArt3_2;
	}

	public Double getRisorseArt3_2() {
		return risorseArt3_2;
	}

	public void setRisorseArt3_Succ(Double risorseArt3_Succ) {
		this.risorseArt3_Succ = risorseArt3_Succ;
	}

	public Double getRisorseArt3_Succ() {
		return risorseArt3_Succ;
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

	public void setRisorseImmobiliSucc(Double risorseImmobiliSucc) {
		this.risorseImmobiliSucc = risorseImmobiliSucc;
	}

	public Double getRisorseImmobiliSucc() {
		return risorseImmobiliSucc;
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

	public void setRisorseAltroSucc(Double risorseAltroSucc) {
		this.risorseAltroSucc = risorseAltroSucc;
	}

	public Double getRisorseAltroSucc() {
		return risorseAltroSucc;
	}

	public void setSpeseSostenute(Double speseSostenute) {
		this.speseSostenute = speseSostenute;
	}

	public Double getSpeseSostenute() {
		return speseSostenute;
	}

	public void setNote(String note) {
		this.note = StringUtils.stripToNull(note);
	}

	public String getNote() {
		return note;
	}

	public void setRup(DatiGeneraliTecnicoEntry rup) {
		this.rup = rup;
	}

	public DatiGeneraliTecnicoEntry getRup() {
		return rup;
	}

	public void setTipologiaCapitalePrivato(String tipologiaCapitalePrivato) {
		this.tipologiaCapitalePrivato = StringUtils.stripToNull(tipologiaCapitalePrivato);
	}

	public String getTipologiaCapitalePrivato() {
		return tipologiaCapitalePrivato;
	}

	public void setCodiceSoggettoDelegato(String codiceSoggettoDelegato) {
		this.codiceSoggettoDelegato = StringUtils.stripToNull(codiceSoggettoDelegato);
	}

	public String getCodiceSoggettoDelegato() {
		return codiceSoggettoDelegato;
	}

	public void setNomeSoggettoDelegato(String nomeSoggettoDelegato) {
		this.nomeSoggettoDelegato = StringUtils.stripToNull(nomeSoggettoDelegato);
	}

	public String getNomeSoggettoDelegato() {
		return nomeSoggettoDelegato;
	}

	public void setVariato(Long variato) {
		this.variato = variato;
	}

	public Long getVariato() {
		return variato;
	}

	public void setCuiCollegato(String cuiCollegato) {
		this.cuiCollegato = StringUtils.stripToNull(cuiCollegato);
	}

	public String getCuiCollegato() {
		return cuiCollegato;
	}

	public void setQuantita(Double quantita) {
		this.quantita = quantita;
	}

	public Double getQuantita() {
		return quantita;
	}

	public void setUnitaMisura(Long unitaMisura) {
		this.unitaMisura = unitaMisura;
	}

	public Long getUnitaMisura() {
		return unitaMisura;
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

	public void setCodiceRup(String codiceRup) {
		this.codiceRup = StringUtils.stripToNull(codiceRup);
	}

	@XmlTransient
	public String getCodiceRup() {
		return codiceRup;
	}

	public void setDelega(String delega) {
		this.delega = StringUtils.stripToNull(delega);
	}

	public String getDelega() {
		return delega;
	}

	public void setMeseAvvioProcedura(Long meseAvvioProcedura) {
		this.meseAvvioProcedura = meseAvvioProcedura;
	}

	public Long getMeseAvvioProcedura() {
		return meseAvvioProcedura;
	}

	public void setImportoRisorseFinanziarie(Double importoRisorseFinanziarie) {
		this.importoRisorseFinanziarie = importoRisorseFinanziarie;
	}

	public Double getImportoRisorseFinanziarie() {
		return importoRisorseFinanziarie;
	}

	public void setImportoRisorseFinanziarieRegionali(
			Double importoRisorseFinanziarieRegionali) {
		this.importoRisorseFinanziarieRegionali = importoRisorseFinanziarieRegionali;
	}

	public Double getImportoRisorseFinanziarieRegionali() {
		return importoRisorseFinanziarieRegionali;
	}

	public void setImportoRisorseFinanziarieAltro(
			Double importoRisorseFinanziarieAltro) {
		this.importoRisorseFinanziarieAltro = importoRisorseFinanziarieAltro;
	}

	public Double getImportoRisorseFinanziarieAltro() {
		return importoRisorseFinanziarieAltro;
	}

	public void setDirezioneGenerale(String direzioneGenerale) {
		this.direzioneGenerale = StringUtils.stripToNull(direzioneGenerale);
	}

	public String getDirezioneGenerale() {
		return direzioneGenerale;
	}

	public void setStrutturaOperativa(String strutturaOperativa) {
		this.strutturaOperativa = StringUtils.stripToNull(strutturaOperativa);
	}

	public String getStrutturaOperativa() {
		return strutturaOperativa;
	}

	public void setReferenteDati(String referenteDati) {
		this.referenteDati = StringUtils.stripToNull(referenteDati);
	}

	public String getReferenteDati() {
		return referenteDati;
	}

	public void setDirigenteResponsabile(String dirigenteResponsabile) {
		this.dirigenteResponsabile = StringUtils.stripToNull(dirigenteResponsabile);
	}

	public String getDirigenteResponsabile() {
		return dirigenteResponsabile;
	}

	public void setProceduraAffidamento(Long proceduraAffidamento) {
		this.proceduraAffidamento = proceduraAffidamento;
	}

	public Long getProceduraAffidamento() {
		return proceduraAffidamento;
	}

	public void setAcquistoVerdi(Long acquistoVerdi) {
		this.acquistoVerdi = acquistoVerdi;
	}

	public Long getAcquistoVerdi() {
		return acquistoVerdi;
	}

	public void setNormativaRiferimento(String normativaRiferimento) {
		this.normativaRiferimento = StringUtils.stripToNull(normativaRiferimento);
	}

	public String getNormativaRiferimento() {
		return normativaRiferimento;
	}

	public void setOggettoVerdi(String oggettoVerdi) {
		this.oggettoVerdi = StringUtils.stripToNull(oggettoVerdi);
	}

	public String getOggettoVerdi() {
		return oggettoVerdi;
	}

	public void setCpvVerdi(String cpvVerdi) {
		this.cpvVerdi = StringUtils.stripToNull(cpvVerdi);
	}

	public String getCpvVerdi() {
		return cpvVerdi;
	}

	public void setImportoNettoIvaVerdi(Double importoNettoIvaVerdi) {
		this.importoNettoIvaVerdi = importoNettoIvaVerdi;
	}

	public Double getImportoNettoIvaVerdi() {
		return importoNettoIvaVerdi;
	}

	public void setImportoIvaVerdi(Double importoIvaVerdi) {
		this.importoIvaVerdi = importoIvaVerdi;
	}

	public Double getImportoIvaVerdi() {
		return importoIvaVerdi;
	}

	public void setImportoTotVerdi(Double importoTotVerdi) {
		this.importoTotVerdi = importoTotVerdi;
	}

	public Double getImportoTotVerdi() {
		return importoTotVerdi;
	}

	public void setAcquistoMaterialiRiciclati(Long acquistoMaterialiRiciclati) {
		this.acquistoMaterialiRiciclati = acquistoMaterialiRiciclati;
	}

	public Long getAcquistoMaterialiRiciclati() {
		return acquistoMaterialiRiciclati;
	}

	public void setOggettoMatRic(String oggettoMatRic) {
		this.oggettoMatRic = StringUtils.stripToNull(oggettoMatRic);
	}

	public String getOggettoMatRic() {
		return oggettoMatRic;
	}

	public void setCpvMatRic(String cpvMatRic) {
		this.cpvMatRic = StringUtils.stripToNull(cpvMatRic);
	}

	public String getCpvMatRic() {
		return cpvMatRic;
	}

	public void setImportoNettoIvaMatRic(Double importoNettoIvaMatRic) {
		this.importoNettoIvaMatRic = importoNettoIvaMatRic;
	}

	public Double getImportoNettoIvaMatRic() {
		return importoNettoIvaMatRic;
	}

	public void setImportoIvaMatRic(Double importoIvaMatRic) {
		this.importoIvaMatRic = importoIvaMatRic;
	}

	public Double getImportoIvaMatRic() {
		return importoIvaMatRic;
	}

	public void setImportoTotMatRic(Double importoTotMatRic) {
		this.importoTotMatRic = importoTotMatRic;
	}

	public Double getImportoTotMatRic() {
		return importoTotMatRic;
	}

	public void setImportoIva1(Double importoIva1) {
		this.importoIva1 = importoIva1;
	}

	public Double getImportoIva1() {
		return importoIva1;
	}

	public void setImportoIva2(Double importoIva2) {
		this.importoIva2 = importoIva2;
	}

	public Double getImportoIva2() {
		return importoIva2;
	}

	public void setImportoIvaSucc(Double importoIvaSucc) {
		this.importoIvaSucc = importoIvaSucc;
	}

	public Double getImportoIvaSucc() {
		return importoIvaSucc;
	}

	public void setCoperturaFinanziaria(String coperturaFinanziaria) {
		this.coperturaFinanziaria = StringUtils.stripToNull(coperturaFinanziaria);
	}

	public String getCoperturaFinanziaria() {
		return coperturaFinanziaria;
	}

	public void setValutazione(Long valutazione) {
		this.valutazione = valutazione;
	}

	public Long getValutazione() {
		return valutazione;
	}

	@XmlTransient
	public double getImportoCapitalePrivato() {
		double totale = 0;
		totale += (risorsePrivati1 == null)?0:risorsePrivati1;
		totale += (risorsePrivati2 == null)?0:risorsePrivati2;
		totale += (risorsePrivatiSucc == null)?0:risorsePrivatiSucc;
		return totale;
	}
	
	@XmlTransient
	public double getImportoTotale() {
		double totale = 0;
		totale += (risorseAltro1 == null)?0:risorseAltro1;
		totale += (risorseAltro2 == null)?0:risorseAltro2;
		totale += (risorseAltroSucc == null)?0:risorseAltroSucc;
		
		totale += (risorseArt3_1 == null)?0:risorseArt3_1;
		totale += (risorseArt3_2 == null)?0:risorseArt3_2;
		totale += (risorseArt3_Succ == null)?0:risorseArt3_Succ;
		
		totale += (risorseBilancio1 == null)?0:risorseBilancio1;
		totale += (risorseBilancio2 == null)?0:risorseBilancio2;
		totale += (risorseBilancioSucc == null)?0:risorseBilancioSucc;
		
		totale += (risorseImmobili1 == null)?0:risorseImmobili1;
		totale += (risorseImmobili2 == null)?0:risorseImmobili2;
		totale += (risorseImmobiliSucc == null)?0:risorseImmobiliSucc;
		
		totale += (risorseMutuo1 == null)?0:risorseMutuo1;
		totale += (risorseMutuo2 == null)?0:risorseMutuo2;
		totale += (risorseMutuoSucc == null)?0:risorseMutuoSucc;
		
		totale += (risorsePrivati1 == null)?0:risorsePrivati1;
		totale += (risorsePrivati2 == null)?0:risorsePrivati2;
		totale += (risorsePrivatiSucc == null)?0:risorsePrivatiSucc;
		
		totale += (risorseVincolatePerLegge1 == null)?0:risorseVincolatePerLegge1;
		totale += (risorseVincolatePerLegge2 == null)?0:risorseVincolatePerLegge2;
		totale += (risorseVincolatePerLeggeSucc == null)?0:risorseVincolatePerLeggeSucc;
		
		totale += (speseSostenute == null)?0:speseSostenute;
		
		return totale;
	}
}
