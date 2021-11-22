package it.maggioli.eldasoft.wsprogrammi.bl;

import java.util.List;
import java.util.Map;

import it.maggioli.eldasoft.wsprogrammi.dao.IntegrazioneLFSMapper;
import it.maggioli.eldasoft.wsprogrammi.dao.SqlMapper;
import it.maggioli.eldasoft.wsprogrammi.vo.EsitoResult;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.DettaglioInterventoAcquistoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.DettaglioInterventoAcquistoResult;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.InterventiAcquistiResult;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.InterventoAcquistoEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component(value = "integrazioneLFSManager")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class IntegrazioneLFSManager {

	/** Logger di classe. */
	private Logger logger = LoggerFactory.getLogger(IntegrazioneLFSManager.class);

	/**
	 * Dao MyBatis con le primitive di estrazione dei dati.
	 */
	@Autowired
	private IntegrazioneLFSMapper integrazioneLFSMapper;

	@Autowired
	private SqlMapper sqlMapper;
	
	/**
	 * @param integrazioneLFSMapper
	 *            integrazioneLFSMapper da settare internamente alla classe.
	 */
	public void setIntegrazioneLFSMapper(IntegrazioneLFSMapper integrazioneLFSMapper) {
		this.integrazioneLFSMapper = integrazioneLFSMapper;
	}

	/**
	 * @param sqlMapper
	 *            sqlMapper da settare internamente alla classe.
	 */
	public void setSqlMapper(SqlMapper sqlMapper) {
		this.sqlMapper = sqlMapper;
	}

	/**
	 * Estrae l'elenco interventi/acquisti che soddisfano i filtri.
	 *
	 * @param tipoProgramma
	 *        Tipologia programma, accetta i valori 1 - Programma Lavori, 2 - Programma Forniture/Servizi
	 * @param codiceCUI
	 *        Codice CUI dell'intervento/acquisto
	 * @param descrizione
	 *        Descrizione dell'intervento/acquisto
	 * @param codiceFiscaleSA
	 *        Codice fiscale stazione appaltante richiedente 
	 * @return dati di dettaglio del programma; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public InterventiAcquistiResult getInterventiAcquisti(String tipoProgramma, String codiceCUI, String descrizione, String codiceFiscaleSA) {
		InterventiAcquistiResult risultato = new InterventiAcquistiResult();
		try {
			// estrazione dei dati generali del programma
			Long tiprog = null;
			if (tipoProgramma != null) {
				tiprog = new Long(tipoProgramma);
			}
			List<InterventoAcquistoEntry> elencoInterventiAcquisti = this.integrazioneLFSMapper.getInterventiAcquisti(tiprog, codiceCUI, descrizione, codiceFiscaleSA);
			String cuiPrecedente = "";
			for (int i = 0; i < elencoInterventiAcquisti.size(); i++) {
				InterventoAcquistoEntry interventoAcquisto = elencoInterventiAcquisti.get(i);
				String cui = interventoAcquisto.getCui();
				if(!cui.equals(cuiPrecedente)) {
					if(interventoAcquisto.getAssociato() == null || interventoAcquisto.getAssociato().equals("2")) {
						interventoAcquisto.setCodiceLavoro("");
					}
				} else {
					elencoInterventiAcquisti.remove(i);
					i--;
				}
				cuiPrecedente = cui;
			}
			risultato.setInterventiAcquisti(elencoInterventiAcquisti);
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione dell'elenco degli interventi / acquisti", t);
			risultato.setError(InterventiAcquistiResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	}  

	/**
	 * collega l'intervento / acquisto con il codiceLavoro
	 * 
	 * @param codiceLavoro
	 *            codiceLavoro
	 * @param codiceCUI
	 *            codiceCUI
	 * @return risultato dell'operazione di collegamento
	 *         
	 */
	public EsitoResult collegaInterventoAcquisto(String codiceLavoro, String codiceCUI) throws Exception{
		EsitoResult risultato = new EsitoResult();
		String query = "select piatri.contri, inttri.conint from inttri join piatri on inttri.contri=piatri.contri " +
      	" where UPPER(inttri.CUIINT) = '" + codiceCUI + "' and piatri.norma=2 and (piatri.DADOZI is not null OR piatri.DAPTRI is not null) " + 
    	" ORDER BY inttri.CUIINT, piatri.DADOZI DESC, piatri.DAPTRI DESC";
    	
		List<Map<String,Object>> interventi = sqlMapper.select(query);
		if (interventi!= null && interventi.size() > 0) {
			Long contri = null;
    		Long conint = null;
    		Map<String,Object> row = interventi.get(0);
    		if (row.containsKey("CONTRI")) {
    			contri = new Long(row.get("CONTRI").toString());
			} else {
				contri = new Long(row.get("contri").toString());
			}
    		if (row.containsKey("CONINT")) {
    			conint = new Long(row.get("CONINT").toString());
			} else {
				conint = new Long(row.get("conint").toString());
			}	
    		int i = sqlMapper.count("inttri where UPPER(CUIINT) = '" + codiceCUI + "' and CONTRI = " + contri + " and CONINT = " + conint + " and CODINT is not null and CODINT<>'' and CEFINT='1'");
    		if(i == 0) {
    			sqlMapper.execute("update INTTRI set CODINT = '" + codiceLavoro + "', CEFINT='1' where UPPER(CUIINT) = '" + codiceCUI + "' and CONTRI = " + contri + " and CONINT = " + conint );
    			risultato.setEsito(true);
    		} else {
    			risultato.setEsito(false);
    			risultato.setMessaggio(EsitoResult.ERROR_INTERVENTO_CUI_ASSOCIATO);    			
    		}
		}
		return risultato;
	}
	
	/**
	 * Estrae il dettaglio dell'intervento / acquisto associato al CUI indicato.
	 *
	 * @param codiceCUI
	 *        Codice CUI dell'intervento/acquisto
	 * @return dettaglio dell'intervento / acquisto associato al CUI; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public DettaglioInterventoAcquistoResult getDettaglioInterventoAcquisto(String codiceCUI) {
		DettaglioInterventoAcquistoResult risultato = new DettaglioInterventoAcquistoResult();
		try {
			List<DettaglioInterventoAcquistoEntry> interventoAcquisto = this.integrazioneLFSMapper.getInterventoAcquisto(codiceCUI);
			if (interventoAcquisto.size()>0) {
				risultato.setInterventoAcquisto(interventoAcquisto.get(0));
			} else {
				risultato.setError(DettaglioInterventoAcquistoResult.ERROR_NO_DATA);
			}
			
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione del dettaglio intervento / acquisto", t);
			risultato.setError(DettaglioInterventoAcquistoResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	}  

}
