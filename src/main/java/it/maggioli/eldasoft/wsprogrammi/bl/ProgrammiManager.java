package it.maggioli.eldasoft.wsprogrammi.bl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import it.maggioli.eldasoft.wsprogrammi.dao.ProgrammiMapper;
import it.maggioli.eldasoft.wsprogrammi.dao.SqlMapper;
import it.maggioli.eldasoft.wsprogrammi.dao.TecniciMapper;
import it.maggioli.eldasoft.wsprogrammi.vo.AllegatoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.FlussoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.PubblicazioneResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.FornitureServiziDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.ImmobileDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.InterventoDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.LavoroEconomiaEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.dm2011.PubblicaProgrammaDM112011Entry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.AcquistoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.AcquistoNonRipropostoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.DettaglioFornitureServiziResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.fornitureservizi.PubblicaProgrammaFornitureServiziEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.DettaglioLavoriResult;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.ImmobileEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.InterventoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.InterventoNonRipropostoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.OperaIncompiutaEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.programmi.lavori.PubblicaProgrammaLavoriEntry;
import it.maggioli.eldasoft.wsprogrammi.utils.Costanti;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component(value = "programmiManager")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProgrammiManager {

	/** Logger di classe. */
	private Logger logger = LoggerFactory.getLogger(ProgrammiManager.class);

	/**
	 * Dao MyBatis con le primitive di estrazione dei dati.
	 */
	@Autowired
	private ProgrammiMapper programmiMapper;

	@Autowired
	private SqlMapper sqlMapper;

	@Autowired
	private TecniciMapper tecniciMapper;
	
	//@Autowired
	//private ApplicationContext appContext;
	/**
	 * @param programmiMapper
	 *            programmiMapper da settare internamente alla classe.
	 */
	public void setProgrammiMapper(ProgrammiMapper programmiMapper) {
		this.programmiMapper = programmiMapper;
	}

	/**
	 * @param sqlMapper
	 *            sqlMapper da settare internamente alla classe.
	 */
	public void setSqlMapper(SqlMapper sqlMapper) {
		this.sqlMapper = sqlMapper;
	}

	/**
	 * @param soggettiMapper
	 *            soggettiMapper da settare internamente alla classe.
	 */
	public void setTecniciMapper(TecniciMapper tecniciMapper) {
		this.tecniciMapper = tecniciMapper;
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	private Long getNextId(String table) {
		this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='" + table + "'");
		Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='" + table + "'");
		if (i == null) {
			i = 1;
		}
		return new Long(i);
	}
	/**
	 * @return ritorna la tipologia dell'applicazione dov'è installato il servizio 
	 */
	private Long tipoInstallazione() {
		if (Costanti.tipoInstallazione == null) {
			String valore = sqlMapper.getConfigValue(Costanti.CONFIG_CODICE_APP,
					Costanti.CONFIG_TIPO_INSTALL_WSREST);
			if (valore != null) {
				Costanti.tipoInstallazione = new Long(valore);
			} else {
				Costanti.tipoInstallazione = new Long(3);
			}
		}
		return Costanti.tipoInstallazione;
	}
	/**
	 * pubblica un programma nel DB
	 * 
	 * @param programma
	 *            programma DM 11/2011
	 * @param modalitaInvio
	 * 			(2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di pubblicazione del programma
	 *         
	 */
	public PubblicazioneResult pubblicaDM112011(PubblicaProgrammaDM112011Entry programma, String modalitaInvio)
	throws Exception {
		PubblicazioneResult risultato= new PubblicazioneResult();
		//ricavo il codice della Stazione appaltante, se non esiste la creo
		String codiceSA = "";
		if (programma.getCodiceUnitaOrganizzativa() != null) {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
		} else {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
		}
		programma.setCodiceSA(codiceSA);
		//ricavo il codice del referente, se non esiste lo creo
		String codiceReferente = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + programma.getReferente().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
		if (codiceReferente == null) {
			//il rup non esiste lo creo
			codiceReferente = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
			programma.getReferente().setCodice(codiceReferente);
			programma.getReferente().setCodiceSA(codiceSA);
			this.tecniciMapper.insertTecnico(programma.getReferente());
		}
		//ricavo l'id ufficio
		if (programma.getUfficio() != null) {
			String idUfficio = this.sqlMapper.executeReturnString("SELECT MAX(ID) FROM UFFICI WHERE CODEIN='" + codiceSA + "' and DENOM='" + programma.getUfficio().replaceAll("'", "''") + "'");
			if (idUfficio == null) {
				//l'ufficio non esiste, lo creo
				//ricavo l'id dell'ufficio
				Integer i = this.sqlMapper.execute("SELECT MAX(ID) FROM UFFICI");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				idUfficio = id.toString();
				this.programmiMapper.insertUfficio(id, codiceSA, programma.getUfficio());
			}
			programma.setIdUfficio(new Long(idUfficio));
		}
		programma.setCodiceReferente(codiceReferente);
		Long tipoInvio = new Long(1);
		if (programma.getIdRicevuto() == null) {
			//inserisco un nuovo programma
			//ricavo l'id del programma per la stazione appaltante
			Integer i = this.sqlMapper.execute("SELECT MAX(CONTRI) FROM PIATRI");
			Long contri = new Long(1);
			if (i != null) {
				contri = new Long(i) + 1;
			}
			programma.setContri(contri);
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.getNextId("W9PUBBLICAZIONI_GEN");
			logger.info("PubblicaDM112011 idRicevuto = " + idRicevuto);
			//i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			risultato.setId(idRicevuto);
			programma.setIdRicevuto(idRicevuto);
			//this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			this.programmiMapper.pubblicaProgrammaDM112011(programma);
		} else {
			tipoInvio = new Long(2);
			//aggiorno il programma
			//ricavo l'id del programma 
			Integer i = this.sqlMapper.execute("SELECT CONTRI FROM PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto());
			programma.setContri(new Long(i));
			this.programmiMapper.modificaPubblicazioneProgrammaDM112011(programma);
			risultato.setId(programma.getIdRicevuto());
			logger.info("PubblicaDM112011 update idRicevuto = " + programma.getIdRicevuto());
			//cancello le eventuali tabelle collegate
			this.sqlMapper.execute("DELETE FROM ECOTRI WHERE CONTRI = " + programma.getContri());
			this.sqlMapper.execute("DELETE FROM INTTRI WHERE CONTRI = " + programma.getContri());
			this.sqlMapper.execute("DELETE FROM IMMTRAI WHERE CONTRI = " + programma.getContri());
		}
		//inserisco lavori economia
		int coneco = 1;
		if (programma.getLavoriEconomia() != null) {
			for(LavoroEconomiaEntry lavEconomia: programma.getLavoriEconomia()) {
				lavEconomia.setContri(programma.getContri());
				lavEconomia.setConeco(new Long(coneco));
				this.programmiMapper.insertLavoroEconomia(lavEconomia);
				coneco++;
			}
		}
		
		//inserisco gli interventi per lavori
		int conint = 1;
		if (programma.getInterventi() != null) {
			for(InterventoDM112011Entry intervento: programma.getInterventi()) {
				intervento.setContri(programma.getContri());
				intervento.setConint(new Long(conint));
				//ricavo il codice del rup, se non esiste lo creo
				if (intervento.getRup() != null) {
					String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + intervento.getRup().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
					if (codiceRup == null) {
						//il rup non esiste lo creo
						codiceRup = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
						intervento.getRup().setCodice(codiceRup);
						this.tecniciMapper.insertTecnico(intervento.getRup());
					}
					intervento.setCodiceRup(codiceRup);
				}
				this.programmiMapper.insertInterventoDM112011(intervento);
				//inserisco gli immobili per l'intervento
				int numimm = 1;
				if(intervento.getImmobili() != null) {
					for(ImmobileDM112011Entry immobile: intervento.getImmobili()) {
						immobile.setContri(intervento.getContri());
						immobile.setConint(intervento.getConint());
						immobile.setNumimm(new Long(numimm));
						this.programmiMapper.insertImmobileDM112011(immobile);
						numimm++;
					}
				}
				conint++;
			}
		}
		
		//inserisco lavori forniture e servizi
		if (programma.getFornitureEServizi() != null) {
			for(FornitureServiziDM112011Entry fornserv: programma.getFornitureEServizi()) {
				fornserv.setContri(programma.getContri());
				fornserv.setConint(new Long(conint));
				//ricavo il codice del rup, se non esiste lo creo
				if (fornserv.getRup() != null) {
					String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + fornserv.getRup().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
					if (codiceRup == null) {
						//il rup non esiste lo creo
						codiceRup = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
						fornserv.getRup().setCodice(codiceRup);
						this.tecniciMapper.insertTecnico(fornserv.getRup());
					}
					fornserv.setCodiceRup(codiceRup);
				}
				this.programmiMapper.insertFornitureServiziDM112011(fornserv);
				conint++;
			}
		}
		
		//inserisco flusso
		FlussoEntry flusso = new FlussoEntry();
		if (tipoInstallazione().equals(new Long(3))) {
			Long idFlusso = this.getNextId("W9FLUSSI");
			//Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9FLUSSI'");
			//Long idFlusso = new Long(1);
		    //if (i != null) {
		    //	idFlusso = new Long(i) + 1;
		    //}
		    //this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9FLUSSI'");
		    flusso.setId(idFlusso);
		}
	    Long key03 = new Long(991);
	    flusso.setArea(new Long(4));
	    flusso.setKey01(programma.getContri());
	    flusso.setKey03(key03);
	    flusso.setTipoInvio(tipoInvio);
	    flusso.setDataInvio(new Date());
	    flusso.setCodiceFiscaleSA(programma.getCodiceFiscaleSA());
	    ObjectMapper mapper = new ObjectMapper();
	    flusso.setJson(mapper.writeValueAsString(programma));
	    flusso.setOggetto(programma.getId());
	    flusso.setIdComunicazione(this.insertInboxOutbox(flusso, modalitaInvio, programma.getCodiceUnitaOrganizzativa()));
	    if (tipoInstallazione().equals(new Long(3)) && !modalitaInvio.equals("3")) {
	    	this.programmiMapper.insertFlusso(flusso);	
	    }
		return risultato;
	}
	
	/**
	 * pubblica un programma nel DB
	 * 
	 * @param programma
	 *            programma lavori
	 * @param modalitaInvio
	 * 			(2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di pubblicazione del programma
	 *         
	 */
	public PubblicazioneResult pubblicaLavori(PubblicaProgrammaLavoriEntry programma, String modalitaInvio)
	throws Exception {
		PubblicazioneResult risultato= new PubblicazioneResult();
		//ricavo il codice della Stazione appaltante, se non esiste la creo
		String codiceSA = "";
		if (programma.getCodiceUnitaOrganizzativa() != null) {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
		} else {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
		}
		programma.setCodiceSA(codiceSA);
		//ricavo il codice del referente, se non esiste lo creo
		String codiceReferente = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + programma.getReferente().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
		if (codiceReferente == null) {
			//il rup non esiste lo creo
			codiceReferente = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
			programma.getReferente().setCodice(codiceReferente);
			programma.getReferente().setCodiceSA(codiceSA);
			this.tecniciMapper.insertTecnico(programma.getReferente());
		}
		//ricavo l'id ufficio
		if (programma.getUfficio() != null) {
			String idUfficio = this.sqlMapper.executeReturnString("SELECT MAX(ID) FROM UFFICI WHERE CODEIN='" + codiceSA + "' and DENOM='" + programma.getUfficio().replaceAll("'", "''") + "'");
			if (idUfficio == null) {
				//l'ufficio non esiste, lo creo
				//ricavo l'id dell'ufficio
				Integer i = this.sqlMapper.execute("SELECT MAX(ID) FROM UFFICI");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				idUfficio = id.toString();
				this.programmiMapper.insertUfficio(id, codiceSA, programma.getUfficio());
			}
			programma.setIdUfficio(new Long(idUfficio));
		}
		programma.setCodiceReferente(codiceReferente);
		Long tipoInvio = new Long(1);
		if (programma.getIdRicevuto() == null) {
			//inserisco un nuovo programma
			//ricavo l'id del programma per la stazione applatante
			Integer i = this.sqlMapper.execute("SELECT MAX(CONTRI) FROM PIATRI");
			Long contri = new Long(1);
			if (i != null) {
				contri = new Long(i) + 1;
			}
			programma.setContri(contri);
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.getNextId("W9PUBBLICAZIONI_GEN");
			logger.info("PubblicaLavori idRicevuto = " + idRicevuto);
			//i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			risultato.setId(idRicevuto);
			programma.setIdRicevuto(idRicevuto);
			//this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			this.programmiMapper.pubblicaProgrammaLavori(programma);
		} else {
			tipoInvio = new Long(2);
			//aggiorno il programma
			//ricavo l'id del programma 
			Integer i = this.sqlMapper.execute("SELECT CONTRI FROM PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto());
			programma.setContri(new Long(i));
			this.programmiMapper.modificaPubblicazioneProgrammaLavori(programma);
			risultato.setId(programma.getIdRicevuto());
			logger.info("PubblicaLavori update idRicevuto = " + programma.getIdRicevuto());
			//cancello le eventuali tabelle collegate
			this.sqlMapper.execute("DELETE FROM OITRI WHERE CONTRI = " + programma.getContri());
			this.sqlMapper.execute("DELETE FROM INTTRI WHERE CONTRI = " + programma.getContri());
			this.sqlMapper.execute("DELETE FROM IMMTRAI WHERE CONTRI = " + programma.getContri());
			this.sqlMapper.execute("DELETE FROM INRTRI WHERE CONTRI = " + programma.getContri());
		}
		//inserisco le opere incompiute
		int numoi = 1;
		int numimm = 1;
		if(programma.getOpereIncompiute() != null) {
			for(OperaIncompiutaEntry opera: programma.getOpereIncompiute()) {
				opera.setContri(programma.getContri());
				opera.setNumoi(new Long(numoi));
				this.programmiMapper.insertOperaIncompiuta(opera);
				//Inserimento Altri Dati
				if (opera.getAltriDati() != null) {
					opera.getAltriDati().setContri(programma.getContri());
					opera.getAltriDati().setNumoi(new Long(numoi));
					this.programmiMapper.insertAltriDatiOperaIncompiuta(opera.getAltriDati());
				}
				//inserisco gli immobili per l'opera incompiuta
				if(opera.getImmobili() != null) {
					for(ImmobileEntry immobile: opera.getImmobili()) {
						immobile.setContri(opera.getContri());
						immobile.setConint(new Long(0));
						immobile.setNumoi(opera.getNumoi());
						immobile.setNumimm(new Long(numimm));
						this.programmiMapper.insertImmobile(immobile);
						numimm++;
					}
				}
				numoi++;
			}
		}
		
		//inserisco gli interventi
		int conint = 1;
		if(programma.getInterventi() != null) {
			for(InterventoEntry intervento: programma.getInterventi()) {
				intervento.setContri(programma.getContri());
				intervento.setConint(new Long(conint));
				//ricavo il codice del rup, se non esiste lo creo
				if (intervento.getRup() != null) {
					String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + intervento.getRup().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
					if (codiceRup == null) {
						//il rup non esiste lo creo
						codiceRup = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
						intervento.getRup().setCodice(codiceRup);
						this.tecniciMapper.insertTecnico(intervento.getRup());
					}
					intervento.setCodiceRup(codiceRup);
				}
				this.programmiMapper.insertIntervento(intervento);
				//inserisco gli immobili per l'opera incompiuta
				numimm = 1;
				if(intervento.getImmobili() != null) {
					for(ImmobileEntry immobile: intervento.getImmobili()) {
						immobile.setContri(intervento.getContri());
						immobile.setConint(intervento.getConint());
						immobile.setNumimm(new Long(numimm));
						this.programmiMapper.insertImmobile(immobile);
						numimm++;
					}
				}
				conint++;
			}
		}
		//inserisco gli interventi non riproposti
		conint = 1;
		if(programma.getInterventiNonRiproposti() != null) {
			for(InterventoNonRipropostoEntry interventoNonRiproposto: programma.getInterventiNonRiproposti()) {
				interventoNonRiproposto.setContri(programma.getContri());
				interventoNonRiproposto.setConint(new Long(conint));
				this.programmiMapper.insertInterventoNonRiproposto(interventoNonRiproposto);
				conint++;
			}
		}
		//inserisco flusso
		FlussoEntry flusso = new FlussoEntry();
		if (tipoInstallazione().equals(new Long(3))) {
			Long idFlusso = this.getNextId("W9FLUSSI");
			//Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9FLUSSI'");
			//Long idFlusso = new Long(1);
		    //if (i != null) {
		    //	idFlusso = new Long(i) + 1;
		    //}
		    //this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9FLUSSI'");
		    flusso.setId(idFlusso);
		}
	    Long key03 = new Long(982);
	    flusso.setArea(new Long(4));
	    flusso.setKey01(programma.getContri());
	    flusso.setKey03(key03);
	    flusso.setTipoInvio(tipoInvio);
	    flusso.setDataInvio(new Date());
	    flusso.setCodiceFiscaleSA(programma.getCodiceFiscaleSA());
	    ObjectMapper mapper = new ObjectMapper();
	    flusso.setJson(mapper.writeValueAsString(programma));
	    flusso.setOggetto(programma.getId());
	    flusso.setIdComunicazione(this.insertInboxOutbox(flusso, modalitaInvio, programma.getCodiceUnitaOrganizzativa()));
	    if (tipoInstallazione().equals(new Long(3)) && !modalitaInvio.equals("3")) {
	    	this.programmiMapper.insertFlusso(flusso);
	    }
		return risultato;
	}
	
	/**
	 * pubblica un programma nel DB
	 * 
	 * @param programma
	 *            programma forniture e servizi
	 * @param modalitaInvio
	 * 			(2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di pubblicazione del programma
	 *         
	 */
	public PubblicazioneResult pubblicaFornitureServizi(PubblicaProgrammaFornitureServiziEntry programma, String modalitaInvio)
	throws Exception {
		PubblicazioneResult risultato= new PubblicazioneResult();
		//ricavo il codice della Stazione appaltante, se non esiste la creo
		String codiceSA = "";
		if (programma.getCodiceUnitaOrganizzativa() != null) {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and CODEIN_UO='" + programma.getCodiceUnitaOrganizzativa() + "'");
		} else {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + programma.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
		}
		programma.setCodiceSA(codiceSA);
		//ricavo il codice del referente, se non esiste lo creo
		String codiceReferente = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + programma.getReferente().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
		if (codiceReferente == null) {
			//il rup non esiste lo creo
			codiceReferente = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
			programma.getReferente().setCodice(codiceReferente);
			programma.getReferente().setCodiceSA(codiceSA);
			this.tecniciMapper.insertTecnico(programma.getReferente());
		}
		//ricavo l'id ufficio
		if (programma.getUfficio() != null) {
			String idUfficio = this.sqlMapper.executeReturnString("SELECT MAX(ID) FROM UFFICI WHERE CODEIN='" + codiceSA + "' and DENOM='" + programma.getUfficio().replaceAll("'", "''") + "'");
			if (idUfficio == null) {
				//l'ufficio non esiste, lo creo
				//ricavo l'id dell'ufficio
				Integer i = this.sqlMapper.execute("SELECT MAX(ID) FROM UFFICI");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				idUfficio = id.toString();
				this.programmiMapper.insertUfficio(id, codiceSA, programma.getUfficio());
			}
			programma.setIdUfficio(new Long(idUfficio));
		}
		programma.setCodiceReferente(codiceReferente);
		Long tipoInvio = new Long(1);
		if (programma.getIdRicevuto() == null) {
			//inserisco un nuovo programma
			//ricavo l'id del programma per la stazione applatante
			Integer i = this.sqlMapper.execute("SELECT MAX(CONTRI) FROM PIATRI");
			Long contri = new Long(1);
			if (i != null) {
				contri = new Long(i) + 1;
			}
			programma.setContri(contri);
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.getNextId("W9PUBBLICAZIONI_GEN");
			logger.info("PubblicaFornitureServizi idRicevuto = " + idRicevuto);
			//i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			risultato.setId(idRicevuto);
			programma.setIdRicevuto(idRicevuto);
			//this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			this.programmiMapper.pubblicaProgrammaFornitureServizi(programma);
		} else {
			tipoInvio = new Long(2);
			//aggiorno il programma
			//ricavo l'id del programma 
			Integer i = this.sqlMapper.execute("SELECT CONTRI FROM PIATRI WHERE ID_GENERATO=" + programma.getIdRicevuto());
			programma.setContri(new Long(i));
			this.programmiMapper.modificaPubblicazioneProgrammaFornitureServizi(programma);
			risultato.setId(programma.getIdRicevuto());
			logger.info("PubblicaFornitureServizi update idRicevuto = " + programma.getIdRicevuto());
			//cancello le eventuali tabelle collegate
			this.sqlMapper.execute("DELETE FROM INTTRI WHERE CONTRI = " + programma.getContri());
			this.sqlMapper.execute("DELETE FROM INRTRI WHERE CONTRI = " + programma.getContri());
		}
		
		//inserisco gli acquisti
		int conint = 1;
		if(programma.getAcquisti() != null) {
			for(AcquistoEntry acquisto: programma.getAcquisti()) {
				acquisto.setContri(programma.getContri());
				acquisto.setConint(new Long(conint));
				//ricavo il codice del rup, se non esiste lo creo
				if (acquisto.getRup() != null) {
					String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + acquisto.getRup().getCfPiva() + "' AND CGENTEI = '" + programma.getCodiceSA() + "'");
					if (codiceRup == null) {
						//il rup non esiste lo creo
						codiceRup = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
						acquisto.getRup().setCodice(codiceRup);
						this.tecniciMapper.insertTecnico(acquisto.getRup());
					}
					acquisto.setCodiceRup(codiceRup);
				}
				this.programmiMapper.insertAcquisto(acquisto);
				conint++;
			}
		}
		
		//inserisco gli acquisti non riproposti
		conint = 1;
		if(programma.getAcquistiNonRiproposti() != null) {
			for(AcquistoNonRipropostoEntry acquistoNonRiproposto: programma.getAcquistiNonRiproposti()) {
				acquistoNonRiproposto.setContri(programma.getContri());
				acquistoNonRiproposto.setConint(new Long(conint));
				this.programmiMapper.insertAcquistoNonRiproposto(acquistoNonRiproposto);
				conint++;
			}
		}
		
		//inserisco flusso
		FlussoEntry flusso = new FlussoEntry();
		if (tipoInstallazione().equals(new Long(3))) {
			Long idFlusso = this.getNextId("W9FLUSSI");
			//Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9FLUSSI'");
			//Long idFlusso = new Long(1);
		    //if (i != null) {
		    //	idFlusso = new Long(i) + 1;
		    //}
		    //this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9FLUSSI'");
		    flusso.setId(idFlusso);
		}
	    Long key03 = new Long(981);
	    flusso.setArea(new Long(4));
	    flusso.setKey01(programma.getContri());
	    flusso.setKey03(key03);
	    flusso.setTipoInvio(tipoInvio);
	    flusso.setDataInvio(new Date());
	    flusso.setCodiceFiscaleSA(programma.getCodiceFiscaleSA());
	    ObjectMapper mapper = new ObjectMapper();
	    flusso.setJson(mapper.writeValueAsString(programma));
	    flusso.setOggetto(programma.getId());
	    flusso.setIdComunicazione(this.insertInboxOutbox(flusso, modalitaInvio, programma.getCodiceUnitaOrganizzativa()));
	    if (tipoInstallazione().equals(new Long(3)) && !modalitaInvio.equals("3")) {
	    	this.programmiMapper.insertFlusso(flusso);
	    }
		return risultato;
	}
	
	
	/**
	 * Estrae il dettaglio di un programma di lavori.
	 *
	 * @param idRicevuto
	 *        identificativo del programma
	 * @return dati di dettaglio del programma; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public DettaglioLavoriResult getDettaglioLavori(Long idRicevuto) {
		DettaglioLavoriResult risultato = new DettaglioLavoriResult();
		try {
			// estrazione dei dati generali del programma
			PubblicaProgrammaLavoriEntry programma = this.programmiMapper.getDettaglioLavori(new Long(idRicevuto));

			if (programma == null) {
				// non ho estratto nulla, allora l'input era errato
				risultato.setError(DettaglioLavoriResult.ERROR_NOT_FOUND);
			} else {
				programma.setIdRicevuto(idRicevuto);
				risultato.setProgramma(programma);
				//tecnico
				if (StringUtils.isNotEmpty(programma.getCodiceReferente())) {
					programma.setReferente(this.tecniciMapper.getTecnico(programma.getCodiceReferente()));
				}
				//opere incompiute
				programma.setOpereIncompiute(this.programmiMapper.getOpereIncompiute(programma.getContri()));
				for(OperaIncompiutaEntry opera:programma.getOpereIncompiute()) {
					opera.setAltriDati(this.programmiMapper.getAltriDatiOperaIncompiuta(programma.getContri(), opera.getNumoi()));
					//immobili opera
					opera.setImmobili(this.programmiMapper.getImmobiliOperaIncompiuta(programma.getContri(), opera.getNumoi()));
				}
				//interventi
				programma.setInterventi(this.programmiMapper.getInterventi(programma.getContri()));
				for(InterventoEntry intervento:programma.getInterventi()) {
					//Rup intervento
					if (StringUtils.isNotEmpty(intervento.getCodiceRup())) {
						intervento.setRup(this.tecniciMapper.getTecnico(intervento.getCodiceRup()));
					}
					//immobili intervento
					intervento.setImmobili(this.programmiMapper.getImmobiliIntervento(programma.getContri(), intervento.getConint()));
				}
				//interventi non riproposti
				programma.setInterventiNonRiproposti(this.programmiMapper.getInterventiNonRiproposti(programma.getContri()));
				//date pubblicazione
				Date primaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MIN(DATINV) FROM W9FLUSSI WHERE AREA = 4 and KEY03 = 982 and KEY01 = " + programma.getContri());
				if (primaPubblicazione != null) {
					programma.setPrimaPubblicazioneSCP(primaPubblicazione);
				}
				Date ultimaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MAX(DATINV) FROM W9FLUSSI WHERE AREA = 4 and KEY03 = 982 and KEY01 = " + programma.getContri());
				if (ultimaPubblicazione != null) {
					programma.setUltimaModificaSCP(ultimaPubblicazione);
				}
			}
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione del dettaglio di un programma con id ricevuto " + idRicevuto, t);
			risultato.setError(DettaglioLavoriResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	}  
	  
	/**
	 * Estrae il dettaglio di un programma di forniture e servizi.
	 *
	 * @param idRicevuto
	 *        identificativo del programma
	 * @return dati di dettaglio del programma; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public DettaglioFornitureServiziResult getDettaglioFornitureServizi(Long idRicevuto) {
		DettaglioFornitureServiziResult risultato = new DettaglioFornitureServiziResult();
		try {
			// estrazione dei dati generali del programma
			PubblicaProgrammaFornitureServiziEntry programma = this.programmiMapper.getDettaglioFornitureServizi(new Long(idRicevuto));

			if (programma == null) {
				// non ho estratto nulla, allora l'input era errato
				risultato.setError(DettaglioFornitureServiziResult.ERROR_NOT_FOUND);
			} else {
				programma.setIdRicevuto(idRicevuto);
				risultato.setProgramma(programma);
				//tecnico
				if (StringUtils.isNotEmpty(programma.getCodiceReferente())) {
					programma.setReferente(this.tecniciMapper.getTecnico(programma.getCodiceReferente()));
				}
				//acquisti
				programma.setAcquisti(this.programmiMapper.getAcquisti(programma.getContri()));
				for(AcquistoEntry acquisto:programma.getAcquisti()) {
					//Rup acquisto
					if (StringUtils.isNotEmpty(acquisto.getCodiceRup())) {
						acquisto.setRup(this.tecniciMapper.getTecnico(acquisto.getCodiceRup()));
					}
				}
				//acquisti non riproposti
				programma.setAcquistiNonRiproposti(this.programmiMapper.getAcquistiNonRiproposti(programma.getContri()));
				//date pubblicazione
				Date primaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MIN(DATINV) FROM W9FLUSSI WHERE AREA = 4 and KEY03 = 981 and KEY01 = " + programma.getContri());
				if (primaPubblicazione != null) {
					programma.setPrimaPubblicazioneSCP(primaPubblicazione);
				}
				Date ultimaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MAX(DATINV) FROM W9FLUSSI WHERE AREA = 4 and KEY03 = 981 and KEY01 = " + programma.getContri());
				if (ultimaPubblicazione != null) {
					programma.setUltimaModificaSCP(ultimaPubblicazione);
				}
			}
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione del dettaglio di un programma con id ricevuto " + idRicevuto, t);
			risultato.setError(DettaglioFornitureServiziResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	} 

	  
	/**
	 * inserisce un record nella inbox per l'avvenuto salvataggio del programma
	 * genera se richiesto un record in w9outbox 
	 * 
	 * @param programma
	 *            programma lavori
	 * @param modalitaInvio
	 *            (2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di pubblicazione del programma
	 *         
	 */
	private Long insertInboxOutbox(FlussoEntry flusso, String modalitaInvio, String codiceUnitaOrganizzativa) throws Exception{
		//Inserimento pubblicazione in W9Inbox
		//ricavo l'id univoco della pubblicazione
		Long idComun = this.getNextId("W9INBOX");
		//Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9INBOX'");
		//Long idComun = new Long(1);
	    //if (i != null) {
	    //	idComun = new Long(i) + 1;
	    //}
	    //this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9INBOX'");
	    this.programmiMapper.insertInbox(idComun, new Date(), new Long(2), flusso.getJson());
	    if (modalitaInvio.equals("2")) {
	    	Integer i = this.sqlMapper.execute("SELECT MAX(IDCOMUN) FROM W9OUTBOX");
		    Long idComunOut = new Long(1);
		    if (i != null) {
		    	idComunOut = new Long(i) + 1;
		    }
		    this.programmiMapper.insertOutbox(idComunOut, flusso.getArea(), flusso.getKey01(), flusso.getKey02(), flusso.getKey03(), flusso.getKey04(), new Long(1), flusso.getCodiceFiscaleSA(), codiceUnitaOrganizzativa);
	    }
	    return idComun;
	}
	/**
	   * inserisce il documento pdf nel programma specificato
	   *
	   * @param idRicevuto
	   *        id ricevuto
	   * @param pdf
	   *        pdf
	   * @return risultato dell'operazione di inserimento di un documento per una pubblicazione
	   */
	  public PubblicazioneResult setPdf(AllegatoEntry pdf) throws Exception {
		  PubblicazioneResult risultato = null;
		  this.programmiMapper.setPdf(pdf);
		  risultato = new PubblicazioneResult();
		  risultato.setId(pdf.getNrDoc());
		  return risultato;
	  }
	  
	  /**
	   * Estrae il pdf relativo ad un programma
	   *
	   * @param id
	   *        identificativo del programma
	   * @return pdf del programma; nel caso di errore si setta il campo error con un identificativo di errore
	   */
	  public AllegatoEntry getPdf(Long id) {
		  AllegatoEntry risultato = null;
	    try {
	      risultato = this.programmiMapper.getPdf(id);

	    } catch (Throwable t) {
	      // qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
	      logger.error("Errore inaspettato durante l'estrazione del pdf del programma con id ricevuto " + id , t);
	    }
	    return risultato;
	  }
	  
	  public void AggiornaImportiProgramma(Long contri, Long tipoProgramma){
		  try {
			  String updateImportiInterventiLavoriDM112011 = "update inttri set di1int = COALESCE(DV1TRI,0) + COALESCE(IM1TRI,0) + COALESCE(MU1TRI,0) + COALESCE(AL1TRI,0) + COALESCE(AP1TRI,0) + COALESCE(BI1TRI,0),"
				  + " di2int = COALESCE(DV2TRI,0) + COALESCE(IM2TRI,0) + COALESCE(MU2TRI,0) + COALESCE(AL2TRI,0) + COALESCE(AP2TRI,0) + COALESCE(BI2TRI,0),"
				  + " di3int = COALESCE(DV3TRI,0) + COALESCE(IM3TRI,0) + COALESCE(MU3TRI,0) + COALESCE(AL3TRI,0) + COALESCE(AP3TRI,0) + COALESCE(BI3TRI,0),"
				  + " ICPINT = COALESCE(PR1TRI,0) + COALESCE(PR2TRI,0) + COALESCE(PR3TRI,0)"
				  + " WHERE CONTRI = " + contri + " and TIPINT=1";
			  String updateImportiLavoriDM112011_2 = "update inttri set DITINT = COALESCE(di1int,0) + COALESCE(di2int,0) + COALESCE(di3int,0) WHERE CONTRI = " + contri + " and TIPINT=1";
			  String updateImportiLavoriDM112011_3 = "update inttri set TOTINT = COALESCE(DITINT,0) + COALESCE(ICPINT,0) WHERE CONTRI = " + contri + " and TIPINT=1";
			  String updateImportiInterventiFSDM112011 = "update inttri set di1int = COALESCE(AL1TRI,0) + COALESCE(BI1TRI,0) + COALESCE(RG1TRI,0) + COALESCE(IMPRFS,0) + COALESCE(MU1TRI,0) + COALESCE(PR1TRI,0) WHERE CONTRI = " + contri + " and TIPINT=2";
			  String updateImportiInterventi = "update inttri set di1int = COALESCE(BI1TRI,0) + COALESCE(DV1TRI,0) + COALESCE(IM1TRI,0) + COALESCE(MU1TRI,0) + COALESCE(AL1TRI,0) + COALESCE(AP1TRI,0),"
				  + " di2int = COALESCE(BI2TRI,0) + COALESCE(DV2TRI,0) + COALESCE(IM2TRI,0) + COALESCE(MU2TRI,0) + COALESCE(AL2TRI,0) + COALESCE(AP2TRI,0),"
				  + " di3int = COALESCE(BI3TRI,0) + COALESCE(DV3TRI,0) + COALESCE(IM3TRI,0) + COALESCE(MU3TRI,0) + COALESCE(AL3TRI,0) + COALESCE(AP3TRI,0),"
				  + " di9int = COALESCE(BI9TRI,0) + COALESCE(DV9TRI,0) + COALESCE(IM9TRI,0) + COALESCE(MU9TRI,0) + COALESCE(AL9TRI,0) + COALESCE(AP9TRI,0),"
				  + " ICPINT = COALESCE(PR1TRI,0) + COALESCE(PR2TRI,0) + COALESCE(PR3TRI,0) + COALESCE(PR9TRI,0)"
				  + " WHERE CONTRI = " + contri;

			  String updateImportiInterventi_2 = "update inttri set DITINT = COALESCE(di1int,0) + COALESCE(di2int,0) + COALESCE(di3int,0) + COALESCE(di9int,0) WHERE CONTRI = " + contri;
			  String updateImportiInterventi_3 = "update inttri set TOTINT = COALESCE(DITINT,0) + COALESCE(ICPINT,0) + COALESCE(SPESESOST,0) WHERE CONTRI = " + contri;
			  String updateImportiImmobili = "update immtrai set VALIMM = COALESCE(VA1IMM,0) + COALESCE(VA2IMM,0) + COALESCE(VA3IMM,0) + COALESCE(VA9IMM,0) WHERE CONTRI = " + contri;
			  
			  switch (tipoProgramma.intValue()) {
			  	case 3:
			  		//Aggiorno importi interventi programma DM 11/2011
			  		this.sqlMapper.execute(updateImportiInterventiLavoriDM112011);
			  		this.sqlMapper.execute(updateImportiLavoriDM112011_2);
			  		this.sqlMapper.execute(updateImportiLavoriDM112011_3);
			  		this.sqlMapper.execute(updateImportiInterventiFSDM112011);
			  		break;
			  	default:
			  		this.sqlMapper.execute(updateImportiInterventi);
			  		this.sqlMapper.execute(updateImportiInterventi_2);
			  		this.sqlMapper.execute(updateImportiInterventi_3);
			  		this.sqlMapper.execute(updateImportiImmobili);
			  		break;
			  }
			  
			  String updateImportiProgramma = "UPDATE PIATRI SET "
			    	+ "DV1TRI ="+ sommaImportiIntervento("DV1TRI", contri, tipoProgramma) + ","
			    	+ "DV2TRI ="+ sommaImportiIntervento("DV2TRI", contri, tipoProgramma) + ","
			    	+ "DV3TRI ="+ sommaImportiIntervento("DV3TRI", contri, tipoProgramma) + ","
			        + "IM1TRI ="+ sommaImportiIntervento("IM1TRI", contri, tipoProgramma) + ","
			        + "IM2TRI ="+ sommaImportiIntervento("IM2TRI", contri, tipoProgramma) + ","
			        + "IM3TRI ="+ sommaImportiIntervento("IM3TRI", contri, tipoProgramma) + ","
			        + "MU1TRI ="+ sommaImportiIntervento("MU1TRI", contri, tipoProgramma) + ","
			        + "MU2TRI ="+ sommaImportiIntervento("MU2TRI", contri, tipoProgramma) + ","
			        + "MU3TRI ="+ sommaImportiIntervento("MU3TRI", contri, tipoProgramma) + ","
			        + "PR1TRI ="+ sommaImportiIntervento("PR1TRI", contri, tipoProgramma) + ","
			        + "PR2TRI ="+ sommaImportiIntervento("PR2TRI", contri, tipoProgramma) + ","
			        + "PR3TRI ="+ sommaImportiIntervento("PR3TRI", contri, tipoProgramma) + ","
			        + "AL1TRI ="+ sommaImportiIntervento("AL1TRI", contri, tipoProgramma) + ","
			        + "AL2TRI ="+ sommaImportiIntervento("AL2TRI", contri, tipoProgramma) + ","
			        + "AL3TRI ="+ sommaImportiIntervento("AL3TRI", contri, tipoProgramma) + ","
			        + "AP1TRI ="+ sommaImportiIntervento("AP1TRI", contri, tipoProgramma) + ","
			        + "AP2TRI ="+ sommaImportiIntervento("AP2TRI", contri, tipoProgramma) + ","
			        + "AP3TRI ="+ sommaImportiIntervento("AP3TRI", contri, tipoProgramma) + ","
			        + "BI1TRI ="+ sommaImportiIntervento("BI1TRI", contri, tipoProgramma) + ","
			        + "BI2TRI ="+ sommaImportiIntervento("BI2TRI", contri, tipoProgramma) + ","
			        + "BI3TRI ="+ sommaImportiIntervento("BI3TRI", contri, tipoProgramma) + ","
			        + "TO1TRI ="+ sommaImportiIntervento("DI1INT", contri, tipoProgramma) + ","
			        + "TO2TRI ="+ sommaImportiIntervento("DI2INT", contri, tipoProgramma) + ","
			        + "TO3TRI ="+ sommaImportiIntervento("DI3INT", contri, tipoProgramma) + ","
			        + "RG1TRI ="+ sommaImportiIntervento("RG1TRI", contri, tipoProgramma) + ","
			        + "IMPRFS ="+ sommaImportiIntervento("IMPRFS", contri, tipoProgramma)
			        + " WHERE CONTRI = " + contri;
			  this.sqlMapper.execute(updateImportiProgramma);
			  
		  } catch (Exception e) {
		      logger.error("Errore durante l'aggiornamento degli importi del programma", e);
		  }
		  
	  }
	  
	  /**
	   *
	   * @param campo campo
	   * @param contri codice programma
	   * @param tiprog tipo programma
	   * @return importo
	   * @throws GestoreException GestoreException
	   */
	  private Double sommaImportiIntervento(final String campo, final Long contri, final Long tiprog) throws Exception {
	    String sqlSelectSomma = "select SUM(" + campo + ") from INTTRI where CONTRI=" + contri + " and (ACQALTINT is null or ACQALTINT =1)";
	    if (tiprog.equals(new Long(3))) {
	    	sqlSelectSomma += " AND TIPINT=1";
	    }
	    Double somma;
	    try {
	    	somma = this.sqlMapper.executeReturnDouble(sqlSelectSomma);
	    } catch (Exception e) {
	      somma = new Double(0);
	      throw new Exception(e);
	    }
	    return somma;
	  }
	  
	  public String calcolaCodificaAutomatica(String entita, String campoChiave) throws Exception {
			String codice = "1";
			String formatoCodice = null;
			String codcal = null;
			Long cont = null;
			try {
				String query = "select CODCAL, CONTAT from G_CONFCOD where NOMENT = '" + entita + "'";
				List<Map<String,Object>> confcod = sqlMapper.select(query);
				if (confcod!= null && confcod.size() > 0) {
					for(Map<String,Object> row:confcod) {
						if (row.containsKey("CODCAL")) {
							codcal = row.get("CODCAL").toString();
						} else {
							codcal = row.get("codcal").toString();
						}
						if (row.containsKey("CONTAT")) {
							cont = new Long(row.get("CONTAT").toString());
						} else {
							cont = new Long(row.get("contat").toString());
						}
						break;
					}
					boolean codiceUnivoco = false;
					int numeroTentativi = 0;
					StringBuffer strBuffer = null;
					long tmpContatore = cont.longValue();
					while (!codiceUnivoco
							&& numeroTentativi < Costanti.NUMERO_MAX_TENTATIVI_INSERT) {
						strBuffer = new StringBuffer("");
						// Come prima cosa eseguo l'update del contatore
						tmpContatore++;
						sqlMapper.execute("update G_CONFCOD set contat = " + tmpContatore + " where NOMENT = '" + entita + "'");

						strBuffer = new StringBuffer("");
						formatoCodice = codcal;
						while (formatoCodice.length() > 0) {
							switch (formatoCodice.charAt(0)) {
							case '<': // Si tratta di un'espressione numerica
								String strNum = formatoCodice.substring(1, formatoCodice.indexOf('>'));
								if (strNum.charAt(0) == '0') {
									// Giustificato a destra
									for (int i = 0; i < (strNum.length() - String.valueOf(tmpContatore).length()); i++)
										strBuffer.append('0');
								}
								strBuffer.append(String.valueOf(tmpContatore));

								formatoCodice = formatoCodice.substring(formatoCodice.indexOf('>') + 1);
								break;
							case '"': // Si tratta di una parte costante
								strBuffer.append(formatoCodice.substring(1, formatoCodice.indexOf('"', 1)));
								formatoCodice = formatoCodice.substring(formatoCodice.indexOf('"', 1) + 1);
								break;
							}
						}
						int occorrenze = sqlMapper.count(entita + " WHERE " + campoChiave + " ='" + strBuffer.toString() + "'");
						if (occorrenze == 0) {
							codiceUnivoco = true;
							codice = strBuffer.toString();
						}
						else {
							numeroTentativi++;
						}
					}
					if (!codiceUnivoco) {
						logger.error("numeroTentativi esaurito durante il calcolo per la codifica automatica " + entita);
						throw new Exception("numeroTentativi esaurito durante il calcolo per la codifica automatica " + entita);
					}
				}
			} catch (Exception ex) {
				logger.error("Errore inaspettato durante il calcolo per la codifica automatica " + entita, ex);
				throw new Exception(ex);
			}
			return codice;
		}
}
