package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
/**
 * Clase que representa el modelo de una Plaza en el sistema.
 * Gestiona tanto la lógica de negocio básica como la persistencia en la base de datos.
 * <p>
 * Esta clase utiliza la conexión {@link datos.ConexionBD} para ejecutar operaciones CRUD.
 * </p>
 *
 * @author  Diego Perez, Adrian Cava
 * @version 1.0
 */
public class Plaza {

    protected int numeroPlaza;
    protected EstadoPlaza estado;


    /**
     * Constructor por defecto. Inicializa el número de plaza en 0.
     */
    public Plaza() {
        numeroPlaza = 0;
    }

    /**
     * Constructor sobrecargado que inicializa la instancia con un número de plaza específico.
     *
     * @param numeroPlaza El identificador numérico de la plaza.
     */
    public Plaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    // NUEVO: genera automáticamente el siguiente número de plaza disponible
    // Formato 000001, 000002... (se almacena como INT, se muestra con formato)
    /**
     * Obtiene el siguiente identificador disponible para una nueva plaza.
     * El cálculo se basa en el máximo valor actual incrementado en uno.
     * Si la tabla está vacía, el método devuelve 1.
     *
     * @return El número de la siguiente plaza a crear.
     * @throws Exception Si ocurre un error durante la consulta SQL.
     */
    public static int siguientePlaza() throws Exception {
        String sql = "SELECT COALESCE(MAX(numeroPlaza), 0) + 1 FROM plaza";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1;
        } catch (SQLException e) {
            throw new Exception("Error en siguientePlaza!!", e);
        }
    }

    // NUEVO: devuelve el número de la primera plaza que esté LIBRE
    // Lanza excepción si no hay ninguna disponible
    /**
     * Busca la primera plaza con estado 'LIBRE' en la base de datos.
     *
     * @return El número de la plaza encontrada.
     * @throws Exception Si no existen plazas libres disponibles o hay un error de conexión.
     */
    public static int plazaLibre() throws Exception {
        String sql = "SELECT numeroPlaza FROM plaza WHERE estado = 'LIBRE' LIMIT 1";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new Exception("No hay plazas libres disponibles");
        } catch (SQLException e) {
            throw new Exception("Error en plazaLibre!!", e);
        }
    }

    /**
     * Carga en una lista proporcionada el estado actual de todas las plazas registradas.
     *
     * @param plazas Lista donde se añadirán los objetos {@link PlazaListado} resultantes.
     * @throws Exception Si ocurre un error al consultar la base de datos.
     */
    public static void listadoPlaza(List<PlazaListado> plazas) throws Exception {

        String sql = "SELECT * from plaza ORDER BY numeroPlaza";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            PlazaListado plazaListado;

            while (rs.next()) {
                plazaListado = new PlazaListado();
                plazaListado.setNumeroPlaza(rs.getInt(1));
                plazaListado.setEstado(rs.getString(2));
                plazaListado.setDescuento(rs.getObject(3, Double.class));
                plazaListado.setPrecioCarga(rs.getObject(4, Double.class));
                plazas.add(plazaListado);
            }

        } catch (SQLException e) {
            throw new Exception("Error en listadoPlaza!!", e);
        }

    }

    public int getNumeroPlaza() {
        return numeroPlaza;
    }

    public void setNumeroPlaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    public String getEstado() {
        return estado.toString();
    }

    // CORREGIDO: añadido default para evitar NullPointerException si el valor
    // de la BD no coincide con ningún caso del switch
    /**
     * Establece el estado de la plaza a partir de un valor de texto.
     *
     * @param op El estado a asignar (LIBRE, OCUPADA, FUERA_DE_SERVICIO).
     * @throws IllegalArgumentException Si el valor de {@code op} no es un estado válido definido en el sistema.
     */
    public void setEstado(String op) {
        switch (op) {
            case "LIBRE":
                estado = EstadoPlaza.LIBRE;
                break;
            case "OCUPADA":
                estado = EstadoPlaza.OCUPADA;
                break;
            case "FUERA_DE_SERVICIO":
                estado = EstadoPlaza.FUERA_DE_SERVICIO;
                break;
            default:
                throw new IllegalArgumentException("Estado de plaza desconocido: " + op);
        }
    }

    /**
     * Verifica si la plaza actual ya existe en la base de datos mediante su número identificador.
     *
     * @return {@code true} si la plaza existe, {@code false} en caso contrario.
     * @throws Exception Si ocurre un error al ejecutar la consulta SQL.
     */
    public boolean existePlaza() throws Exception {

        String sql = "SELECT * from plaza WHERE numeroPlaza = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new Exception("Error en existePlaza!!", e);
        }

    }

    /**
     * Registra una nueva plaza en el sistema.
     * Genera automáticamente el número de plaza y establece valores nulos por defecto
     * para descuento y precio de carga.
     *
     * @throws Exception Si la plaza ya existe o hay un error de inserción.
     */
    public void altaPlaza() throws Exception {

        this.numeroPlaza = siguientePlaza();

        if (existePlaza()) {
            throw new Exception("La plaza ya existe!!");
        }

        String sql = "INSERT INTO plaza VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.setString(2, estado.toString());
            pst.setNull(3, java.sql.Types.DECIMAL);
            pst.setNull(4, java.sql.Types.DECIMAL);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaPlaza!!", e);
        }

    }
    /**
     * Cambia el estado de la plaza a 'FUERA_DE_SERVICIO' en la base de datos.
     *
     * @throws Exception Si ocurre un error al actualizar el registro.
     */
    public void bajaPlaza() throws Exception {

        String sql = "UPDATE plaza SET estado = 'FUERA_DE_SERVICIO' WHERE numeroPlaza = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en bajaPlaza!!", e);
        }
    }
    /**
     * Enumeración que define los estados posibles para una plaza dentro del sistema.
     */
    protected enum EstadoPlaza {
        LIBRE,
        OCUPADA,
        FUERA_DE_SERVICIO
    }
    /**
     * Clase interna que extiende de {@link Plaza} para visualizar datos extendidos
     * de la plaza en listados (como descuentos y precios de carga).
     */
    public static class PlazaListado extends Plaza {

        private Double precioCarga;
        private Double descuento;

        public PlazaListado() {
            precioCarga = null;
            descuento = null;
        }

        public Double getPrecioCarga() {
            return precioCarga;
        }

        public void setPrecioCarga(Double precioCarga) {
            this.precioCarga = precioCarga;
        }

        public Double getDescuento() {
            return descuento;
        }

        public void setDescuento(Double descuento) {
            this.descuento = descuento;
        }
    }

}
