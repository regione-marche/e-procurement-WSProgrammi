package it.maggioli.eldasoft.wsprogrammi.dao;

import java.util.List;

import it.maggioli.eldasoft.wsprogrammi.vo.lfs.DettaglioInterventoAcquistoEntry;
import it.maggioli.eldasoft.wsprogrammi.vo.lfs.InterventoAcquistoEntry;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;



/**
 * DAO Interface per l'estrazione delle informazioni relative ai programmi
 * mediante MyBatis.
 * 
 * @author Mirco.Franzoni
 */
public interface IntegrazioneLFSMapper {

	@Select("<script>select piatri.ANNTRI, inttri.CUIINT, inttri.DESINT, tecni.CFTEC, tecni.NOMETEI, tecni.COGTEI, inttri.ANNRIF, inttri.CODINT, inttri.CEFINT, inttri.CUPPRG, inttri.CODCPV "
			+ "from inttri join piatri on inttri.contri=piatri.contri left join tecni on inttri.codrup=tecni.codtec left join uffint on piatri.cenint=uffint.codein "
			+ "where piatri.norma=2 and inttri.CUIINT is not null and (piatri.DADOZI is not null OR piatri.DAPTRI is not null)"
			+ "<if test='tipoProgramma != null'> and piatri.TIPROG= #{tipoProgramma}</if>"
			+ "<if test='codiceCUI != null'> and UPPER(inttri.CUIINT) like #{codiceCUI}</if>"
			+ "<if test='descrizione != null'> and UPPER(inttri.DESINT) like #{descrizione}</if>"
			+ "<if test='codiceFiscaleSA != null'> and UPPER(uffint.CFEIN) = #{codiceFiscaleSA}</if>"
			+ " ORDER BY inttri.CUIINT, piatri.DADOZI DESC, piatri.DAPTRI DESC"
			+ "</script>")
	@Results({
		@Result(property = "anno", column = "ANNTRI"),
		@Result(property = "annualitaRiferimento", column = "ANNRIF"),
		@Result(property = "cui", column = "CUIINT"),
		@Result(property = "descrizione", column = "DESINT"),
		@Result(property = "codiceFiscaleRUP", column = "CFTEC"),
		@Result(property = "nomeRUP", column = "NOMETEI"),
		@Result(property = "cognomeRUP", column = "COGTEI"),
		@Result(property = "codiceLavoro", column = "CODINT"),
		@Result(property = "cup", column = "CUPPRG"),
		@Result(property = "cpv", column = "CODCPV"),
		@Result(property = "associato", column = "CEFINT")
	})
	public List<InterventoAcquistoEntry> getInterventiAcquisti(@Param("tipoProgramma") Long tipoProgramma, @Param("codiceCUI") String codiceCUI, @Param("descrizione") String descrizione, @Param("codiceFiscaleSA") String codiceFiscaleSA);
	
	@Select("select piatri.ANNTRI, inttri.ANNRIF, inttri.CUIINT, inttri.CUPPRG, inttri.DESINT, inttri.TOTINT, tecni.NOMTEC, " +
    	  "inttri.PRGINT, inttri.COMINT, inttri.NUTS, inttri.TIPINT, " +
    	  "inttri.DV1TRI, inttri.DV2TRI, inttri.DV3TRI, inttri.DV9TRI, " +
    	  "inttri.MU1TRI, inttri.MU2TRI, inttri.MU3TRI, inttri.MU9TRI, " +
    	  "inttri.PR1TRI, inttri.PR2TRI, inttri.PR3TRI, inttri.PR9TRI, " +
    	  "inttri.BI1TRI, inttri.BI2TRI, inttri.BI3TRI, inttri.BI9TRI, " +
    	  "inttri.AP1TRI, inttri.AP2TRI, inttri.AP3TRI, inttri.AP9TRI, " +
    	  "inttri.IM1TRI, inttri.IM2TRI, inttri.IM3TRI, inttri.IM9TRI, " +
    	  "inttri.AL1TRI, inttri.AL2TRI, inttri.AL3TRI, inttri.AL9TRI " +
    	  "from inttri join piatri on inttri.contri=piatri.contri left join tecni on inttri.codrup=tecni.codtec " +
    	  "where piatri.norma=2 and UPPER(inttri.CUIINT) = #{codiceCUI} and (piatri.DADOZI is not null OR piatri.DAPTRI is not null) " +
    	  "ORDER BY inttri.CUIINT, piatri.DADOZI DESC, piatri.DAPTRI DESC")
	@Results({
		@Result(property = "cui", column = "CUIINT"),
		@Result(property = "cup", column = "CUPPRG"),
		@Result(property = "descrizione", column = "DESINT"),
		@Result(property = "annoProgramma", column = "ANNTRI"),
		@Result(property = "annualita", column = "ANNRIF"),
		@Result(property = "importoTotale", column = "TOTINT"),
		@Result(property = "rup", column = "NOMTEC"),
		@Result(property = "priorita", column = "PRGINT"),
		@Result(property = "istat", column = "COMINT"),
		@Result(property = "nuts", column = "NUTS"),
		@Result(property = "risorseVincolatePerLegge1", column = "DV1TRI"),
		@Result(property = "risorseVincolatePerLegge2", column = "DV2TRI"),
		@Result(property = "risorseVincolatePerLegge3", column = "DV3TRI"),
		@Result(property = "risorseVincolatePerLeggeSucc", column = "DV9TRI"),
		@Result(property = "risorseMutuo1", column = "MU1TRI"),
		@Result(property = "risorseMutuo2", column = "MU2TRI"),
		@Result(property = "risorseMutuo3", column = "MU3TRI"),
		@Result(property = "risorseMutuoSucc", column = "MU9TRI"),
		@Result(property = "risorsePrivati1", column = "PR1TRI"),
		@Result(property = "risorsePrivati2", column = "PR2TRI"),
		@Result(property = "risorsePrivati3", column = "PR3TRI"),
		@Result(property = "risorsePrivatiSucc", column = "PR9TRI"),
		@Result(property = "risorseBilancio1", column = "BI1TRI"),
		@Result(property = "risorseBilancio2", column = "BI2TRI"),
		@Result(property = "risorseBilancio3", column = "BI3TRI"),
		@Result(property = "risorseBilancioSucc", column = "BI9TRI"),
		@Result(property = "risorseArt3_1", column = "AP1TRI"),
		@Result(property = "risorseArt3_2", column = "AP2TRI"),
		@Result(property = "risorseArt3_3", column = "AP3TRI"),
		@Result(property = "risorseArt3_Succ", column = "AP9TRI"),
		@Result(property = "risorseImmobili1", column = "IM1TRI"),
		@Result(property = "risorseImmobili2", column = "IM2TRI"),
		@Result(property = "risorseImmobili3", column = "IM3TRI"),
		@Result(property = "risorseImmobiliSucc", column = "IM9TRI"),
		@Result(property = "risorseAltro1", column = "AL1TRI"),
		@Result(property = "risorseAltro2", column = "AL2TRI"),
		@Result(property = "risorseAltro3", column = "AL3TRI"),
		@Result(property = "risorseAltroSucc", column = "AL9TRI")
	})
	public List<DettaglioInterventoAcquistoEntry> getInterventoAcquisto(@Param("codiceCUI") String codiceCUI);
	
}
