import modelo.Plaza;
import modelo.PlazaElectrica;
import modelo.PlazaMinusvalida;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Plaza}, {@link PlazaElectrica} y {@link PlazaMinusvalida}.
 * <p>
 * Cubre:
 * - Constructor y getters básicos.
 * - setEstado: valores válidos e inválido.
 * - altaPlaza: numeración automática y estado inicial LIBRE.
 * - bajaPlaza: plaza inexistente y plaza OCUPADA.
 * - modificarEstadoPlaza: estado inválido, intento de OCUPADA manual, plaza inexistente.
 * - listadoPlaza: lista correctamente las plazas insertadas.
 * - PlazaElectrica y PlazaMinusvalida: alta con sus campos específicos.
 */
class PlazaTest {

    @BeforeEach
    void setUp() throws Exception {
        TestDB.setUp();
    }

    @AfterEach
    void tearDown() throws Exception {
        TestDB.tearDown();
    }

    // -----------------------------------------------------------------------
    // Constructor y getters
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor por defecto inicializa numeroPlaza a 0")
    void constructorPorDefecto() {
        Plaza p = new Plaza();
        assertEquals(0, p.getNumeroPlaza());
    }

    @Test
    @DisplayName("Constructor con número inicializa correctamente")
    void constructorConNumero() {
        Plaza p = new Plaza(42);
        assertEquals(42, p.getNumeroPlaza());
    }

    // -----------------------------------------------------------------------
    // setEstado
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("setEstado acepta LIBRE")
    void setEstadoLibre() {
        Plaza p = new Plaza();
        p.setEstado("LIBRE");
        assertEquals("LIBRE", p.getEstado());
    }

    @Test
    @DisplayName("setEstado acepta OCUPADA")
    void setEstadoOcupada() {
        Plaza p = new Plaza();
        p.setEstado("OCUPADA");
        assertEquals("OCUPADA", p.getEstado());
    }

    @Test
    @DisplayName("setEstado acepta FUERA_DE_SERVICIO")
    void setEstadoFueraDeServicio() {
        Plaza p = new Plaza();
        p.setEstado("FUERA_DE_SERVICIO");
        assertEquals("FUERA_DE_SERVICIO", p.getEstado());
    }

    @Test
    @DisplayName("setEstado lanza IllegalArgumentException con valor desconocido")
    void setEstadoInvalido() {
        Plaza p = new Plaza();
        assertThrows(IllegalArgumentException.class, () -> p.setEstado("CERRADA"));
    }

