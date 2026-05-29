import datos.ConexionBD;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Utilidad compartida por los tests: levanta una base de datos H2 en memoria
 * con el mismo esquema que MySQL (usuario, plaza, reserva) e inyecta la
 * conexión en {@link ConexionBD} mediante reflexión, sin modificar el código
 * de producción.
 */
public class TestDB {

    /**
     * Abre una conexión H2 en memoria, crea las tablas y la inyecta en ConexionBD.
     * Llamar en @BeforeEach de cada clase de test.
     *
     * @return La conexión abierta, por si el test la necesita directamente.
     */
    public static Connection setUp() throws Exception {
        // H2 en modo MySQL-compatible para que las mismas sentencias funcionen
        Connection con = DriverManager.getConnection(
                "jdbc:h2:mem:parkify;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "sa", "");

        try (Statement st = con.createStatement()) {
            // Limpiar entre tests
            st.execute("DROP TABLE IF EXISTS reserva");
            st.execute("DROP TABLE IF EXISTS plaza");
            st.execute("DROP TABLE IF EXISTS usuario");

            st.execute("CREATE TABLE usuario ("
                    + "DNI VARCHAR(9) PRIMARY KEY, "
                    + "nombre VARCHAR(100) NOT NULL, "
                    + "matricula VARCHAR(10) NOT NULL, "
                    + "descuento DECIMAL(5,2))");

            st.execute("CREATE TABLE plaza ("
                    + "numeroPlaza INT PRIMARY KEY, "
                    + "estado VARCHAR(20) NOT NULL, "
                    + "descuento DECIMAL(5,2), "
                    + "precioCarga DECIMAL(5,2))");

            st.execute("CREATE TABLE reserva ("
                    + "numeroReserva INT NOT NULL, "
                    + "dniCliente VARCHAR(9) NOT NULL, "
                    + "numeroPlaza INT NOT NULL, "
                    + "fechaHoraSalida DATETIME, "
                    + "fechaHoraEntrada DATETIME NOT NULL, "
                    + "coste DECIMAL(6,2), "
                    + "PRIMARY KEY (numeroReserva, dniCliente, numeroPlaza), "
                    + "FOREIGN KEY (dniCliente) REFERENCES usuario(DNI) ON DELETE CASCADE, "
                    + "FOREIGN KEY (numeroPlaza) REFERENCES plaza(numeroPlaza) ON DELETE CASCADE)");
        }

        // Inyectar la conexión H2 en el campo privado estático de ConexionBD
        Field field = ConexionBD.class.getDeclaredField("conexionBD");
        field.setAccessible(true);
        field.set(null, con);

        return con;
    }

    /**
     * Cierra la conexión H2. Llamar en @AfterEach.
     */
    public static void tearDown() throws Exception {
        Connection con = ConexionBD.getConexionBD();
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}
