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
    // CORREGIDO: el constructor no debe tocar el campo static
    public ConexionBD() {
    }

    /* Métodos getters & setters **********************************************/
    public static Connection getConexionBD() {
        return conexionBD;
    }

    /* Métodos ****************************************************************/
    private static void crearTablas() throws Exception {
        String sql;
        try (Statement st = conexionBD.createStatement()) {
            sql = "CREATE TABLE IF NOT EXISTS usuario ("
                    + "DNI VARCHAR(9) PRIMARY KEY, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "matricula VARCHAR(10) NOT NULL, "
                    + "descuento DECIMAL(5,2))";
            st.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS plaza("
                    + "numeroPlaza INT PRIMARY KEY, "
                    + "estado varchar(20) NOT NULL, "
                    + "descuento DECIMAL(5,2), "
                    + "precioCarga DECIMAL(5,2))";
            st.executeUpdate(sql);

            // CORREGIDO: eliminado UNIQUE en numeroPlaza.
            // Con UNIQUE solo podría haber UNA reserva por plaza en toda la historia;
            // al liberar una plaza y volver a reservarla, la INSERT fallaba.
            sql = "CREATE TABLE IF NOT EXISTS reserva("
                    + "numeroReserva INT PRIMARY KEY, "
                    + "dniCliente VARCHAR(9) NOT NULL, "
                    + "numeroPlaza INT NOT NULL, "
                    + "fechaHoraSalida DATETIME, "
                    + "fechaHoraEntrada DATETIME NOT NULL, "
                    + "coste DECIMAL(6,2), "
                    + "FOREIGN KEY (dniCliente) REFERENCES usuario(DNI), "
                    + "FOREIGN KEY (numeroPlaza) REFERENCES plaza(numeroPlaza))";
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
