package it.maggioli.eldasoft.wsprogrammi.dao;

import it.maggioli.eldasoft.wsprogrammi.vo.ControlloEntry;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

public interface SqlMapper {
	
	
    class PureSqlProvider {
        public String sql(String sql) {
            return sql;
        }

        public String sqlObject(String sql) {
            return sql;
        }
        
        public String count(String from) {
            return "SELECT count(*) FROM " + from;
        }
    }

    
    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public List<Map<String,Object>> select(String sql);

    @SelectProvider(type = PureSqlProvider.class, method = "count")
    public Integer count(String from);

    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public Integer execute(String query);

    @SelectProvider(type = PureSqlProvider.class, method = "sqlObject")
    public String executeReturnString(String query);
    
    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public Double executeReturnDouble(String query);
    
    @SelectProvider(type = PureSqlProvider.class, method = "sqlObject")
    public Date executeReturnDate(String query);
    /**
	 * Restituisce il valore richiesto dalla chiave salvato nella w_config 
	 * 
	 * @param codapp
	 *            codapp
	 * @param chiave
	 *            chiave
	 * @return valore valore
	 */
	@Select("select valore from w_config where UPPER(codapp) = #{codapp} AND chiave = #{chiave}")
	public String getConfigValue(@Param("codapp") String codapp, @Param("chiave") String chiave);

	@Select("select NUM, SEZIONE, TITOLO, MSG, TIPO from WS_CONTROLLI where CODAPP='PT' and CODFUNZIONE= #{codFunzione} and entita= #{entita} and (tipo = 'E' or tipo = 'W') order by NUM")
	@Results({
		@Result(property = "numero", column = "NUM"),
		@Result(property = "sezione", column = "SEZIONE"),
		@Result(property = "titolo", column = "TITOLO"),
		@Result(property = "messaggio", column = "MSG"),
		@Result(property = "tipo", column = "TIPO")
	})
	public List<ControlloEntry> getControlli(@Param("codFunzione") String codFunzione, @Param("entita") String entita);

}