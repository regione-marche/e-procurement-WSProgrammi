package it.maggioli.eldasoft.wsprogrammi.utils;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.maggioli.eldasoft.wsprogrammi.bl.ProgrammiManager;

public class HandlerPostPubblica implements Runnable{

	private Logger logger = LoggerFactory.getLogger(HandlerPostPubblica.class);

	private Long contri;
	private Long tipoProg;
	private Long idRicevuto;
	
	private ProgrammiManager programmiManager;
	private ServletContext request;
	
	private String piatriID;
	
	public HandlerPostPubblica(Long contri, Long tipoProg, Long idRicevuto, ProgrammiManager programmiManager, ServletContext request, String piatriId) 
	{ 
		this.contri = contri;
		this.tipoProg = tipoProg;
		this.idRicevuto = idRicevuto;
		this.programmiManager = programmiManager;
		this.request = request;
		this.piatriID = piatriId;
	}
	
	@Override
	public void run() {
		logger.info("PostPubblica idRicevuto = " + this.idRicevuto + " , contri = " + this.contri + " , piatriId = " + this.piatriID);
		try {
			this.programmiManager.AggiornaImportiProgramma(this.contri, this.tipoProg);
			if (this.tipoProg.equals(new Long(1)) || this.tipoProg.equals(new Long(2))) {
				this.programmiManager.creaPdfNuovaNormativa(this.contri, this.piatriID, this.tipoProg, this.idRicevuto, this.request);
			} else {
				this.programmiManager.creaPdf(this.contri, this.piatriID, this.idRicevuto, this.request);
			}
		}catch (Exception ex) {
			logger.error("Errore durante le operazioni di post pubblicazione", ex);
		} finally{
			logger.info("Fine PostPubblica idRicevuto = " + this.idRicevuto + " , contri = " + this.contri + " , piatriId = " + this.piatriID);
		}
	}

}