    // -----------------------------------------------------------------------
    // altaPlaza (Plaza normal)
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("altaPlaza asigna numeroPlaza = 1 cuando la tabla está vacía")
    void altaPlazaPrimeraPlaza() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        assertEquals(1, p.getNumeroPlaza());
    }

    @Test
    @DisplayName("altaPlaza incrementa el número en plazas sucesivas")
    void altaPlazaNumeracionAutomatica() throws Exception {
        Plaza p1 = new Plaza();
        p1.altaPlaza();
        Plaza p2 = new Plaza();
        p2.altaPlaza();
        assertEquals(2, p2.getNumeroPlaza());
    }

    @Test
    @DisplayName("altaPlaza fija el estado inicial a LIBRE")
    void altaPlazaEstadoLibre() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        assertEquals("LIBRE", p.getEstado());
    }

    @Test
    @DisplayName("altaPlaza persiste la plaza y existePlaza() devuelve true")
    void altaPlazaPersiste() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        assertTrue(p.existePlaza());
    }

    // -----------------------------------------------------------------------
    // bajaPlaza
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("bajaPlaza lanza excepción si la plaza no existe")
    void bajaPlazaInexistente() {
        Plaza p = new Plaza(999);
        Exception ex = assertThrows(Exception.class, p::bajaPlaza);
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    @DisplayName("bajaPlaza lanza excepción si la plaza está OCUPADA")
    void bajaPlazaOcupada() throws Exception {
        // Insertar plaza manualmente con estado OCUPADA
        Plaza p = new Plaza();
        p.altaPlaza();
        // Cambiar estado a OCUPADA directamente en BD
        datos.ConexionBD.getConexionBD()
                .createStatement()
                .execute("UPDATE plaza SET estado='OCUPADA' WHERE numeroPlaza=" + p.getNumeroPlaza());

        Exception ex = assertThrows(Exception.class, p::bajaPlaza);
        assertTrue(ex.getMessage().contains("OCUPADA"));
    }

    @Test
    @DisplayName("bajaPlaza pone la plaza FUERA_DE_SERVICIO correctamente")
    void bajaPlazaCorrecta() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        p.bajaPlaza();

        // Verificar en BD
        ResultSet rs = datos.ConexionBD.getConexionBD()
                .createStatement()
                .executeQuery("SELECT estado FROM plaza WHERE numeroPlaza=" + p.getNumeroPlaza());
        rs.next();
        assertEquals("FUERA_DE_SERVICIO", rs.getString(1));
    }

    // -----------------------------------------------------------------------
    // modificarEstadoPlaza
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("modificarEstadoPlaza lanza excepción al intentar poner OCUPADA manualmente")
    void modificarEstadoOcupadaManual() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        Exception ex = assertThrows(Exception.class, () -> p.modificarEstadoPlaza("OCUPADA"));
        assertTrue(ex.getMessage().contains("manualmente"));
    }

    @Test
    @DisplayName("modificarEstadoPlaza lanza IllegalArgumentException con estado desconocido")
    void modificarEstadoDesconocido() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        assertThrows(IllegalArgumentException.class, () -> p.modificarEstadoPlaza("INVALIDO"));
    }

    @Test
    @DisplayName("modificarEstadoPlaza lanza excepción si la plaza no existe en BD")
    void modificarEstadoPlazaInexistente() {
        Plaza p = new Plaza(999);
        Exception ex = assertThrows(Exception.class, () -> p.modificarEstadoPlaza("LIBRE"));
        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    @DisplayName("modificarEstadoPlaza cambia correctamente a FUERA_DE_SERVICIO")
    void modificarEstadoAFueraDeServicio() throws Exception {
        Plaza p = new Plaza();
        p.altaPlaza();
        p.modificarEstadoPlaza("FUERA_DE_SERVICIO");
        assertEquals("FUERA_DE_SERVICIO", p.getEstado());
    }

    // -----------------------------------------------------------------------
    // listadoPlaza
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("listadoPlaza devuelve lista vacía si no hay plazas")
    void listadoPlazaVacio() throws Exception {
        List<Plaza.PlazaListado> lista = new ArrayList<>();
        Plaza.listadoPlaza(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("listadoPlaza devuelve todas las plazas insertadas")
    void listadoPlazaConDatos() throws Exception {
        new Plaza().altaPlaza();
        new Plaza().altaPlaza();
        List<Plaza.PlazaListado> lista = new ArrayList<>();
        Plaza.listadoPlaza(lista);
        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("listadoPlaza devuelve las plazas ordenadas por número")
    void listadoPlazaOrdenada() throws Exception {
        new Plaza().altaPlaza(); // número 1
        new Plaza().altaPlaza(); // número 2
        List<Plaza.PlazaListado> lista = new ArrayList<>();
        Plaza.listadoPlaza(lista);
        assertTrue(lista.get(0).getNumeroPlaza() < lista.get(1).getNumeroPlaza());
    }

    // -----------------------------------------------------------------------
    // PlazaElectrica
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("PlazaElectrica.altaPlaza persiste el precioCarga")
    void altaPlazaElectrica() throws Exception {
        PlazaElectrica pe = new PlazaElectrica();
        pe.setPrecioCarga(3.50);
        pe.altaPlaza();

        ResultSet rs = datos.ConexionBD.getConexionBD()
                .createStatement()
                .executeQuery("SELECT precioCarga FROM plaza WHERE numeroPlaza=" + pe.getNumeroPlaza());
        rs.next();
        assertEquals(3.50, rs.getDouble(1), 0.001);
    }

    @Test
    @DisplayName("PlazaElectrica.altaPlaza fija estado LIBRE")
    void altaPlazaElectricaEstadoLibre() throws Exception {
        PlazaElectrica pe = new PlazaElectrica();
        pe.setPrecioCarga(2.0);
        pe.altaPlaza();
        assertEquals("LIBRE", pe.getEstado());
    }

    // -----------------------------------------------------------------------
    // PlazaMinusvalida
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("PlazaMinusvalida.altaPlaza persiste el descuento")
    void altaPlazaMinusvalida() throws Exception {
        PlazaMinusvalida pm = new PlazaMinusvalida();
        pm.setDescuento(20.0);
        pm.altaPlaza();

        ResultSet rs = datos.ConexionBD.getConexionBD()
                .createStatement()
                .executeQuery("SELECT descuento FROM plaza WHERE numeroPlaza=" + pm.getNumeroPlaza());
        rs.next();
        assertEquals(20.0, rs.getDouble(1), 0.001);
    }

    @Test
    @DisplayName("PlazaMinusvalida.altaPlaza fija estado LIBRE")
    void altaPlazaMinusvalidaEstadoLibre() throws Exception {
        PlazaMinusvalida pm = new PlazaMinusvalida();
        pm.setDescuento(15.0);
        pm.altaPlaza();
        assertEquals("LIBRE", pm.getEstado());
    }
}