
import modelo.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Reserva}.
 * <p>
 * Cubre:
 * - Constructores y getters.
 * - altaReserva: asignación automática de número y plaza, y marca plaza como OCUPADA.
 * - altaReserva: lanza excepción si no hay plazas libres.
 * - bajaReserva: libera la plaza, calcula coste base correctamente.
 * - bajaReserva: aplica descuento VIP.
 * - bajaReserva: añade precioCarga en plaza eléctrica.
 * - bajaReserva: lanza excepción si la reserva ya fue cerrada.
 * - bajaReserva: lanza excepción si los datos no coinciden.
 * - existeReserva: con clave compuesta correcta e incorrecta.
 * - listadoReserva: lista vacía y con datos.
 */
class ReservaTest {

    private Connection con;

    @BeforeEach
    void setUp() throws Exception {
        con = TestDB.setUp();
    }

    @AfterEach
    void tearDown() throws Exception {
        TestDB.tearDown();
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /**
     * Inserta un usuario normal en la BD.
     */
    private void insertarUsuario(String dni) throws Exception {
        con.createStatement().execute(
                "INSERT INTO usuario VALUES('" + dni + "','Test','MAT001',NULL)");
    }

    /**
     * Inserta un usuario VIP con descuento.
     */
    private void insertarUsuarioVIP(String dni, double descuento) throws Exception {
        con.createStatement().execute(
                "INSERT INTO usuario VALUES('" + dni + "','VIP','VIP001'," + descuento + ")");
    }

    /**
     * Inserta una plaza LIBRE normal. Devuelve su número.
     */
    private int insertarPlazaLibre() throws Exception {
        int num = siguientePlazaManual();
        con.createStatement().execute(
                "INSERT INTO plaza VALUES(" + num + ",'LIBRE',NULL,NULL)");
        return num;
    }

    /**
     * Inserta una plaza LIBRE eléctrica con precioCarga. Devuelve su número.
     */
    private int insertarPlazaElectrica(double precioCarga) throws Exception {
        int num = siguientePlazaManual();
        con.createStatement().execute(
                "INSERT INTO plaza VALUES(" + num + ",'LIBRE',NULL," + precioCarga + ")");
        return num;
    }

    /**
     * Inserta una plaza LIBRE minusválida con descuento. Devuelve su número.
     */
    private int insertarPlazaMinusvalida(double descuento) throws Exception {
        int num = siguientePlazaManual();
        con.createStatement().execute(
                "INSERT INTO plaza VALUES(" + num + ",'LIBRE'," + descuento + ",NULL)");
        return num;
    }

    private int siguientePlazaManual() throws Exception {
        ResultSet rs = con.createStatement()
                .executeQuery("SELECT COALESCE(MAX(numeroPlaza),0)+1 FROM plaza");
        rs.next();
        return rs.getInt(1);
    }

    /**
     * Inserta una reserva con fechaHoraEntrada retrasada {@code minutosAtras} minutos
     * para poder calcular coste sin esperar tiempo real.
     */
    private Reserva insertarReservaAbierta(String dni, int numPlaza, int minutosAtras) throws Exception {
        int numRes = 1;
        LocalDateTime entrada = LocalDateTime.now().minusMinutes(minutosAtras);
        con.createStatement().execute(
                "INSERT INTO reserva VALUES(" + numRes + ",'" + dni + "'," + numPlaza
                        + ",NULL,'" + entrada + "',NULL)");
        // Marcar plaza como OCUPADA
        con.createStatement().execute(
                "UPDATE plaza SET estado='OCUPADA' WHERE numeroPlaza=" + numPlaza);

        Reserva r = new Reserva(numRes, dni, numPlaza);
        return r;
    }

    // -----------------------------------------------------------------------
    // Constructores y getters
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor por defecto inicializa coste a 0.0 y fechaHoraSalida a null")
    void constructorPorDefecto() {
        Reserva r = new Reserva();
        assertEquals(0.0, r.getCoste(), 0.001);
        assertNull(r.getFechaHoraSalida());
    }

    @Test
    @DisplayName("Constructor parametrizado asigna los tres campos clave")
    void constructorParametrizado() {
        Reserva r = new Reserva(5, "12345678A", 3);
        assertEquals(5, r.getNumeroReserva());
        assertEquals("12345678A", r.getDniCliente());
        assertEquals(3, r.getNumeroPlaza());
    }

    // -----------------------------------------------------------------------
    // altaReserva
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("altaReserva asigna numeroReserva = 1 en tabla vacía")
    void altaReservaNumeroAutomatico() throws Exception {
        insertarUsuario("11111111A");
        insertarPlazaLibre();

        Reserva r = new Reserva();
        r.setDniCliente("11111111A");
        r.altaReserva();

        assertEquals(1, r.getNumeroReserva());
    }

    @Test
    @DisplayName("altaReserva asigna la primera plaza LIBRE disponible")
    void altaReservaAsignaPlazaLibre() throws Exception {
        insertarUsuario("22222222B");
        int numPlaza = insertarPlazaLibre();

        Reserva r = new Reserva();
        r.setDniCliente("22222222B");
        r.altaReserva();

        assertEquals(numPlaza, r.getNumeroPlaza());
    }

    @Test
    @DisplayName("altaReserva marca la plaza asignada como OCUPADA")
    void altaReservaMarcaPlazaOcupada() throws Exception {
        insertarUsuario("33333333C");
        int numPlaza = insertarPlazaLibre();

        Reserva r = new Reserva();
        r.setDniCliente("33333333C");
        r.altaReserva();

        ResultSet rs = con.createStatement()
                .executeQuery("SELECT estado FROM plaza WHERE numeroPlaza=" + numPlaza);
        rs.next();
        assertEquals("OCUPADA", rs.getString(1));
    }

    @Test
    @DisplayName("altaReserva lanza excepción si no hay plazas libres")
    void altaReservaSinPlazasLibres() throws Exception {
        insertarUsuario("44444444D");
        // No insertamos ninguna plaza
        Reserva r = new Reserva();
        r.setDniCliente("44444444D");
        assertThrows(Exception.class, r::altaReserva);
    }

    // -----------------------------------------------------------------------
    // bajaReserva — cálculo de coste
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("bajaReserva calcula coste base proporcional al minuto (sin descuentos)")
    void bajaReservaCosteBase() throws Exception {
        // 60 minutos → 1 hora → 2.50 €
        insertarUsuario("55555555E");
        int numPlaza = insertarPlazaLibre();
        Reserva r = insertarReservaAbierta("55555555E", numPlaza, 60);

        r.bajaReserva();

        // Tolerancia de ±1 minuto de ejecución → ±0.042 €
        assertEquals(2.5, r.getCoste(), 0.05);
    }

    @Test
    @DisplayName("bajaReserva aplica descuento VIP correctamente")
    void bajaReservaDescuentoVIP() throws Exception {
        // 60 min → 2.50 € base. VIP 50% → 1.25 €
        insertarUsuarioVIP("66666666F", 50.0);
        int numPlaza = insertarPlazaLibre();
        Reserva r = insertarReservaAbierta("66666666F", numPlaza, 60);

        r.bajaReserva();

        assertEquals(1.25, r.getCoste(), 0.05);
    }

    @Test
    @DisplayName("bajaReserva suma precioCarga en plaza eléctrica")
    void bajaReservaPrecioCarga() throws Exception {
        // 60 min → 2.50 € + 3.00 € carga = 5.50 €
        insertarUsuario("77777777G");
        int numPlaza = insertarPlazaElectrica(3.00);
        Reserva r = insertarReservaAbierta("77777777G", numPlaza, 60);

        r.bajaReserva();

        assertEquals(5.50, r.getCoste(), 0.05);
    }

    @Test
    @DisplayName("bajaReserva aplica descuento plaza minusválida correctamente")
    void bajaReservaDescuentoPlazaMinusvalida() throws Exception {
        // 60 min → 2.50 € base. Descuento plaza 20% → 2.00 €
        insertarUsuario("88888888H");
        int numPlaza = insertarPlazaMinusvalida(20.0);
        Reserva r = insertarReservaAbierta("88888888H", numPlaza, 60);

        r.bajaReserva();

        assertEquals(2.00, r.getCoste(), 0.05);
    }

    @Test
    @DisplayName("bajaReserva libera la plaza (la pone LIBRE)")
    void bajaReservaLiberaPlaza() throws Exception {
        insertarUsuario("12121212A");
        int numPlaza = insertarPlazaLibre();
        Reserva r = insertarReservaAbierta("12121212A", numPlaza, 30);

        r.bajaReserva();

        ResultSet rs = con.createStatement()
                .executeQuery("SELECT estado FROM plaza WHERE numeroPlaza=" + numPlaza);
        rs.next();
        assertEquals("LIBRE", rs.getString(1));
    }

    @Test
    @DisplayName("bajaReserva establece fechaHoraSalida en la instancia")
    void bajaReservaEstableceFechaSalida() throws Exception {
        insertarUsuario("13131313B");
        int numPlaza = insertarPlazaLibre();
        Reserva r = insertarReservaAbierta("13131313B", numPlaza, 10);

        r.bajaReserva();

        assertNotNull(r.getFechaHoraSalida());
    }

    // -----------------------------------------------------------------------
    // bajaReserva — casos de error
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("bajaReserva lanza excepción si la reserva no existe")
    void bajaReservaInexistente() {
        Reserva r = new Reserva(999, "00000000Z", 999);
        assertThrows(Exception.class, r::bajaReserva);
    }

    @Test
    @DisplayName("bajaReserva lanza excepción si la reserva ya fue cerrada")
    void bajaReservaYaCerrada() throws Exception {
        insertarUsuario("14141414C");
        int numPlaza = insertarPlazaLibre();
        Reserva r = insertarReservaAbierta("14141414C", numPlaza, 30);

        r.bajaReserva(); // primera baja → OK

        // Segunda baja → excepción
        Exception ex = assertThrows(Exception.class, r::bajaReserva);
        assertTrue(ex.getMessage().contains("ya fue cerrada"));
    }

    // -----------------------------------------------------------------------
    // existeReserva
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existeReserva devuelve true con la clave compuesta correcta")
    void existeReservaTrue() throws Exception {
        insertarUsuario("15151515D");
        int numPlaza = insertarPlazaLibre();
        Reserva r = insertarReservaAbierta("15151515D", numPlaza, 5);
        assertTrue(r.existeReserva());
    }

    @Test
    @DisplayName("existeReserva devuelve false si el DNI no coincide")
    void existeReservaDniIncorrecto() throws Exception {
        insertarUsuario("16161616E");
        insertarUsuario("17171717F");
        int numPlaza = insertarPlazaLibre();
        insertarReservaAbierta("16161616E", numPlaza, 5);

        Reserva r = new Reserva(1, "17171717F", numPlaza); // DNI diferente
        assertFalse(r.existeReserva());
    }

    @Test
    @DisplayName("existeReserva devuelve false si el número de plaza no coincide")
    void existeReservaPlazaIncorrecta() throws Exception {
        insertarUsuario("18181818G");
        int numPlaza = insertarPlazaLibre();
        insertarReservaAbierta("18181818G", numPlaza, 5);

        Reserva r = new Reserva(1, "18181818G", numPlaza + 99); // plaza diferente
        assertFalse(r.existeReserva());
    }

    // -----------------------------------------------------------------------
    // listadoReserva
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("listadoReserva devuelve lista vacía si no hay reservas")
    void listadoReservaVacio() throws Exception {
        List<Reserva> lista = new ArrayList<>();
        Reserva.listadoReserva(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("listadoReserva devuelve todas las reservas insertadas")
    void listadoReservaConDatos() throws Exception {
        insertarUsuario("19191919H");
        insertarUsuario("20202020I");
        int p1 = insertarPlazaLibre();
        int p2 = insertarPlazaLibre();
        insertarReservaAbierta("19191919H", p1, 10);

        // Segunda reserva manual con numeroReserva=2
        con.createStatement().execute(
                "INSERT INTO reserva VALUES(2,'20202020I'," + p2
                        + ",NULL,'" + LocalDateTime.now().minusMinutes(5) + "',NULL)");

        List<Reserva> lista = new ArrayList<>();
        Reserva.listadoReserva(lista);
        assertEquals(2, lista.size());
    }
}
