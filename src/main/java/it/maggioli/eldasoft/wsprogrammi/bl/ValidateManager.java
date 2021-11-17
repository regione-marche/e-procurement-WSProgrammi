/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wsprogrammi.bl;

import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.tabellati.dao.TabellatiMapper;
import it.maggioli.eldasoft.tabellati.vo.TabellatoEntry;
import it.maggioli.eldasoft.wsprogrammi.dao.SqlMapper;
import it.maggioli.eldasoft.wsprogrammi.vo.ControlloEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.EsitoResult;
import it.maggioli.eldasoft.wsprogrammi.vo.ValidateEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.DettaglioInterventoAcquistoResult;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.InterventiAcquistiResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.FornitureServiziDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.ImmobileDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.InterventoDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.LavoroEconomiaEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.PubblicaProgrammaDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.AcquistoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.AcquistoNonRipropostoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.DettaglioFornitureServiziResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.PubblicaProgrammaFornitureServiziEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.AltriDatiOperaIncompiutaEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.DettaglioLavoriResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.ImmobileEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.InterventoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.InterventoNonRipropostoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.OperaIncompiutaEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.PubblicaProgrammaLavoriEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manager per la gestione della business logic.
 *
 * @author Mirco.Franzoni
 */
@Component(value = "validateManager")
public class ValidateManager {

	private static final String CONFIG_CODICE_APP = "W9";
	private static final String CONFIG_CHIAVE_APP = "it.maggioli.eldasoft.wslogin.jwtKey";

	/** Logger di classe. */
	private Logger             logger = LoggerFactory.getLogger(ValidateManager.class);

	@Autowired
	private TabellatiMapper tabellatiMapper;

	@Autowired
	private SqlMapper sqlMapper;
	
	/**
	 * @param tabellatiMapper
	 *        tabellatiMapper da settare internamente alla classe.
	 */
	public void setTabellatiMapper(TabellatiMapper tabellatiMapper) {
		this.tabellatiMapper = tabellatiMapper;
	}
  
	/**
	 * @param sqlMapper
	 *            sqlMapper da settare internamente alla classe.
	 */
	public void setSqlMapper(SqlMapper sqlMapper) {
		this.sqlMapper = sqlMapper;
	}
	
	public String getJWTKey() throws CriptazioneException {

		String criptedKey = this.sqlMapper.getConfigValue(CONFIG_CODICE_APP,
				CONFIG_CHIAVE_APP);
		try {
			ICriptazioneByte decript = FactoryCriptazioneByte.getInstance(
					FactoryCriptazioneByte.CODICE_CRIPTAZIONE_LEGACY, 	 
					criptedKey.getBytes(),
            		ICriptazioneByte.FORMATO_DATO_CIFRATO);
			
			return new String(decript.getDatoNonCifrato());
		} catch (CriptazioneException e) {
			logger.error("Errore in fase di decrypt della chiave hash per generazione token",e);
			throw e;
		}
	}
	
