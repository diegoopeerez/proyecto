package datos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConexionBD {

    /* Atributos **************************************************************/
    private static Connection conexionBD;

    /* Constructores **********************************************************/
    public ConexionBD() {
        conexionBD = null;
    }

    /* Métodos getters & setters **********************************************/
    public static Connection getConexionBD() {
        return conexionBD;
    }

    /* Métodos ****************************************************************/
    private static void crearTablas() throws Exception {
        String sql;
        try (Statement st = conexionBD.createStatement()) {
            sql = "CREATE TABLE Usuario ("
                    +"DNI VARCHAR(9) PRIMARY KEY, "
                    +"nombre VARCHAR(100) NOT NULL, "
                    +"matricula VARCHAR(10) NOT NULL, "
                    +"descuento DECIMAL(5,2))";
            st.executeUpdate(sql);

            sql = "CREATE TABLE reserva("
                    +"numero_reserva INT PRIMARY KEY "
                    +"dni_cliente VARCHAR(9) NOT NULL "
                    +"numero_plaza INT UNIQUE, "
                    +"fecha_hora_salida DATETIME, "
                    +"fecha_hora_entrada DATETIME NOT NULL, "
                    +"coste DECIMAL(6,2), "
                    +"FOREIGN KEY (dni_cliente) REFERENCES usuario(DNI), "
                    +"FOREIGN KEY (numero_plaza) REFERENCES plaza(numero_plaza))";
            st.executeUpdate(sql);

            sql = "CREATE TABLE Plaza("
                    +"numero_plaza INT PRIMARY KEY, "
                    +"estado varchar2(20) NOT NULL, "
                    +"descuento DECIMAL(5,2), "
                    +"precio_carga DECIMAL(5,2))";
            st.executeUpdate(sql);

        } catch (SQLException e) {
            throw new Exception("Error crearTablas()!!", e);
        }
    }

    public static void abrirConexion() throws Exception {
        try (FileInputStream fis = new FileInputStream("dbproperties.txt")) {
            Properties props = new Properties();
            props.load(fis);
            conexionBD = DriverManager.getConnection(
                    props.getProperty("mysql.url"),
                    props.getProperty("mysql.username"),
                    props.getProperty("mysql.password"));
            crearTablas();
        } catch (IOException e) {
            throw new Exception("Error abrirConexion()!! Propiedades!!", e);
        } catch (SQLException e) {
            throw new Exception("Error abrirConexion()!!", e);
        }
    }

    public static void cerrarConexion() throws Exception {
        try {
            if (conexionBD != null) {
                conexionBD.close();
            }
        } catch (SQLException e) {
            throw new Exception("Error cerrarConexion()!!", e);
        }
    }

}
