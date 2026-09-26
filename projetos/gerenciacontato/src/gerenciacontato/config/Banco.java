package gerenciacontato.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Banco {
    private static final String URL = "jdbc:postgresql://ep-crimson-snow-ac666qfm-pooler.sa-east-1.aws.neon.tech/neondb?user=neondb_owner&password=npg_DnQUcGYqH25A&sslmode=require&channelBinding=require";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "postgres";

    public static Connection conectar() {
        Connection con = null;
        
        try {
            con = DriverManager.getConnection(URL);
        } catch (SQLException ex) {
            System.out.printf("Conexao com banco de dados falhou");
        } 
        
        return con;
    }
}