	public void validatePubblicaProgrammaDM112011(PubblicaProgrammaDM112011Entry programma, List<ValidateEntry> controlli) throws IOException {
		
		if (programma.getIdRicevuto() != null) {
			//l'avviso è già stato inviato devo verificare se esiste nel DB
			int i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto());
			if (i == 0){
				ValidateEntry item = new ValidateEntry("idRicevuto", "L'id programma indicato non esiste nella banca dati");
				controlli.add(item);
			} else {
				//se esiste verifico che idclient sia uguale
				i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto() + " and ID_CLIENT='" + programma.getClientId() + "'");
				if (i == 0){
					ValidateEntry item = new ValidateEntry("idRicevuto", "L'id programma indicato esiste già per un'altra utenza");
					controlli.add(item);
				}
			}
		}

		if (programma.getId() != null && programma.getId().length()>0) {
			if (programma.getId().length()<20 || programma.getId().length()>25) {
				ValidateEntry item = new ValidateEntry("id", "Il numero di caratteri non conforme alle specifiche");
				controlli.add(item);
			}
			//verifico se esiste già un programma per l'amministrazione dello stesso anno e tipo
			/*String sqlProgrammaGiaEsiste = "PIATRI WHERE ID='" + programma.getId() + "'";
			if (programma.getIdRicevuto() != null) {
				sqlProgrammaGiaEsiste += " AND ID_GENERATO <> " + programma.getIdRicevuto();
			}
			int i = sqlMapper.count(sqlProgrammaGiaEsiste);
			if (i > 0){
				ValidateEntry item = new ValidateEntry("id", "Il programma con id " + programma.getId() + "esiste già nella banca dati e non è possibile proseguire con l'aggiornamento");
				controlli.add(item);
			}*/
		} else {
			ValidateEntry item = new ValidateEntry("id", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (programma.getCodiceFiscaleSA() != null) {
			int i = 0;
			if (programma.getCodiceUnitaOrganizzativa() != null) {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
			} else {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
			}
			if (i == 0){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "La stazione appaltante indicata non esiste nell'archivio di destinazione");
				controlli.add(item);
			} else if (i > 1){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Esistono piu' stazioni appaltanti con lo stesso codice fiscale e codice unita' organizzativa. Contattare l'amministratore");
				controlli.add(item);
			} else {
				//controllo solo in inserimento
				//se esiste la stazione appaltante e la gara deve essere ancora inserita verifico se c'è l'aasociazione nell'usrein
				//oppure l'tente è "Amministrazione parametri di sistema"
				//ricavo il codice della Stazione appaltante
				String codiceSA = "";
				if (programma.getCodiceUnitaOrganizzativa() != null) {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
				} else {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
				}
				int usrEinExist = sqlMapper.count("USR_EIN WHERE SYSCON = " + programma.getSyscon() + " AND CODEIN = '" + codiceSA + "'");
				int isSuperUser = sqlMapper.count("USRSYS WHERE SYSCON = " + programma.getSyscon() + " AND syspwbou LIKE '%ou89|%'");
				if (usrEinExist == 0 && isSuperUser == 0) {
					ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Non si dispone delle credenziali per la Stazione Appaltante indicata");
					controlli.add(item);
				}
				//verifico se esiste già un programma per l'amministrazione dello stesso anno e tipo
				String sqlProgrammaGiaEsiste = "PIATRI WHERE ID='" + programma.getId() + "' AND CENINT='" + codiceSA + "'";
				if (programma.getIdRicevuto() != null) {
					sqlProgrammaGiaEsiste += " AND ID_GENERATO <> " + programma.getIdRicevuto();
				}
				int j = sqlMapper.count(sqlProgrammaGiaEsiste);
				if (j > 0){
					ValidateEntry item = new ValidateEntry("id", "Il programma con id " + programma.getId() + "esiste già nella banca dati e non è possibile proseguire con l'aggiornamento");
					controlli.add(item);
				}
			}
		} else {
			ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Valorizzare il campo");
			controlli.add(item);
		}

		if (programma.getAnno() == null) {
			ValidateEntry item = new ValidateEntry("anno", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (programma.getDescrizione() != null && programma.getDescrizione().length()>0) {
			if (programma.getDescrizione().length()>500) {
				ValidateEntry item = new ValidateEntry("descrizione", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("descrizione", "Valorizzare il campo");
			controlli.add(item);
		}
		
		//Dati Approvazione e Adozione 
		if (programma.getNumeroApprovazione() != null && programma.getNumeroApprovazione().length()>0) {
			if (programma.getNumeroApprovazione().length()>50) {
				ValidateEntry item = new ValidateEntry("numeroApprovazione", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		} 
		if (programma.getNumeroAdozione() != null && programma.getNumeroAdozione().length()>0) {
			if (programma.getNumeroAdozione().length()>50) {
				ValidateEntry item = new ValidateEntry("numeroAdozione", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		} 
		/*if (programma.getDataPubblicazioneApprovazione() == null) {
			programma.setDataPubblicazioneApprovazione(new Date());
		}
		if (programma.getDataPubblicazioneAdozione()== null) {
			programma.setDataPubblicazioneAdozione(new Date());
		}*/
		if (programma.getDataApprovazione() == null && programma.getTitoloAttoApprovazione() == null && programma.getUrlAttoApprovazione() == null &&
				programma.getDataAdozione() == null && programma.getTitoloAttoAdozione() == null && programma.getUrlAttoAdozione() == null) {
			ValidateEntry item = new ValidateEntry("Adozione/Approvazione", "Dati adozione o approvazione obbligatori");
			controlli.add(item);
		} else {
			if (programma.getDataApprovazione() != null && programma.getTitoloAttoApprovazione() != null && programma.getUrlAttoApprovazione() != null) {
				if (programma.getTitoloAttoApprovazione().length()>250) {
					ValidateEntry item = new ValidateEntry("titoloAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
					controlli.add(item);
				}
				if (programma.getUrlAttoApprovazione().length()>2000) {
					ValidateEntry item = new ValidateEntry("urlAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
					controlli.add(item);
				}
			} else if (programma.getDataAdozione() != null && programma.getTitoloAttoAdozione() != null && programma.getUrlAttoAdozione() != null) {
				if (programma.getTitoloAttoAdozione().length()>250) {
					ValidateEntry item = new ValidateEntry("titoloAttoAdozione", "Il numero di caratteri eccede la lunghezza massima");
					controlli.add(item);
				}
				if (programma.getUrlAttoAdozione().length()>2000) {
					ValidateEntry item = new ValidateEntry("urlAttoAdozione", "Il numero di caratteri eccede la lunghezza massima");
					controlli.add(item);
				}
			} else if (programma.getDataApprovazione() == null || programma.getTitoloAttoApprovazione() == null || programma.getUrlAttoApprovazione() == null) {
				if (programma.getDataApprovazione() == null) {
					ValidateEntry item = new ValidateEntry("dataApprovazione", "Valorizzare il campo");
					controlli.add(item);
				}
				if (programma.getTitoloAttoApprovazione() != null && programma.getTitoloAttoApprovazione().length()>0) {
					if (programma.getTitoloAttoApprovazione().length()>250) {
						ValidateEntry item = new ValidateEntry("titoloAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
						controlli.add(item);
					}
				} else {
					ValidateEntry item = new ValidateEntry("titoloAttoApprovazione", "Valorizzare il campo");
					controlli.add(item);
				}
				
				if (programma.getUrlAttoApprovazione() != null && programma.getUrlAttoApprovazione().length()>0) {
					if (programma.getUrlAttoApprovazione().length()>2000) {
						ValidateEntry item = new ValidateEntry("urlAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
						controlli.add(item);
					}
				} else {
					ValidateEntry item = new ValidateEntry("urlAttoApprovazione", "Valorizzare il campo");
					controlli.add(item);
				}
			} else if (programma.getDataAdozione() == null || programma.getTitoloAttoAdozione() == null || programma.getUrlAttoAdozione() == null) {
				if (programma.getDataAdozione() == null) {
					ValidateEntry item = new ValidateEntry("dataAdozione", "Valorizzare il campo");
					controlli.add(item);
				}
				if (programma.getTitoloAttoAdozione() != null && programma.getTitoloAttoAdozione().length()>0) {
					if (programma.getTitoloAttoAdozione().length()>250) {
						ValidateEntry item = new ValidateEntry("titoloAttoAdozione", "Il numero di caratteri eccede la lunghezza massima");
						controlli.add(item);
					}
				} else {
					ValidateEntry item = new ValidateEntry("titoloAttoAdozione", "Valorizzare il campo");
					controlli.add(item);
				}
				
				if (programma.getUrlAttoAdozione() != null && programma.getUrlAttoAdozione().length()>0) {
					if (programma.getUrlAttoAdozione().length()>2000) {
						ValidateEntry item = new ValidateEntry("urlAttoAdozione", "Il numero di caratteri eccede la lunghezza massima");
						controlli.add(item);
					}
				} else {
					ValidateEntry item = new ValidateEntry("urlAttoAdozione", "Valorizzare il campo");
					controlli.add(item);
				}
			}
		}
		
		if (programma.getNote1() != null && programma.getNote1().length()>2000) {
			ValidateEntry item = new ValidateEntry("note1", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getNote2() != null && programma.getNote2().length()>2000) {
			ValidateEntry item = new ValidateEntry("note2", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getNote2b() != null && programma.getNote2b().length()>2000) {
			ValidateEntry item = new ValidateEntry("note2b", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getNote3() != null && programma.getNote3().length()>2000) {
			ValidateEntry item = new ValidateEntry("note3", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getNote4() != null && programma.getNote4().length()>2000) {
			ValidateEntry item = new ValidateEntry("note4", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getReferente() == null) {
			ValidateEntry item = new ValidateEntry("referente", "Valorizzare il campo");
			controlli.add(item);
		} else {
			this.validatePubblicaTecnico(programma.getReferente(), "responsabile programma", controlli);
		}
		
		//validate Lavori economia
		int coneco = 1;
		if (programma.getLavoriEconomia() != null) {
			for(LavoroEconomiaEntry lavoro:programma.getLavoriEconomia()) {
				lavoro.setConeco(new Long(coneco));
				this.validateLavoroEconomia(lavoro, controlli);
				coneco++;
			}
		}
		
		//validate interventi lavori
		int conint = 1;
		if (programma.getInterventi() != null) {
			for(InterventoDM112011Entry intervento:programma.getInterventi()) {
				intervento.setConint(new Long(conint));
				this.validateInterventoDM112011(intervento, controlli);
				conint++;
			}
		}
		//validate interventi forniture e servizi
		conint = 1;
		if (programma.getFornitureEServizi() != null) {
			for(FornitureServiziDM112011Entry fornserv:programma.getFornitureEServizi()) {
				fornserv.setConint(new Long(conint));
				this.validateFornitureServiziDM112011(fornserv, controlli);
				conint++;
			}
		}
	}

	private void validateLavoroEconomia(LavoroEconomiaEntry lavoro, List<ValidateEntry> controlli) throws IOException {
		if (lavoro.getCup() != null && lavoro.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("lavoro economia nr " + lavoro.getConeco() + ".cup", "Valore non valido");
			controlli.add(item);
		}
		if (lavoro.getDescrizione() != null && lavoro.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("lavoro economia nr " + lavoro.getConeco() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
	}
	
	private void validateInterventoDM112011(InterventoDM112011Entry intervento, List<ValidateEntry> controlli) throws IOException {
		
		if (intervento.getNumeroProgressivo() == null) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".numeroProgressivo", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (intervento.getDescrizione() == null || intervento.getDescrizione().length() == 0) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (intervento.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getAnnualitaRiferimento() == null) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".annualitaRiferimento", "Valorizzare il campo");
			controlli.add(item);
		} else if (intervento.getAnnualitaRiferimento() < 1 || intervento.getAnnualitaRiferimento() > 3) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".annualitaRiferimento", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getCodiceInterno() != null && intervento.getCodiceInterno().length()>20) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".CodiceInterno", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getTipologia() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValoreTabsche1("S2008", intervento.getTipologia());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Tipologia", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getCategoria() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValoreCategoriaDM112011("S2005", intervento.getCategoria());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Categoria", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getSubCategoria() != null && intervento.getSubCategoria().length()>2) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".SubCategoria", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
	
		if (intervento.getIstat() != null && intervento.getIstat().length()!=9) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Istat", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getNuts() != null && intervento.getNuts().length()>5) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Nuts", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getEsenteCup() != null && !intervento.getEsenteCup().equals("1") && !intervento.getEsenteCup().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".EsenteCup", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getCup() != null && intervento.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getCpv() != null && intervento.getCpv().length()>20) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Cpv", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getAnnuale() != null && !intervento.getAnnuale().equals("1") && !intervento.getAnnuale().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Annuale", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getFinalita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValoreTabsche1("S2016", intervento.getFinalita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Finalita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getPriorita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT008", intervento.getPriorita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".Priorita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getConformitaAmbientale() != null && !intervento.getConformitaAmbientale().equals("1") && !intervento.getConformitaAmbientale().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".ConformitaAmbientale", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getConformitaUrbanistica() != null && !intervento.getConformitaUrbanistica().equals("1") && !intervento.getConformitaUrbanistica().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".ConformitaUrbanistica", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getStatoProgettazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValoreTabsche1("S2017", intervento.getStatoProgettazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".StatoProgettazione", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getTrimestreInizioLavori() != null && (intervento.getTrimestreInizioLavori() < 1 || intervento.getTrimestreInizioLavori() > 4)) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".TrimestreInizioLavori", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getTrimestreFineLavori() != null && (intervento.getTrimestreFineLavori() < 1 || intervento.getTrimestreFineLavori() > 4)) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".TrimestreFineLavori", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getTipologiaCapitalePrivato() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValoreTabsche1("S2015", intervento.getTipologiaCapitalePrivato());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getNumeroProgressivo() + ".TipologiaCapitalePrivato", "Valore non valido");
				controlli.add(item);
			}
		}
		
		//validate Rup
		if (intervento.getRup() != null) {
			this.validatePubblicaTecnico(intervento.getRup(), "intervento lavori nr " + intervento.getNumeroProgressivo() + ".Rup" , controlli);
		}

		//validate Immmobili
		int numimm = 1;
		if(intervento.getImmobili() != null) {
			for(ImmobileDM112011Entry immobile:intervento.getImmobili()) {
				immobile.setConint(intervento.getConint());
				immobile.setNumimm(new Long(numimm));
				this.validateImmobileDM112011(immobile, intervento, controlli);
				numimm++;
			}
		}
	}
	
	private void validateFornitureServiziDM112011(FornitureServiziDM112011Entry intervento, List<ValidateEntry> controlli) throws IOException {
		if (intervento.getDescrizione() == null || intervento.getDescrizione().length() == 0) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (intervento.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getCodiceInterno() != null && intervento.getCodiceInterno().length()>20) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".CodiceInterno", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
	
		if (intervento.getIstat() != null && intervento.getIstat().length()!=9) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Istat", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getNuts() != null && intervento.getNuts().length()>5) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Nuts", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getEsenteCup() != null && !intervento.getEsenteCup().equals("1") && !intervento.getEsenteCup().equals("2")) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".EsenteCup", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getCup() != null && intervento.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getCpv() != null && intervento.getCpv().length()>20) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Cpv", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getSettore() == null) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Settore", "Valorizzare il campo");
			controlli.add(item);
		} else if (!intervento.getSettore().equals("F") && !intervento.getSettore().equals("S")) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Settore", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getPriorita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT008", intervento.getPriorita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".Priorita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getNormativaRiferimento() != null && intervento.getNormativaRiferimento().length()>200) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".NormativaRiferimento", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getStrumentoProgrammazione() != null && intervento.getStrumentoProgrammazione().length()>100) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".StrumentoProgrammazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getPrevistaManodopera() != null && !intervento.getPrevistaManodopera().equals("1") && !intervento.getPrevistaManodopera().equals("2")) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".PrevistaManodopera", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getMeseAvvioProcedura() != null && (intervento.getMeseAvvioProcedura().compareTo(new Long(1))<0 || intervento.getMeseAvvioProcedura().compareTo(new Long(12))>0)) {
			ValidateEntry item = new ValidateEntry("forniture e servizi nr " + intervento.getConint() + ".MeseAvvioProcedura", "Valore non valido");
			controlli.add(item);
		} 
		//validate Rup
		if (intervento.getRup() != null) {
			this.validatePubblicaTecnico(intervento.getRup(), "forniture e servizi nr " + intervento.getConint() + ".Rup" , controlli);
		}

	}
	
	private void validateImmobileDM112011(ImmobileDM112011Entry immobile, InterventoDM112011Entry intervento, List<ValidateEntry> controlli) throws IOException {
		if (immobile.getDescrizione() != null && immobile.getDescrizione().length()>400) {
			ValidateEntry item = new ValidateEntry("immobile nr " + immobile.getNumimm() + " - intervento nr " + intervento.getNumeroProgressivo() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (immobile.getTipoProprieta() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("A2137", immobile.getTipoProprieta());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile nr " + immobile.getNumimm() + " - intervento nr " + intervento.getNumeroProgressivo() + ".TipoProprieta", "Valore non valido");
				controlli.add(item);
			}
		}
	}
	

	public void validatePubblicaProgrammaLavori(PubblicaProgrammaLavoriEntry programma, List<ValidateEntry> controlli) throws IOException {
		
		if (programma.getIdRicevuto() != null) {
			//l'avviso è già stato inviato devo verificare se esiste nel DB
			int i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto());
			if (i == 0){
				ValidateEntry item = new ValidateEntry("idRicevuto", "L'id programma indicato non esiste nella banca dati");
				controlli.add(item);
			} else {
				//se esiste verifico che idclient sia uguale
				i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto() + " and ID_CLIENT='" + programma.getClientId() + "'");
				if (i == 0){
					ValidateEntry item = new ValidateEntry("idRicevuto", "L'id programma indicato esiste già per un'altra utenza");
					controlli.add(item);
				}
			}
		}

		if (programma.getId() != null && programma.getId().length()>0) {
			if (programma.getId().length()<20 || programma.getId().length()>25) {
				ValidateEntry item = new ValidateEntry("id", "Il numero di caratteri non conforme alle specifiche");
				controlli.add(item);
			}
			//verifico se esiste già un programma per l'amministrazione dello stesso anno e tipo
			/*String sqlProgrammaGiaEsiste = "PIATRI WHERE ID='" + programma.getId() + "'";
			if (programma.getIdRicevuto() != null) {
				sqlProgrammaGiaEsiste += " AND ID_GENERATO <> " + programma.getIdRicevuto();
			}
			int i = sqlMapper.count(sqlProgrammaGiaEsiste);
			if (i > 0){
				ValidateEntry item = new ValidateEntry("id", "Il programma con id " + programma.getId() + "esiste già nella banca dati e non è possibile proseguire con l'aggiornamento");
				controlli.add(item);
			}*/
		} else {
			ValidateEntry item = new ValidateEntry("id", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (programma.getCodiceFiscaleSA() != null) {
			int i = 0;
			if (programma.getCodiceUnitaOrganizzativa() != null) {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
			} else {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
			}
			if (i == 0){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "La stazione appaltante indicata non esiste nell'archivio di destinazione");
				controlli.add(item);
			} else if (i > 1){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Esistono pìu stazioni appaltanti con lo stesso codice fiscale. Contattare l'amministratore");
				controlli.add(item);
			} else {
				//controllo solo in inserimento
				//se esiste la stazione appaltante e la gara deve essere ancora inserita verifico se c'è l'aasociazione nell'usrein
				//oppure l'tente è "Amministrazione parametri di sistema"
				//ricavo il codice della Stazione appaltante
				String codiceSA = "";
				if (programma.getCodiceUnitaOrganizzativa() != null) {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
				} else {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
				}
				int usrEinExist = sqlMapper.count("USR_EIN WHERE SYSCON = " + programma.getSyscon() + " AND CODEIN = '" + codiceSA + "'");
				int isSuperUser = sqlMapper.count("USRSYS WHERE SYSCON = " + programma.getSyscon() + " AND syspwbou LIKE '%ou89|%'");
				if (usrEinExist == 0 && isSuperUser == 0) {
					ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Non si dispone delle credenziali per la Stazione Appaltante indicata");
					controlli.add(item);
				}
				//verifico se esiste già un programma per l'amministrazione dello stesso anno e tipo
				String sqlProgrammaGiaEsiste = "PIATRI WHERE ID='" + programma.getId() + "' AND CENINT='" + codiceSA + "'";
				if (programma.getIdRicevuto() != null) {
					sqlProgrammaGiaEsiste += " AND ID_GENERATO <> " + programma.getIdRicevuto();
				}
				int j = sqlMapper.count(sqlProgrammaGiaEsiste);
				if (j > 0){
					ValidateEntry item = new ValidateEntry("id", "Il programma con id " + programma.getId() + "esiste già nella banca dati e non è possibile proseguire con l'aggiornamento");
					controlli.add(item);
				}
			}
		} else {
			ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Valorizzare il campo");
			controlli.add(item);
		}

		//Ufficio
		if (programma.getUfficio() != null && programma.getUfficio().length()>250) {
			ValidateEntry item = new ValidateEntry("ufficio", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		} 
		
		if (programma.getAnno() == null) {
			ValidateEntry item = new ValidateEntry("anno", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (programma.getDescrizione() != null && programma.getDescrizione().length()>0) {
			if (programma.getDescrizione().length()>500) {
				ValidateEntry item = new ValidateEntry("descrizione", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("descrizione", "Valorizzare il campo");
			controlli.add(item);
		}
		
		//Dati Approvazione e Adozione 
		//Dati Approvazione e Adozione 
		if (programma.getNumeroApprovazione() != null && programma.getNumeroApprovazione().length()>50) {
			ValidateEntry item = new ValidateEntry("numeroApprovazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		} 
		
		if (programma.getTitoloAttoApprovazione() != null && programma.getTitoloAttoApprovazione().length()>250) {
			ValidateEntry item = new ValidateEntry("titoloAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getUrlAttoApprovazione() != null && programma.getUrlAttoApprovazione().length()>2000) {
			ValidateEntry item = new ValidateEntry("urlAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getNumeroAdozione() != null && programma.getNumeroAdozione().length()>50) {
			ValidateEntry item = new ValidateEntry("numeroAdozione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		} 
		
		if (programma.getTitoloAttoAdozione() != null && programma.getTitoloAttoAdozione().length()>250) {
			ValidateEntry item = new ValidateEntry("titoloAttoAdozione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getUrlAttoAdozione() != null && programma.getUrlAttoAdozione().length()>2000) {
			ValidateEntry item = new ValidateEntry("urlAttoAdozione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getReferente() == null) {
			ValidateEntry item = new ValidateEntry("referente", "Valorizzare il campo");
			controlli.add(item);
		} else {
			this.validatePubblicaTecnico(programma.getReferente(), "responsabile programma", controlli);
		}
		
		this.validateWSControlli("PUBBLICA_LAVORI", "PIATRI", programma, null, controlli);
		
		
		//validate opere incompiute
		int numoi = 1;
		if(programma.getOpereIncompiute() != null) {
			for(OperaIncompiutaEntry opera:programma.getOpereIncompiute()) {
				opera.setNumoi(new Long(numoi));
				this.validateOperaIncompiuta(opera, controlli);
				numoi++;
			}
		}
		
		//validate interventi
		int conint = 1;
		if(programma.getInterventi() != null) {
			for(InterventoEntry intervento:programma.getInterventi()) {
				intervento.setConint(new Long(conint));
				this.validateInterventoLavori(intervento, controlli);
				conint++;
			}
		}
		
		//validate interventi non riproposti
		conint = 1;
		if(programma.getInterventiNonRiproposti() != null) {
			for(InterventoNonRipropostoEntry interventoNonRiproposto:programma.getInterventiNonRiproposti()) {
				interventoNonRiproposto.setConint(new Long(conint));
				this.validateInterventoNonRiproposto(interventoNonRiproposto, controlli);
				conint++;
			}
		}
	}

	private void validateInterventoLavori(InterventoEntry intervento, List<ValidateEntry> controlli) throws IOException {
		
		if (intervento.getCui() == null ) {
			ValidateEntry item = new ValidateEntry("intervento lavori nr " + intervento.getConint() + ".Cui", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else if (intervento.getCui().length()>25) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getDescrizione() == null || intervento.getDescrizione().length() == 0) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (intervento.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getAnno() == null) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Anno", "Valorizzare il campo");
			controlli.add(item);
		} else if (intervento.getAnno() < 1 || intervento.getAnno() > 3) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Anno", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getCodiceInterno() != null && intervento.getCodiceInterno().length()>20) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".CodiceInterno", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getEsenteCup() != null && !intervento.getEsenteCup().equals("1") && !intervento.getEsenteCup().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".EsenteCup", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getCup() != null && intervento.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (intervento.getCpv() != null && intervento.getCpv().length()>20) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Cpv", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + intervento.getIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Istat", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getNuts() != null) {
			int i = sqlMapper.count("TABNUTS WHERE CODICE='" + intervento.getNuts() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Nuts", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getPriorita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT008", intervento.getPriorita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Priorita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getLottoFunzionale() != null && !intervento.getLottoFunzionale().equals("1") && !intervento.getLottoFunzionale().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".LottoFunzionale", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getLavoroComplesso() != null && !intervento.getLavoroComplesso().equals("1") && !intervento.getLavoroComplesso().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".LavoroComplesso", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getTipologia() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx01", intervento.getTipologia());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Tipologia", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getCategoria() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore5("PTj01", intervento.getCategoria());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Categoria", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getFinalita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx03", intervento.getFinalita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Finalita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getConformitaAmbientale() != null && !intervento.getConformitaAmbientale().equals("1") && !intervento.getConformitaAmbientale().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".ConformitaAmbientale", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getConformitaUrbanistica() != null && !intervento.getConformitaUrbanistica().equals("1") && !intervento.getConformitaUrbanistica().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".ConformitaUrbanistica", "Valore non valido");
			controlli.add(item);
		} 
	
		if (intervento.getStatoProgettazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx09", intervento.getStatoProgettazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".StatoProgettazione", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getTipologiaCapitalePrivato() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx08", intervento.getTipologiaCapitalePrivato());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".TipologiaCapitalePrivato", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getDelega() != null && !intervento.getDelega().equals("1") && !intervento.getDelega().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Delega", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getCodiceSoggettoDelegato() != null && intervento.getCodiceSoggettoDelegato().length()>20) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".CodiceSoggettoDelegato", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getNomeSoggettoDelegato() != null && intervento.getNomeSoggettoDelegato().length()>160) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".NomeSoggettoDelegato", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getVariato() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT010", intervento.getVariato());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Variato", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getNote() != null && intervento.getNote().length()>2000) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Note", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (intervento.getMeseAvvioProcedura() != null && (intervento.getMeseAvvioProcedura().compareTo(new Long(1))<0 || intervento.getMeseAvvioProcedura().compareTo(new Long(12))>0)) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".MeseAvvioProcedura", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getDirezioneGenerale() != null && intervento.getDirezioneGenerale().length()>160) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".DirezioneGenerale", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getStrutturaOperativa() != null && intervento.getStrutturaOperativa().length()>160) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".StrutturaOperativa", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getReferenteDati() != null && intervento.getReferenteDati().length()>160) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".ReferenteDati", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getDirigenteResponsabile() != null && intervento.getDirigenteResponsabile().length()>160) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".DirigenteResponsabile", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getProceduraAffidamento() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT020", intervento.getProceduraAffidamento());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".ProceduraAffidamento", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getAcquistoVerdi() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT018", intervento.getAcquistoVerdi());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".AcquistoVerdi", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getNormativaRiferimento() != null && intervento.getNormativaRiferimento().length()>200) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".NormativaRiferimento", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getOggettoVerdi() != null && intervento.getOggettoVerdi().length()>500) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".OggettoVerdi", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getCpvVerdi() != null && intervento.getCpvVerdi().length()>12) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".CpvVerdi", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getAcquistoMaterialiRiciclati() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT018", intervento.getAcquistoMaterialiRiciclati());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".AcquistoMaterialiRiciclati", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (intervento.getOggettoMatRic() != null && intervento.getOggettoMatRic().length()>500) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".OggettoMatRic", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getCpvMatRic() != null && intervento.getCpvMatRic().length()>12) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".CpvMatRic", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (intervento.getCoperturaFinanziaria() != null && !intervento.getCoperturaFinanziaria().equals("1") && !intervento.getCoperturaFinanziaria().equals("2")) {
			ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".CoperturaFinanziaria", "Valore non valido");
			controlli.add(item);
		} 
		
		if (intervento.getValutazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT021", intervento.getValutazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento lavori " + intervento.getCui() + ".Valutazione", "Valore non valido");
				controlli.add(item);
			}
		}
		
		//validate Rup
		if (intervento.getRup() != null) {
			this.validatePubblicaTecnico(intervento.getRup(), "intervento lavori " + intervento.getCui() + ".Rup" , controlli);
		}

		this.validateWSControlli("PUBBLICA_LAVORI", "INTTRI", intervento, null, controlli);
		//validate Immmobili
		int numimm = 1;
		if (intervento.getImmobili() != null) {
			for(ImmobileEntry immobile:intervento.getImmobili()) {
				immobile.setConint(intervento.getConint());
				immobile.setNumimm(new Long(numimm));
				this.validateImmobile(immobile, intervento, controlli);
				numimm++;
			}
		}
	}
	
	private void validateImmobile(ImmobileEntry immobile, InterventoEntry intervento, List<ValidateEntry> controlli) throws IOException {
		
		if (immobile.getCui() == null ) {
			ValidateEntry item = new ValidateEntry("immobile nr " + immobile.getNumimm() + ".Cui", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else if (immobile.getCui().length()>30) {
			ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (immobile.getIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + immobile.getIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Istat", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getNuts() != null) {
			int i = sqlMapper.count("TABNUTS WHERE CODICE='" + immobile.getNuts() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Nuts", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			} 
		}
		
		if (immobile.getTrasferimentoTitoloCorrispettivo() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT013", immobile.getTrasferimentoTitoloCorrispettivo());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".TrasferimentoTitoloCorrispettivo", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getImmobileDisponibile() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT014", immobile.getImmobileDisponibile());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".ImmobileDisponibile", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getAlienati() != null && !immobile.getAlienati().equals("1") && !immobile.getAlienati().equals("2")) {
			ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Alienati", "Valore non valido");
			controlli.add(item);
		} 
		
		if (immobile.getInclusoProgrammaDismissione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT015", immobile.getInclusoProgrammaDismissione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".InclusoProgrammaDismissione", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getDescrizione() != null && immobile.getDescrizione().length()>400) {
			ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (immobile.getTipoProprieta() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("A2137", immobile.getTipoProprieta());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".TipoProprieta", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getTipoDisponibilita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT016", immobile.getTipoDisponibilita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".TipoDisponibilita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		this.validateWSControlli("PUBBLICA_LAVORI", "IMMTRAI", immobile, null, controlli);
	}
	
	private void validateInterventoNonRiproposto(InterventoNonRipropostoEntry interventoNonRiproposto, List<ValidateEntry> controlli) throws IOException {
		if (interventoNonRiproposto.getCui() == null) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto nr " + interventoNonRiproposto.getConint() + ".Cui", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else if (interventoNonRiproposto.getCui().length()>25) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (interventoNonRiproposto.getCup() != null && interventoNonRiproposto.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Cup", "Valore non valido");
			controlli.add(item);
		}
		if (interventoNonRiproposto.getDescrizione() == null) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (interventoNonRiproposto.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (interventoNonRiproposto.getImporto() == null) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Importo", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (interventoNonRiproposto.getPriorita() == null) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Priorita", "Valorizzare il campo");
			controlli.add(item);
		} else {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT008", interventoNonRiproposto.getPriorita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Priorita", "Valore non valido");
				controlli.add(item);
			}
		}
		if (interventoNonRiproposto.getMotivo() == null) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Motivo", "Valorizzare il campo");
			controlli.add(item);
		} else if (interventoNonRiproposto.getMotivo().length()>2000) {
			ValidateEntry item = new ValidateEntry("intervento non riproposto " + interventoNonRiproposto.getCui() + ".Motivo", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		this.validateWSControlli("PUBBLICA_LAVORI", "INRTRI", interventoNonRiproposto, null, controlli);
	}
	
	public void validatePubblicaProgrammaFornitureServizi(PubblicaProgrammaFornitureServiziEntry programma, List<ValidateEntry> controlli) throws IOException {
		
		if (programma.getIdRicevuto() != null) {
			//l'avviso è già stato inviato devo verificare se esiste nel DB
			int i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto());
			if (i == 0){
				ValidateEntry item = new ValidateEntry("idRicevuto", "L'id programma indicato non esiste nella banca dati");
				controlli.add(item);
			} else {
				//se esiste verifico che idclient sia uguale
				i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto() + " and ID_CLIENT='" + programma.getClientId() + "'");
				if (i == 0){
					ValidateEntry item = new ValidateEntry("idRicevuto", "L'id programma indicato esiste già per un'altra utenza");
					controlli.add(item);
				}
			}
		}

		if (programma.getId() != null && programma.getId().length()>0) {
			if (programma.getId().length()<20 || programma.getId().length()>25) {
				ValidateEntry item = new ValidateEntry("id", "Il numero di caratteri non conforme alle specifiche");
				controlli.add(item);
			}
			//verifico se esiste già un programma per l'amministrazione dello stesso anno e tipo
			/*String sqlProgrammaGiaEsiste = "PIATRI WHERE ID='" + programma.getId() + "'";
			if (programma.getIdRicevuto() != null) {
				sqlProgrammaGiaEsiste += " AND ID_GENERATO <> " + programma.getIdRicevuto();
			}
			int i = sqlMapper.count(sqlProgrammaGiaEsiste);
			if (i > 0){
				ValidateEntry item = new ValidateEntry("id", "Il programma con id " + programma.getId() + "esiste già nella banca dati e non è possibile proseguire con l'aggiornamento");
				controlli.add(item);
			}*/
		} else {
			ValidateEntry item = new ValidateEntry("id", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (programma.getCodiceFiscaleSA() != null) {
			int i = 0;
			if (programma.getCodiceUnitaOrganizzativa() != null) {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
			} else {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
			}
			if (i == 0){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "La stazione appaltante indicata non esiste nell'archivio di destinazione");
				controlli.add(item);
			} else if (i > 1){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Esistono pìu stazioni appaltanti con lo stesso codice fiscale. Contattare l'amministratore");
				controlli.add(item);
			} else {
				//controllo solo in inserimento
				//se esiste la stazione appaltante e la gara deve essere ancora inserita verifico se c'è l'aasociazione nell'usrein
				//oppure l'tente è "Amministrazione parametri di sistema"
				//ricavo il codice della Stazione appaltante
				String codiceSA = "";
				if (programma.getCodiceUnitaOrganizzativa() != null) {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
				} else {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
				}
				int usrEinExist = sqlMapper.count("USR_EIN WHERE SYSCON = " + programma.getSyscon() + " AND CODEIN = '" + codiceSA + "'");
				int isSuperUser = sqlMapper.count("USRSYS WHERE SYSCON = " + programma.getSyscon() + " AND syspwbou LIKE '%ou89|%'");
				if (usrEinExist == 0 && isSuperUser == 0) {
					ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Non si dispone delle credenziali per la Stazione Appaltante indicata");
					controlli.add(item);
				}
				//verifico se esiste già un programma per l'amministrazione dello stesso anno e tipo
				String sqlProgrammaGiaEsiste = "PIATRI WHERE ID='" + programma.getId() + "' AND CENINT='" + codiceSA + "'";
				if (programma.getIdRicevuto() != null) {
					sqlProgrammaGiaEsiste += " AND ID_GENERATO <> " + programma.getIdRicevuto();
				}
				int j = sqlMapper.count(sqlProgrammaGiaEsiste);
				if (j > 0){
					ValidateEntry item = new ValidateEntry("id", "Il programma con id " + programma.getId() + "esiste già nella banca dati e non è possibile proseguire con l'aggiornamento");
					controlli.add(item);
				}
			}
		} else {
			ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Valorizzare il campo");
			controlli.add(item);
		}

		//Ufficio
		if (programma.getUfficio() != null && programma.getUfficio().length()>250) {
			ValidateEntry item = new ValidateEntry("ufficio", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		} 
		
		if (programma.getAnno() == null) {
			ValidateEntry item = new ValidateEntry("anno", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (programma.getDescrizione() != null && programma.getDescrizione().length()>0) {
			if (programma.getDescrizione().length()>500) {
				ValidateEntry item = new ValidateEntry("descrizione", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("descrizione", "Valorizzare il campo");
			controlli.add(item);
		}
		
		//Dati Approvazione e Adozione 
		if (programma.getNumeroApprovazione() != null && programma.getNumeroApprovazione().length()>50) {
			ValidateEntry item = new ValidateEntry("numeroApprovazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		} 
		
		if (programma.getTitoloAttoApprovazione() != null && programma.getTitoloAttoApprovazione().length()>250) {
			ValidateEntry item = new ValidateEntry("titoloAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getUrlAttoApprovazione() != null && programma.getUrlAttoApprovazione().length()>2000) {
			ValidateEntry item = new ValidateEntry("urlAttoApprovazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (programma.getReferente() == null) {
			ValidateEntry item = new ValidateEntry("referente", "Valorizzare il campo");
			controlli.add(item);
		} else {
			this.validatePubblicaTecnico(programma.getReferente(), "responsabile programma", controlli);
		}
		
		this.validateWSControlli("PUBBLICA_FS", "PIATRI", programma, null, controlli);
		//validate acquisti
		int conint = 1;
		if(programma.getAcquisti() != null) {
			for(AcquistoEntry acquisto:programma.getAcquisti()) {
				acquisto.setConint(new Long(conint));
				this.validateAcquisto(acquisto, controlli);
				conint++;
			}
		}
		
		//validate acquisti non riproposti
		conint = 1;
		if(programma.getAcquistiNonRiproposti() != null) {
			for(AcquistoNonRipropostoEntry acquistoNonRiproposto:programma.getAcquistiNonRiproposti()) {
				acquistoNonRiproposto.setConint(new Long(conint));
				this.validateAcquistoNonRiproposto(acquistoNonRiproposto, controlli);
				conint++;
			}
		}
	}

	private void validateAcquisto(AcquistoEntry acquisto, List<ValidateEntry> controlli) throws IOException {
		
		if (acquisto.getCui() == null) {
			ValidateEntry item = new ValidateEntry("acquisto nr " + acquisto.getConint() + ".Cui", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else if (acquisto.getCui().length()>25) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getSettore() == null) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Settore", "Valorizzare il campo");
			controlli.add(item);
		}
		else if (!acquisto.getSettore().equals("F") && !acquisto.getSettore().equals("S")) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Settore", "Valore non valido");
			controlli.add(item);
		} 
		
		if (acquisto.getDescrizione() == null || acquisto.getDescrizione().length() == 0) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (acquisto.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getAnno() == null) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Anno", "Valorizzare il campo");
			controlli.add(item);
		} else if (acquisto.getAnno() < 1 || acquisto.getAnno() > 3) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Anno", "Valore non valido");
			controlli.add(item);
		}
		
		if (acquisto.getCodiceInterno() != null && acquisto.getCodiceInterno().length()>20) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".CodiceInterno", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getEsenteCup() != null && !acquisto.getEsenteCup().equals("1") && !acquisto.getEsenteCup().equals("2")) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".EsenteCup", "Valore non valido");
			controlli.add(item);
		} 
		
		if (acquisto.getCup() != null && acquisto.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (acquisto.getAcquistoRicompreso() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT022", acquisto.getAcquistoRicompreso());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".AcquistoRicompreso", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getCuiCollegato() != null && acquisto.getCuiCollegato().length()>25) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".CuiCollegato", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (acquisto.getCpv() != null && acquisto.getCpv().length()>20) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Cpv", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + acquisto.getIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Istat", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getNuts()  != null) {
			int i = sqlMapper.count("TABNUTS WHERE CODICE='" + acquisto.getNuts() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Nuts", "Valore non valido");
				controlli.add(item);
			} 
		}
		
		if (acquisto.getUnitaMisura() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT019", acquisto.getUnitaMisura());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".UnitaMisura", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getPriorita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT008", acquisto.getPriorita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Priorita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getLottoFunzionale() != null && !acquisto.getLottoFunzionale().equals("1") && !acquisto.getLottoFunzionale().equals("2")) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".LottoFunzionale", "Valore non valido");
			controlli.add(item);
		} 
		
		if (acquisto.getNuovoAffidamento() != null && !acquisto.getNuovoAffidamento().equals("1") && !acquisto.getNuovoAffidamento().equals("2")) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".NuovoAffidamento", "Valore non valido");
			controlli.add(item);
		}
		
		if (acquisto.getTipologiaCapitalePrivato() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx08", acquisto.getTipologiaCapitalePrivato());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".TipologiaCapitalePrivato", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getDelega() != null && !acquisto.getDelega().equals("1") && !acquisto.getDelega().equals("2")) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Delega", "Valore non valido");
			controlli.add(item);
		} 
		
		if (acquisto.getCodiceSoggettoDelegato() != null && acquisto.getCodiceSoggettoDelegato().length()>20) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".CodiceSoggettoDelegato", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getNomeSoggettoDelegato() != null && acquisto.getNomeSoggettoDelegato().length()>160) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".NomeSoggettoDelegato", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getVariato() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT010", acquisto.getVariato());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Variato", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getNote() != null && acquisto.getNote().length()>2000) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Note", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (acquisto.getMeseAvvioProcedura() != null && (acquisto.getMeseAvvioProcedura().compareTo(new Long(1))<0 || acquisto.getMeseAvvioProcedura().compareTo(new Long(12))>0)) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".MeseAvvioProcedura", "Valore non valido");
			controlli.add(item);
		} 
		
		if (acquisto.getDirezioneGenerale() != null && acquisto.getDirezioneGenerale().length()>160) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".DirezioneGenerale", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getStrutturaOperativa() != null && acquisto.getStrutturaOperativa().length()>160) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".StrutturaOperativa", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getReferenteDati() != null && acquisto.getReferenteDati().length()>160) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".ReferenteDati", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getDirigenteResponsabile() != null && acquisto.getDirigenteResponsabile().length()>160) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".DirigenteResponsabile", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getProceduraAffidamento() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT020", acquisto.getProceduraAffidamento());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".ProceduraAffidamento", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getAcquistoVerdi() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT018", acquisto.getAcquistoVerdi());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".AcquistoVerdi", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getNormativaRiferimento() != null && acquisto.getNormativaRiferimento().length()>200) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".NormativaRiferimento", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getOggettoVerdi() != null && acquisto.getOggettoVerdi().length()>500) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".OggettoVerdi", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getCpvVerdi() != null && acquisto.getCpvVerdi().length()>12) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".CpvVerdi", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getAcquistoMaterialiRiciclati() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT018", acquisto.getAcquistoMaterialiRiciclati());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".AcquistoMaterialiRiciclati", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquisto.getOggettoMatRic() != null && acquisto.getOggettoMatRic().length()>500) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".OggettoMatRic", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getCpvMatRic() != null && acquisto.getCpvMatRic().length()>12) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".CpvMatRic", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquisto.getCoperturaFinanziaria() != null && !acquisto.getCoperturaFinanziaria().equals("1") && !acquisto.getCoperturaFinanziaria().equals("2")) {
			ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".CoperturaFinanziaria", "Valore non valido");
			controlli.add(item);
		} 
		
		if (acquisto.getValutazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT021", acquisto.getValutazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto " + acquisto.getCui() + ".Valutazione", "Valore non valido");
				controlli.add(item);
			}
		}
		this.validateWSControlli("PUBBLICA_FS", "INTTRI", acquisto, null, controlli);
		//validate Rup
		if (acquisto.getRup() != null) {
			this.validatePubblicaTecnico(acquisto.getRup(), "acquisto " + acquisto.getCui() + ".Rup" , controlli);
		}
	}

	private void validateAcquistoNonRiproposto(AcquistoNonRipropostoEntry acquistoNonRiproposto, List<ValidateEntry> controlli) throws IOException {
		
		if(acquistoNonRiproposto.getCui() == null) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getConint() + ".Cui", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else if (acquistoNonRiproposto.getCui().length()>25) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (acquistoNonRiproposto.getCup() != null && acquistoNonRiproposto.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (acquistoNonRiproposto.getDescrizione() == null) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (acquistoNonRiproposto.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if(acquistoNonRiproposto.getImporto() == null) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Importo", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (acquistoNonRiproposto.getPriorita() == null) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Priorita", "Valorizzare il campo");
			controlli.add(item);
		} else {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT008", acquistoNonRiproposto.getPriorita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Priorita", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (acquistoNonRiproposto.getMotivo() == null) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Motivo", "Valorizzare il campo");
			controlli.add(item);
		} else if(acquistoNonRiproposto.getMotivo().length()>2000) {
			ValidateEntry item = new ValidateEntry("acquisto non riproposto " + acquistoNonRiproposto.getCui() + ".Motivo", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		this.validateWSControlli("PUBBLICA_FS", "INRTRI", acquistoNonRiproposto, null, controlli);
	}

	private void validateOperaIncompiuta(OperaIncompiutaEntry opera, List<ValidateEntry> controlli) throws IOException {
		
		if (opera.getCup() == null) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".cup", "Valorizzare il campo");
			controlli.add(item);
		} else if (opera.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (opera.getDescrizione() == null) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".descrizione", "Valorizzare il campo");
			controlli.add(item);
		} else if (opera.getDescrizione().length()>2000) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (opera.getDeterminazioneAmministrazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx02", opera.getDeterminazioneAmministrazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".determinazioneAmministrazione", "Valore non valido");
				controlli.add(item);
			}
		}
		if (opera.getAmbito() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx04", opera.getAmbito());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".ambito", "Valore non valido");
				controlli.add(item);
			}
		}
		if (opera.getCausa() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx05", opera.getCausa());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Causa", "Valore non valido");
				controlli.add(item);
			}
		}
		if (opera.getStato() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx06", opera.getStato());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Stato", "Valore non valido");
				controlli.add(item);
			}
		}
		if (opera.getInfrastruttura() != null && !opera.getInfrastruttura().equals("1") && !opera.getInfrastruttura().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Infrastruttura", "Valore non valido");
			controlli.add(item);
		}
		if (opera.getDiscontinuitaRete() != null && !opera.getDiscontinuitaRete().equals("1") && !opera.getDiscontinuitaRete().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".DiscontinuitaRete", "Valore non valido");
			controlli.add(item);
		}
		if (opera.getFruibile() != null && !opera.getFruibile().equals("1") && !opera.getFruibile().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Fruibile", "Valore non valido");
			controlli.add(item);
		}
		if (opera.getRidimensionato() != null && !opera.getRidimensionato().equals("1") && !opera.getRidimensionato().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Ridimensionato", "Valore non valido");
			controlli.add(item);
		}
		if (opera.getDestinazioneUso() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx07", opera.getDestinazioneUso());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".DestinazioneUso", "Valore non valido");
				controlli.add(item);
			}
		}
		if (opera.getCessione() != null && !opera.getCessione().equals("1") && !opera.getCessione().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Cessione", "Valore non valido");
			controlli.add(item);
		}
		if (opera.getPrevistaVendita() != null && !opera.getPrevistaVendita().equals("1") && !opera.getPrevistaVendita().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".PrevistaVendita", "Valore non valido");
			controlli.add(item);
		}
		if (opera.getDemolizione() != null && !opera.getDemolizione().equals("1") && !opera.getDemolizione().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + opera.getNumoi() + ".Demolizione", "Valore non valido");
			controlli.add(item);
		}
		//validate altri dati
		if (opera.getAltriDati() != null) {
			opera.getAltriDati().setNumoi(opera.getNumoi());
			this.validateAltriDatiOperaIncompiuta(opera.getAltriDati(), controlli);
		}
		
		this.validateWSControlli("PUBBLICA_LAVORI", "OITRI", opera, null, controlli);
		//validate Immmobili
		int numimm = 1;
		if (opera.getImmobili() != null) {
			for(ImmobileEntry immobile:opera.getImmobili()) {
				immobile.setNumoi(opera.getNumoi());
				immobile.setNumimm(new Long(numimm));
				this.validateImmobileOpera(immobile, opera, "immobile nr " + immobile.getNumimm() + " - opera nr " + opera.getNumoi(), controlli);
				numimm++;
			}
		}
	}

	private void validateAltriDatiOperaIncompiuta(AltriDatiOperaIncompiutaEntry altriDati, List<ValidateEntry> controlli) throws IOException {
		
		if (altriDati.getIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + altriDati.getIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.Istat", "Valore non valido");
				controlli.add(item);
			}
		}
	
		if (altriDati.getNuts() != null) {
			int i = sqlMapper.count("TABNUTS WHERE CODICE='" + altriDati.getNuts() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.Nuts", "Valore non valido");
				controlli.add(item);
			} 
		}
		
		if (altriDati.getTipologiaIntervento() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore3("PTx01", altriDati.getTipologiaIntervento());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.TipologiaIntervento", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (altriDati.getCategoriaIntervento() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore5("PTj01", altriDati.getCategoriaIntervento());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CategoriaIntervento", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (altriDati.getRequisitiCapitolato() != null && !altriDati.getRequisitiCapitolato().equals("1") && !altriDati.getRequisitiCapitolato().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.RequisitiCapitolato", "Valore non valido");
			controlli.add(item);
		} 
		
		if (altriDati.getRequisitiApprovato() != null && !altriDati.getRequisitiApprovato().equals("1") && !altriDati.getRequisitiApprovato().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.RequisitiApprovato", "Valore non valido");
			controlli.add(item);
		} 
		
		if (altriDati.getUnitaMisura() != null && altriDati.getUnitaMisura().length()>10) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.UnitaMisura", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (altriDati.getSponsorizzazione() != null && !altriDati.getSponsorizzazione().equals("1") && !altriDati.getSponsorizzazione().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.Sponsorizzazione", "Valore non valido");
			controlli.add(item);
		} 
		
		if (altriDati.getFinanzaDiProgetto() != null && !altriDati.getFinanzaDiProgetto().equals("1") && !altriDati.getFinanzaDiProgetto().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.FinanzaDiProgetto", "Valore non valido");
			controlli.add(item);
		} 
		
		if (altriDati.getCoperturaStatale() != null && !altriDati.getCoperturaStatale().equals("1") && !altriDati.getCoperturaStatale().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaStatale", "Valore non valido");
			controlli.add(item);
		}
		
		if (altriDati.getCoperturaRegionale() != null && !altriDati.getCoperturaRegionale().equals("1") && !altriDati.getCoperturaRegionale().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaRegionale", "Valore non valido");
			controlli.add(item);
		}
		
		if (altriDati.getCoperturaProvinciale() != null && !altriDati.getCoperturaProvinciale().equals("1") && !altriDati.getCoperturaProvinciale().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaProvinciale", "Valore non valido");
			controlli.add(item);
		}
		
		if (altriDati.getCoperturaComunale() != null && !altriDati.getCoperturaComunale().equals("1") && !altriDati.getCoperturaComunale().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaComunale", "Valore non valido");
			controlli.add(item);
		}
		
		if (altriDati.getCoperturaAltro() != null && !altriDati.getCoperturaAltro().equals("1") && !altriDati.getCoperturaAltro().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaAltro", "Valore non valido");
			controlli.add(item);
		}
		
		if (altriDati.getCoperturaComunitaria() != null && !altriDati.getCoperturaComunitaria().equals("1") && !altriDati.getCoperturaComunitaria().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaComunitaria", "Valore non valido");
			controlli.add(item);
		}
		
		if (altriDati.getCoperturaPrivata() != null && !altriDati.getCoperturaPrivata().equals("1") && !altriDati.getCoperturaPrivata().equals("2")) {
			ValidateEntry item = new ValidateEntry("opera nr " + altriDati.getNumoi() + ".AltriDati.CoperturaPrivata", "Valore non valido");
			controlli.add(item);
		}
	}
	
	private void validateImmobileOpera(ImmobileEntry immobile, OperaIncompiutaEntry opera, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (immobile.getCui() == null ) {
			ValidateEntry item = new ValidateEntry("immobile nr " + immobile.getNumimm() + ".Cui", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else if (immobile.getCui().length()>30) {
			ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (immobile.getIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + immobile.getIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Istat", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getNuts() != null) {
			int i = sqlMapper.count("TABNUTS WHERE CODICE='" + immobile.getNuts() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Nuts", "Valore non valido");
				controlli.add(item);
			} 
		}
		
		if (immobile.getTrasferimentoTitoloCorrispettivo() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT013", immobile.getTrasferimentoTitoloCorrispettivo());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".TrasferimentoTitoloCorrispettivo", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getImmobileDisponibile() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT014", immobile.getImmobileDisponibile());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".ImmobileDisponibile", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getAlienati() != null && !immobile.getAlienati().equals("1") && !immobile.getAlienati().equals("2")) {
			ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".Alienati", "Valore non valido");
			controlli.add(item);
		}
		
		if (immobile.getInclusoProgrammaDismissione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT015", immobile.getInclusoProgrammaDismissione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".InclusoProgrammaDismissione", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getDescrizione() != null && immobile.getDescrizione().length()>400) {
			ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".descrizione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (immobile.getTipoProprieta() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("A2137", immobile.getTipoProprieta());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".TipoProprieta", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (immobile.getTipoDisponibilita() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("PT016", immobile.getTipoDisponibilita());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("immobile " + immobile.getCui() + ".TipoProprieta", "Valore non valido");
				controlli.add(item);
			}
		}
		
		this.validateWSControlli("PUBBLICA_LAVORI", "IMMTRAI", immobile, opera, controlli);
	}
	
	private void validatePubblicaTecnico(DatiGeneraliTecnicoEntry tecnico, String riferimento, List<ValidateEntry> controlli) throws IOException {
		if (tecnico.getCognome() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cognome", "Valorizzare il campo");
			controlli.add(item);
		} else if (tecnico.getCognome().length() > 80) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cognome",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (tecnico.getNome() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".nome", "Valorizzare il campo");
			controlli.add(item);
		} else if (tecnico.getNome().length() > 80) {
			ValidateEntry item = new ValidateEntry(riferimento + ".nome",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (tecnico.getNomeCognome() == null) {
			tecnico.setNomeCognome(tecnico.getNome() + " " + tecnico.getCognome());
		}
		if (tecnico.getCfPiva() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cfPiva", "Valorizzare il campo");
			controlli.add(item);
		} else if (!(UtilityFiscali.isValidCodiceFiscale(tecnico.getCfPiva()) || UtilityFiscali
						.isValidPartitaIVA(tecnico.getCfPiva()))) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cfPiva",
					"Il dato non e' un codice fiscale o una partita iva valida");
			controlli.add(item);
		}
		if(tecnico.getIndirizzo() != null && tecnico.getIndirizzo().length()>60) {
			ValidateEntry item = new ValidateEntry(riferimento + ".indirizzo",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if(tecnico.getCivico() != null && tecnico.getCivico().length()>10) {
			ValidateEntry item = new ValidateEntry(riferimento + ".civico",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if(tecnico.getCap() != null && tecnico.getCap().length()>5) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cap",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (tecnico.getLuogoIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + tecnico.getLuogoIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry(riferimento + ".luogoIstat", "Valore non valido");
				controlli.add(item);
			} 
		}
		if(tecnico.getProvincia() != null && tecnico.getProvincia().length()>2) {
			ValidateEntry item = new ValidateEntry(riferimento + ".provincia",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
	}
	
	public String verificaIdRicevuto(Long idRicevuto, Long tipoProgramma, String clientId) throws Exception {
		String error = "";
		int i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + idRicevuto);
		if (i == 0){
			//il programma richiesto non esiste
			error = DettaglioLavoriResult.ERROR_NOT_FOUND;
		} else {
			//se esiste verifico che il programma sia di tipo lavori
			i = sqlMapper.count("PIATRI WHERE TIPROG = " + tipoProgramma + " AND ID_GENERATO=" + idRicevuto);
			if (i == 0){
				error = DettaglioLavoriResult.ERROR_VALIDATION;
				if (tipoProgramma.equals(new Long(2))) {
					error = DettaglioFornitureServiziResult.ERROR_VALIDATION;
				}
			} /*else {
				//se esiste verifico che idclient sia uguale
				i = sqlMapper.count("PIATRI WHERE ID_GENERATO=" + idRicevuto + " and ID_CLIENT='" + clientId + "'");
				if (i == 0){
					error = DettaglioLavoriResult.ERROR_UNAUTHORIZED;
				}
			}*/
		}
		return error;
	}

	public String verificaLeggiInterventoAcquisto(String tipoProgramma, String codiceCUI, String descrizione, String codiceFiscaleSA) throws Exception {
		String error = "";
		if (tipoProgramma == null && codiceCUI == null && descrizione == null && codiceFiscaleSA == null) {
			error = InterventiAcquistiResult.ERROR_NO_PARAMETRI;
		} else if (tipoProgramma != null && !tipoProgramma.equals("1") && !tipoProgramma.equals("2")) {
			error = InterventiAcquistiResult.ERROR_TIPO_PROGRAMMA;
		}
		return error;
	}
	
	public String verificaCollegaInterventoAcquisto(String codiceLavoro, String codiceCUI) throws Exception {
		String error = "";
		if (codiceLavoro == null || codiceCUI == null) {
			error = EsitoResult.ERROR_NO_PARAMETRI;
		} else {
			// Verifica esistenza cui
			String query = "inttri join piatri on inttri.contri=piatri.contri " +
	      	"where inttri.CUIINT = '"+ codiceCUI +"' and piatri.norma=2 and (piatri.DADOZI is not null OR piatri.DAPTRI is not null) " ;
			int i = sqlMapper.count(query);
	        if (i == 0) {
	        	error = EsitoResult.ERROR_INTERVENTO_NO_CUI;
	        }
		}
		return error;
	}
	
	public String verificaDettaglioInterventoAcquisto(String codiceCUI) throws Exception {
		String error = "";
		if (codiceCUI == null) {
			error = DettaglioInterventoAcquistoResult.ERROR_NO_PARAMETRI;
		} 
		return error;
	}
	
	private void validateWSControlli(String codFunzione, String entita, Object json, Object jsonParent, List<ValidateEntry> controlli)
	throws IOException {
		PubblicaProgrammaFornitureServiziEntry programmaFS = null;
		AcquistoEntry acquisto = null;
		PubblicaProgrammaLavoriEntry programmaLavori = null;
		InterventoEntry intervento = null;
		OperaIncompiutaEntry opera = null;
		ImmobileEntry immobile = null;
		InterventoNonRipropostoEntry interventoNR = null;
		AcquistoNonRipropostoEntry acquistoNR = null;
		String oggetto = "";
		if(codFunzione.equals("PUBBLICA_FS")) {
			if(entita.equals("PIATRI")) {
				programmaFS = (PubblicaProgrammaFornitureServiziEntry)json;
			} else if(entita.equals("INTTRI")) {
				acquisto = (AcquistoEntry)json;
				oggetto = "acquisto " + acquisto.getCui() + ".";
			} else if(entita.equals("INRTRI")) {
				acquistoNR = (AcquistoNonRipropostoEntry)json;
				oggetto = "acquisto non riproposto " + acquistoNR.getCui() + ".";
			}
		} else if(codFunzione.equals("PUBBLICA_LAVORI")) {
			if(entita.equals("PIATRI")) {
				programmaLavori = (PubblicaProgrammaLavoriEntry)json;
			} else if(entita.equals("INTTRI")) {
				intervento = (InterventoEntry)json;
				oggetto = "intervento lavori " + intervento.getCui() + ".";
			} else if(entita.equals("OITRI")) {
				opera = (OperaIncompiutaEntry)json;
				oggetto = "opera nr " + opera.getNumoi() + ".";
			} else if(entita.startsWith("IMMTRAI")) {
				immobile = (ImmobileEntry)json;
				if(jsonParent != null) {
					opera = (OperaIncompiutaEntry) jsonParent;
				}
				oggetto = "immobile " + immobile.getCui() + ".";
			} else if(entita.equals("INRTRI")) {
				interventoNR = (InterventoNonRipropostoEntry)json;
				oggetto = "intervento non riproposto " + interventoNR.getCui() + ".";
			}
		}
		List<ControlloEntry> controlliDaFare = sqlMapper.getControlli(codFunzione, entita);
		if(codFunzione.equals("PUBBLICA_FS")) {
			for(ControlloEntry controllo:controlliDaFare) {
				boolean condizioneVerificata = false;
				switch (controllo.getNumero().intValue()) {
				case 10:
					//acquisti previsti?
					if (programmaFS.getAcquisti() == null || programmaFS.getAcquisti().size() == 0) {
						condizioneVerificata = true;
					}
					break;
				case 20:
					//Dati approvazione
					if (programmaFS.getDataApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 30:
					//Data pubblicazione approvazione
					if (programmaFS.getDataApprovazione() != null && programmaFS.getDataPubblicazioneApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 40:
					//titolo approvazione
					if (programmaFS.getDataApprovazione() != null && programmaFS.getTitoloAttoApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 50:
					//url approvazione
					if (programmaFS.getDataApprovazione() != null && programmaFS.getUrlAttoApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 60:
					//cup esente
					if (acquisto.getEsenteCup() == null) {
						condizioneVerificata = true;
					}
					break;
				case 70:
					//cup obbligatorio
					if (acquisto.getCup() == null && "2".equals(acquisto.getEsenteCup())) {
						condizioneVerificata = true;
					}
					break;
				case 80:
					//acquisto ricompreso
					if (acquisto.getAcquistoRicompreso() == null) {
						condizioneVerificata = true;
					}
					break;
				case 90:
					//cui collegato
					if (acquisto.getCuiCollegato() == null && "2".equals(acquisto.getAcquistoRicompreso())) {
						condizioneVerificata = true;
					}
					break;
				case 100:
					//nuts
					if (acquisto.getNuts() == null) {
						condizioneVerificata = true;
					}
					break;
				case 110:
					//cpv
					if (acquisto.getCpv() == null) {
						condizioneVerificata = true;
					}
					break;
				case 120:
					//Livello di Priorità
					if (acquisto.getPriorita() == null) {
						condizioneVerificata = true;
					}
					break;
				case 130:
					//Rup
					if (acquisto.getRup() == null) {
						condizioneVerificata = true;
					}
					break;
				case 140:
					//Lotto funzionale?
					if (acquisto.getLottoFunzionale() == null) {
						condizioneVerificata = true;
					}
					break;
				case 150:
					//Durata del contratto (mesi)
					if (acquisto.getDurataInMesi() == null) {
						condizioneVerificata = true;
					}
					break;
				case 160:
					//Nuovo affidamento contratto in essere?
					if (acquisto.getNuovoAffidamento() == null) {
						condizioneVerificata = true;
					}
					break;
				case 170:
					//Tipologia apporto di capitale privato
					if (acquisto.getTipologiaCapitalePrivato() == null && acquisto.getImportoCapitalePrivato() > 0) {
						condizioneVerificata = true;
					}
					break;
				case 180:
					//Si intende delegare la procedura di affidamento?
					if (acquisto.getDelega() == null && new Long(1).equals(acquisto.getAnno())) {
						condizioneVerificata = true;
					}
					break;
				case 190:
					//Codice AUSA del Soggetto delegato
					if (acquisto.getCodiceSoggettoDelegato() == null && "1".equals(acquisto.getDelega())) {
						condizioneVerificata = true;
					}
					break;
				case 200:
					//Denominazione del Soggetto delegato
					if (acquisto.getNomeSoggettoDelegato() == null && "1".equals(acquisto.getDelega())) {
						condizioneVerificata = true;
					}
					break;
				case 210:
					//Importo totale intervento
					if (acquisto.getImportoTotale() == 0) {
						condizioneVerificata = true;
					}
					break;
				case 220:
					//CUP
					if (acquistoNR.getCup() == null) {
						condizioneVerificata = true;
					}
					break;
				default:
					break;
				}
				if(condizioneVerificata) {
					ValidateEntry item = new ValidateEntry(oggetto + controllo.getTitolo(), controllo.getMessaggio(), controllo.getTipo());
					controlli.add(item);
				}
			}
			
		} else if(codFunzione.equals("PUBBLICA_LAVORI")) {
			for(ControlloEntry controllo:controlliDaFare) {
				boolean condizioneVerificata = false;
				switch (controllo.getNumero().intValue()) {
				case 10:
					//interventi previsti?
					if (programmaLavori.getInterventi() == null || programmaLavori.getInterventi().size() == 0) {
						condizioneVerificata = true;
					}
					break;
				case 20:
					//Dati adozione / approvazione
					if (programmaLavori.getDataApprovazione() == null && programmaLavori.getDataAdozione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 30:
					//Data pubblicazione adozione
					if (programmaLavori.getDataAdozione() != null && programmaLavori.getDataPubblicazioneAdozione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 40:
					//titolo adozione
					if (programmaLavori.getDataAdozione() != null && programmaLavori.getTitoloAttoAdozione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 50:
					//url adozione
					if (programmaLavori.getDataAdozione() != null && programmaLavori.getUrlAttoAdozione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 60:
					//Data pubblicazione approvazione
					if (programmaLavori.getDataApprovazione() != null && programmaLavori.getDataPubblicazioneApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 70:
					//titolo approvazione
					if (programmaLavori.getDataApprovazione() != null && programmaLavori.getTitoloAttoApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 80:
					//url approvazione
					if (programmaLavori.getDataApprovazione() != null && programmaLavori.getUrlAttoApprovazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 90:
					//cup esente
					if (intervento.getEsenteCup() == null) {
						condizioneVerificata = true;
					}
					break;
				case 100:
					//cup obbligatorio
					if (intervento.getCup() == null && "2".equals(intervento.getEsenteCup())) {
						condizioneVerificata = true;
					}
					break;
				case 110:
					//nuts o istat
					if (intervento.getNuts() == null && intervento.getIstat() == null) {
						condizioneVerificata = true;
					}
					break;
				case 120:
					//Livello di Priorità
					if (intervento.getPriorita() == null) {
						condizioneVerificata = true;
					}
					break;
				case 130:
					//Rup
					if (intervento.getRup() == null) {
						condizioneVerificata = true;
					}
					break;
				case 140:
					//Lotto funzionale?
					if (intervento.getLottoFunzionale() == null) {
						condizioneVerificata = true;
					}
					break;
				case 150:
					//Lavoro complesso?
					if (intervento.getLavoroComplesso() == null) {
						condizioneVerificata = true;
					}
					break;
				case 160:
					//Classificazione intervento: Tipologia
					if (intervento.getTipologia() == null) {
						condizioneVerificata = true;
					}
					break;
				case 170:
					//Classificazione intervento: categoria
					if (intervento.getCategoria() == null) {
						condizioneVerificata = true;
					}
					break;
				case 180:
					//Tipologia apporto di capitale privato
					if (intervento.getTipologiaCapitalePrivato() == null && intervento.getImportoCapitalePrivato() > 0) {
						condizioneVerificata = true;
					}
					break;
				case 190:
					//Finalità dell''intervento
					if (intervento.getFinalita() == null && new Long(1).equals(intervento.getAnno())) {
						condizioneVerificata = true;
					}
					break;
				case 200:
					//Svolta verifica conformità urbanistica?
					if (intervento.getConformitaUrbanistica() == null && new Long(1).equals(intervento.getAnno())) {
						condizioneVerificata = true;
					}
					break;
				case 210:
					//Svolta verifica conformità vincoli ambientali?
					if (intervento.getConformitaAmbientale() == null && new Long(1).equals(intervento.getAnno())) {
						condizioneVerificata = true;
					}
					break;
				case 220:
					//Stato Progettazione approvata
					if (intervento.getStatoProgettazione()== null && new Long(1).equals(intervento.getAnno())) {
						condizioneVerificata = true;
					}
					break;
				case 230:
					//Si intende delegare la procedura di affidamento?
					if (intervento.getDelega()== null && new Long(1).equals(intervento.getAnno())) {
						condizioneVerificata = true;
					}
					break;
				case 240:
					//Codice AUSA del Soggetto delegato
					if (intervento.getCodiceSoggettoDelegato() == null && "1".equals(intervento.getDelega())) {
						condizioneVerificata = true;
					}
					break;
				case 250:
					//Denominazione del Soggetto delegato
					if (intervento.getNomeSoggettoDelegato() == null && "1".equals(intervento.getDelega())) {
						condizioneVerificata = true;
					}
					break;
				case 260:
					//Determinazioni dell'amministrazione
					if (opera.getDeterminazioneAmministrazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 270:
					//Ambito di interesse dell'opera
					if (opera.getAmbito() == null) {
						condizioneVerificata = true;
					}
					break;
				case 280:
					//Anno ultimo q.e. approvato
					if (opera.getAnno() == null) {
						condizioneVerificata = true;
					}
					break;
				case 290:
					//Importo complessivo dell'intervento
					if (opera.getImportoIntervento() == null) {
						condizioneVerificata = true;
					}
					break;
				case 300:
					//Importo complessivo lavori
					if (opera.getImportoLavori() == null) {
						condizioneVerificata = true;
					}
					break;
				case 310:
					//Oneri necessari per l'ultimazione dei lavori
					if (opera.getOneri() == null) {
						condizioneVerificata = true;
					}
					break;
				case 320:
					//Importo ultimo SAL
					if (opera.getImportoAvanzamento() == null) {
						condizioneVerificata = true;
					}
					break;
				case 330:
					//Percentuale avanzamento lavori
					if (opera.getPercentualeAvanzamento() == null) {
						condizioneVerificata = true;
					}
					break;
				case 340:
					//Causa per la quale l''opera è incompiuta
					if (opera.getCausa() == null) {
						condizioneVerificata = true;
					}
					break;
				case 350:
					//Stato di realizzazione
					if (opera.getStato() == null) {
						condizioneVerificata = true;
					}
					break;
				case 360:
					//Parte di infrastruttura di rete?
					if (opera.getInfrastruttura() == null) {
						condizioneVerificata = true;
					}
					break;
				case 362:
					//Discontinuita' di rete?
					if (opera.getDiscontinuitaRete() == null && opera.getInfrastruttura() != null && opera.getInfrastruttura().equals("1")) {
						condizioneVerificata = true;
					}
					break;
				case 370:
					//Opera fruibile parzialmente?
					if (opera.getFruibile() == null) {
						condizioneVerificata = true;
					}
					break;
				case 380:
					//Utilizzo ridimensionato?
					if (opera.getRidimensionato() == null) {
						condizioneVerificata = true;
					}
					break;
				case 390:
					//Destinazione d'uso
					if (opera.getDestinazioneUso() == null) {
						condizioneVerificata = true;
					}
					break;
				case 400:
					//Cessione per realizzazione di altra opera?
					if (opera.getCessione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 410:
					//Prevista la vendita
					if (opera.getPrevistaVendita() == null) {
						condizioneVerificata = true;
					}
					break;
				case 420:
					//Opera da demolire?
					if (opera.getDemolizione() == null && opera.getPrevistaVendita()!=null && opera.getPrevistaVendita().equals("2")) {
						condizioneVerificata = true;
					}
					break;
				case 421:
					//oneriSito
					if (opera.getOneriSito() == null && opera.getDemolizione() != null && opera.getDemolizione().equals("1")) {
						condizioneVerificata = true;
					}
					break;
				case 430:
					//Descrizione dell''immobile
					if (immobile.getDescrizione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 440:
					//Codice ISTAT o NUTS del comune
					if (immobile.getIstat() == null && immobile.getNuts() == null) {
						condizioneVerificata = true;
					}
					break;
				case 450:
					//Cessione o trasferimento immobile a titolo corrispettivo
					if (immobile.getTrasferimentoTitoloCorrispettivo() == null) {
						condizioneVerificata = true;
					}
					break;
				case 460:
					//Concessi in diritto di godimento
					if (immobile.getImmobileDisponibile() == null) {
						condizioneVerificata = true;
					}
					break;
				case 470:
					//Già incluso in programma di dismissione art. 27 DL 201/2011
					if (immobile.getInclusoProgrammaDismissione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 480:
					//Tipo disponibilità
					if (opera != null) {
						if (immobile.getTipoDisponibilita() == null && ("1".equals(opera.getCessione()) || "1".equals(opera.getPrevistaVendita()))) {
							condizioneVerificata = true;
						}
					}
					break;
				case 490:
					//Valore stimato dell''immobile
					if (immobile.getValoreStimato1() == null && immobile.getValoreStimato2() == null && immobile.getValoreStimato3() == null && immobile.getValoreStimatoSucc() == null) {
						condizioneVerificata = true;
					}
					break;
				case 500:
					//CUP
					if (interventoNR.getCup() == null) {
						condizioneVerificata = true;
					}
					break;
				case 510:
					//Importo totale intervento
					if (intervento.getImportoTotale() == 0) {
						condizioneVerificata = true;
					}
					break;
				case 520:
					//Codice ISTAT o NUTS opera incompiuta
					if (opera.getAltriDati() != null && opera.getAltriDati().getNuts() == null && opera.getAltriDati().getIstat() == null) {
						condizioneVerificata = true;
					}
					break;
				case 521:
					//TipologiaIntervento
					if (opera.getImportoIntervento() != null && opera.getImportoIntervento()<100000 &&
							(opera.getAltriDati() != null && opera.getAltriDati().getTipologiaIntervento() == null)) {
						condizioneVerificata = true;
					}
					break;
				case 522:
					//CategoriaIntervento
					if (opera.getImportoIntervento() != null && opera.getImportoIntervento()<100000 &&
							(opera.getAltriDati() != null && opera.getAltriDati().getCategoriaIntervento() == null)) {
						condizioneVerificata = true;
					}
					break;
				case 523:
					//RequisitiCapitolato
					if (opera.getAltriDati() != null && opera.getAltriDati().getRequisitiCapitolato() == null) {
						condizioneVerificata = true;
					}
					break;
				case 524:
					//RequisitiApprovato
					if (opera.getAltriDati() != null && opera.getAltriDati().getRequisitiApprovato() == null) {
						condizioneVerificata = true;
					}
					break;
				case 525:
					//UnitaMisura
					if (opera.getAltriDati() != null && opera.getAltriDati().getUnitaMisura() == null) {
						condizioneVerificata = true;
					}
					break;
				case 526:
					//Dimensione
					if (opera.getAltriDati() != null && opera.getAltriDati().getDimensione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 527:
					//Sponsorizzazione()
					if (opera.getAltriDati() != null && opera.getAltriDati().getSponsorizzazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 528:
					//FinanzaDiProgetto()
					if (opera.getAltriDati() != null && opera.getAltriDati().getFinanzaDiProgetto() == null) {
						condizioneVerificata = true;
					}
					break;
				case 529:
					//CostoProgetto()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCostoProgetto() == null) {
						condizioneVerificata = true;
					}
					break;
				case 530:
					//Finanziamento()
					if (opera.getAltriDati() != null && opera.getAltriDati().getFinanziamento() == null) {
						condizioneVerificata = true;
					}
					break;
				case 531:
					//CoperturaStatale()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaStatale() == null) {
						condizioneVerificata = true;
					}
					break;
				case 532:
					//CoperturaRegionale()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaRegionale() == null) {
						condizioneVerificata = true;
					}
					break;
				case 533:
					//CoperturaProvinciale()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaProvinciale() == null) {
						condizioneVerificata = true;
					}
					break;
				case 534:
					//CoperturaComunale()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaComunale() == null) {
						condizioneVerificata = true;
					}
					break;
				case 535:
					//CoperturaComunitaria()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaComunitaria() == null) {
						condizioneVerificata = true;
					}
					break;
				case 536:
					//CoperturaAltro()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaAltro() == null) {
						condizioneVerificata = true;
					}
					break;
				case 537:
					//CoperturaPrivata()
					if (opera.getAltriDati() != null && opera.getAltriDati().getCoperturaPrivata() == null) {
						condizioneVerificata = true;
					}
					break;
				default:
					break;
				}
				if(condizioneVerificata) {
					ValidateEntry item = new ValidateEntry(oggetto + controllo.getTitolo(), controllo.getMessaggio(), controllo.getTipo());
					controlli.add(item);
				}
			}
			
		}

		
		
	}
}
