package datos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Clase de utilidad encargada de gestionar la conexión con la base de datos MySQL.
 * Proporciona métodos estáticos para inicializar la conexión, crear la estructura
 * de tablas necesaria al arrancar y liberar los recursos al finalizar la ejecución.
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.0
 */
public class ConexionBD {

    /* Atributos **************************************************************/
    private static Connection conexionBD;

    /* Constructores **********************************************************/
    // CORREGIDO: el constructor no debe tocar el campo static

    /**
     * Constructor por defecto.
     */
    public ConexionBD() {
    }

    /* Métodos getters & setters **********************************************/
    public static Connection getConexionBD() {
        return conexionBD;
    }

    /* Métodos ****************************************************************/

    /**
     * Ejecuta las sentencias DDL para crear las tablas necesarias (usuario, plaza y reserva)
     * en el esquema de base de datos si estas aún no existen.
     * * @throws Exception Si ocurre un error al ejecutar las consultas SQL de creación.
     */
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
            // CORREGIDO: clave primaria compuesta (numeroReserva, dniCliente, numeroPlaza),
            // que refleja correctamente el modelo de negocio: una reserva la identifica
            // de forma unívoca la combinación de los tres campos.
            sql = "CREATE TABLE IF NOT EXISTS reserva("
                    + "numeroReserva INT NOT NULL, "
                    + "dniCliente VARCHAR(9) NOT NULL, "
                    + "numeroPlaza INT NOT NULL, "
                    + "fechaHoraSalida DATETIME, "
                    + "fechaHoraEntrada DATETIME NOT NULL, "
                    + "coste DECIMAL(6,2), "
                    + "PRIMARY KEY (numeroReserva, dniCliente, numeroPlaza), "
                    + "FOREIGN KEY (dniCliente) REFERENCES usuario(DNI) ON DELETE CASCADE, "
                    + "FOREIGN KEY (numeroPlaza) REFERENCES plaza(numeroPlaza) ON DELETE CASCADE)";
            st.executeUpdate(sql);

        } catch (SQLException e) {
            throw new Exception("Error crearTablas()!!", e);
        }
    }

    /**
     * Lee el archivo de configuración 'dbproperties.txt', establece la conexión con el
     * servidor de base de datos y procede a verificar/crear la estructura de tablas.
     * * @throws Exception Si no se encuentra o no se puede leer el archivo de propiedades,
     * o si falla la conexión con el servidor MySQL.
     */
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

    /**
     * Cierra la conexión activa con la base de datos de forma segura para liberar
     * los recursos del sistema.
     * * @throws Exception Si ocurre un error durante el proceso de cierre de la conexión.
     */
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