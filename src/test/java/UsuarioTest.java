import modelo.Usuario;
import modelo.UsuarioVIP;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para {@link Usuario} y {@link UsuarioVIP}.
 * <p>
 * Cubre:
 * - Constructores y getters.
 * - altaUsuario: alta correcta y duplicado.
 * - bajaUsuario: baja correcta.
 * - existeUsuario: usuario existente e inexistente.
 * - modificarUsuario: actualización de datos.
 * - listadoUsuario: lista vacía y con datos.
 * - UsuarioVIP: alta con descuento, getter de descuento.
 */
class UsuarioTest {

    @BeforeEach
    void setUp() throws Exception {
        TestDB.setUp();
    }

    @AfterEach
    void tearDown() throws Exception {
        TestDB.tearDown();
    }

    // -----------------------------------------------------------------------
    // Constructores y getters
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Constructor por defecto inicializa campos vacíos")
    void constructorPorDefecto() {
        Usuario u = new Usuario();
        assertEquals("", u.getDni());
        assertEquals("", u.getNombre());
        assertEquals("", u.getMatricula());
    }

    @Test
    @DisplayName("Constructor con DNI inicializa el DNI correctamente")
    void constructorConDni() {
        Usuario u = new Usuario("12345678A");
        assertEquals("12345678A", u.getDni());
    }

    // -----------------------------------------------------------------------
    // altaUsuario
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("altaUsuario inserta correctamente un usuario nuevo")
    void altaUsuarioCorrecta() throws Exception {
        Usuario u = new Usuario();
        u.setDni("11111111A");
        u.setNombre("Ana");
        u.setMatricula("1234ABC");
        u.altaUsuario();
        assertTrue(u.existeUsuario());
    }

    @Test
    @DisplayName("altaUsuario lanza excepción si el usuario ya existe")
    void altaUsuarioDuplicado() throws Exception {
        Usuario u = new Usuario();
        u.setDni("22222222B");
        u.setNombre("Luis");
        u.setMatricula("5678DEF");
        u.altaUsuario();

        Usuario duplicado = new Usuario();
        duplicado.setDni("22222222B");
        duplicado.setNombre("Luis");
        duplicado.setMatricula("5678DEF");

        Exception ex = assertThrows(Exception.class, duplicado::altaUsuario);
        assertTrue(ex.getMessage().contains("ya existe"));
    }

    // -----------------------------------------------------------------------
    // bajaUsuario
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("bajaUsuario elimina el usuario de la BD")
    void bajaUsuarioCorrecta() throws Exception {
        Usuario u = new Usuario();
        u.setDni("33333333C");
        u.setNombre("Marta");
        u.setMatricula("9999XYZ");
        u.altaUsuario();
        u.bajaUsuario();
        assertFalse(u.existeUsuario());
    }

    // -----------------------------------------------------------------------
    // existeUsuario
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("existeUsuario devuelve false para un DNI no registrado")
    void existeUsuarioFalse() throws Exception {
        Usuario u = new Usuario("99999999Z");
        assertFalse(u.existeUsuario());
    }

    @Test
    @DisplayName("existeUsuario devuelve true tras el alta")
    void existeUsuarioTrue() throws Exception {
        Usuario u = new Usuario();
        u.setDni("44444444D");
        u.setNombre("Pedro");
        u.setMatricula("AAAA000");
        u.altaUsuario();
        assertTrue(u.existeUsuario());
    }

    // -----------------------------------------------------------------------
    // modificarUsuario
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("modificarUsuario actualiza nombre y matrícula en la BD")
    void modificarUsuarioCorrecta() throws Exception {
        Usuario u = new Usuario();
        u.setDni("55555555E");
        u.setNombre("Carlos");
        u.setMatricula("BBBB111");
        u.altaUsuario();

        u.modificarUsuario("Carlos Nuevo", "ZZZZ999");

        ResultSet rs = datos.ConexionBD.getConexionBD()
                .createStatement()
                .executeQuery("SELECT nombre, matricula FROM usuario WHERE DNI='55555555E'");
        rs.next();
        assertEquals("Carlos Nuevo", rs.getString(1));
        assertEquals("ZZZZ999", rs.getString(2));
    }

    // -----------------------------------------------------------------------
    // listadoUsuario
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("listadoUsuario devuelve lista vacía si no hay usuarios")
    void listadoUsuarioVacio() throws Exception {
        List<Usuario.UsuarioListado> lista = new ArrayList<>();
        Usuario.listadoUsuario(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("listadoUsuario devuelve todos los usuarios insertados")
    void listadoUsuarioConDatos() throws Exception {
        Usuario u1 = new Usuario();
        u1.setDni("66666666F");
        u1.setNombre("Eva");
        u1.setMatricula("A1");
        u1.altaUsuario();

        Usuario u2 = new Usuario();
        u2.setDni("77777777G");
        u2.setNombre("Juan");
        u2.setMatricula("B2");
        u2.altaUsuario();

        List<Usuario.UsuarioListado> lista = new ArrayList<>();
        Usuario.listadoUsuario(lista);
        assertEquals(2, lista.size());
    }

    // -----------------------------------------------------------------------
    // UsuarioVIP
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("UsuarioVIP.altaUsuario persiste el descuento en la BD")
    void altaUsuarioVIPConDescuento() throws Exception {
        UsuarioVIP vip = new UsuarioVIP();
        vip.setDni("88888888H");
        vip.setNombre("Sofía VIP");
        vip.setMatricula("VIP001");
        vip.setDescuento(25.0);
        vip.altaUsuario();

        ResultSet rs = datos.ConexionBD.getConexionBD()
                .createStatement()
                .executeQuery("SELECT descuento FROM usuario WHERE DNI='88888888H'");
        rs.next();
        assertEquals(25.0, rs.getDouble(1), 0.001);
    }

    @Test
    @DisplayName("UsuarioVIP.getDescuento devuelve el valor asignado")
    void usuarioVIPGetDescuento() {
        UsuarioVIP vip = new UsuarioVIP();
        vip.setDescuento(15.5);
        assertEquals(15.5, vip.getDescuento(), 0.001);
    }

    @Test
    @DisplayName("UsuarioVIP lanza excepción si el usuario ya existe")
    void altaUsuarioVIPDuplicado() throws Exception {
        UsuarioVIP vip = new UsuarioVIP();
        vip.setDni("99999999I");
        vip.setNombre("Repeat");
        vip.setMatricula("REP001");
        vip.setDescuento(10.0);
        vip.altaUsuario();

        UsuarioVIP duplicado = new UsuarioVIP();
        duplicado.setDni("99999999I");
        duplicado.setNombre("Repeat");
        duplicado.setMatricula("REP001");
        duplicado.setDescuento(10.0);

        Exception ex = assertThrows(Exception.class, duplicado::altaUsuario);
        assertTrue(ex.getMessage().contains("ya existe"));
    }
}