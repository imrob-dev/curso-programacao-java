package com.oficina.config;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Gerenciador de conexão com o banco de dados PostgreSQL.
 * Carrega credenciais com a biblioteca Dotenv e realiza a verificação
 * e criação automática das tabelas na inicialização caso não existam.
 */
public class Banco {

    private static Dotenv dotenv;
    private static boolean inicializado = false;

    static {
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception ex) {
            System.err.println("Aviso ao carregar dotenv: " + ex.getMessage());
        }
    }

    /**
     * Obtém valor de variável de ambiente do .env ou do sistema operacional.
     */
    public static String getEnv(String key, String defaultValue) {
        String valor = (dotenv != null) ? dotenv.get(key) : null;
        if (valor == null || valor.trim().isEmpty()) {
            valor = System.getenv(key);
        }
        return (valor != null && !valor.trim().isEmpty()) ? valor : defaultValue;
    }

    /**
     * Retorna a URL de conexão configurada no .env.
     */
    public static String getUrl() {
        String url = getEnv("DATABASE_URL", null);
        if (url == null || url.trim().isEmpty()) {
            url = getEnv("DB_URL", null);
        }
        return url;
    }

    public static String getUsuario() {
        return getEnv("DB_USER", null);
    }

    public static String getSenha() {
        return getEnv("DB_PASSWORD", null);
    }

    /**
     * Estabelece conexão com o banco de dados.
     */
    public static Connection conectar() throws SQLException {
        String url = getUrl();
        String usuario = getUsuario();
        String senha = getSenha();

        if (url == null || url.trim().isEmpty()) {
            throw new SQLException("URL do banco de dados não configurada no arquivo .env (DB_URL ou DATABASE_URL).");
        }

        if (usuario != null && senha != null && !usuario.trim().isEmpty()) {
            return DriverManager.getConnection(url, usuario, senha);
        } else {
            return DriverManager.getConnection(url);
        }
    }

    /**
     * Verifica se o banco e as tabelas já foram criados. Se não existirem,
     * cria automaticamente na inicialização.
     * Uma vez validado que as tabelas existem, não repete mais a verificação.
     */
    public static synchronized void inicializarBancoSeNecessario() {
        if (inicializado) {
            return;
        }

        try {
            garantirExistenciaBancoETabelas();
            inicializado = true;
            System.out.println("Banco de dados verificado e pronto para uso.");
        } catch (Exception ex) {
            System.err.println("Erro ao inicializar o banco de dados: " + ex.getMessage());
        }
    }

    private static void garantirExistenciaBancoETabelas() throws SQLException {
        Connection con = null;

        try {
            con = conectar();
        } catch (SQLException ex) {
            // Código 3D000 indica banco inexistente no PostgreSQL
            if ("3D000".equals(ex.getSQLState()) || (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("does not exist"))) {
                criarBancoDeDados();
                con = conectar();
            } else {
                throw ex;
            }
        }

        try {
            if (!tabelasExistem(con)) {
                System.out.println("Tabelas não encontradas. Criando tabelas no banco de dados...");
                criarTabelas(con);
                System.out.println("Tabelas criadas com sucesso!");
            } else {
                System.out.println("Tabelas já existentes no banco de dados.");
            }
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Caso o banco 'oficina' não exista, conecta ao banco padrão 'neondb' e cria o banco 'oficina'.
     */
    private static void criarBancoDeDados() {
        String urlOriginal = getUrl();
        if (urlOriginal == null || !urlOriginal.contains("/oficina")) {
            return;
        }

        String urlManutencao = urlOriginal.replace("/oficina", "/neondb");
        String usuario = getUsuario();
        String senha = getSenha();

        System.out.println("Banco 'oficina' não encontrado. Tentando criar via banco de manutenção...");

        try (Connection conManutencao = (usuario != null && senha != null)
                ? DriverManager.getConnection(urlManutencao, usuario, senha)
                : DriverManager.getConnection(urlManutencao);
             Statement stmt = conManutencao.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE oficina");
            System.out.println("Banco 'oficina' criado com sucesso!");
        } catch (Exception e) {
            System.err.println("Não foi possível criar o banco automaticamente via script: " + e.getMessage());
        }
    }

    /**
     * Verifica se as tabelas principais já existem.
     */
    private static boolean tabelasExistem(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM information_schema.tables "
                + "WHERE table_schema = 'public' "
                + "AND table_name IN ('usuario', 'cliente', 'veiculo', 'ordem_servico')";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total") >= 4;
            }
        }
        return false;
    }

    /**
     * Executa os scripts DDL para criação das tabelas e carga do usuário inicial.
     */
    private static void criarTabelas(Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            // Tabela usuario
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS usuario ("
                    + "    id         SERIAL PRIMARY KEY,"
                    + "    nome       VARCHAR(100) NOT NULL,"
                    + "    login      VARCHAR(30)  NOT NULL UNIQUE,"
                    + "    senha_hash CHAR(64)     NOT NULL,"
                    + "    perfil     VARCHAR(20)  NOT NULL DEFAULT 'ATENDENTE',"
                    + "    ativo      BOOLEAN      NOT NULL DEFAULT TRUE"
                    + ")");

            // Tabela cliente
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS cliente ("
                    + "    id       SERIAL PRIMARY KEY,"
                    + "    nome     VARCHAR(100) NOT NULL,"
                    + "    telefone VARCHAR(20)  NOT NULL,"
                    + "    cpf      VARCHAR(14)  UNIQUE"
                    + ")");

            // Tabela veiculo
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS veiculo ("
                    + "    id         SERIAL PRIMARY KEY,"
                    + "    placa      VARCHAR(8)  NOT NULL UNIQUE,"
                    + "    marca      VARCHAR(50) NOT NULL,"
                    + "    modelo     VARCHAR(50) NOT NULL,"
                    + "    ano        INTEGER,"
                    + "    id_cliente INTEGER NOT NULL REFERENCES cliente(id)"
                    + ")");

            // Tabela ordem_servico
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ordem_servico ("
                    + "    id                 SERIAL PRIMARY KEY,"
                    + "    id_veiculo         INTEGER NOT NULL REFERENCES veiculo(id),"
                    + "    id_usuario         INTEGER NOT NULL REFERENCES usuario(id),"
                    + "    data_abertura      DATE NOT NULL DEFAULT CURRENT_DATE,"
                    + "    data_conclusao     DATE,"
                    + "    descricao_problema VARCHAR(500) NOT NULL,"
                    + "    servico_realizado  VARCHAR(500),
                    + "    valor_mao_obra     NUMERIC(10,2) NOT NULL DEFAULT 0,"
                    + "    status             VARCHAR(20) NOT NULL DEFAULT 'ABERTA'"
                    + ")");

            // Carga do usuário inicial admin / admin123
            stmt.executeUpdate("INSERT INTO usuario (nome, login, senha_hash, perfil) "
                    + "VALUES ('Administrador', 'admin', "
                    + "'240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN') "
                    + "ON CONFLICT (login) DO NOTHING");
        }
    }
}
