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
 * @author Diego Perez, Adrian Cava
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

    /**
     * Genera automáticamente el siguiente número de plaza disponible.
     * Si la tabla está vacía devuelve 1.
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

    /**
     * Busca la primera plaza con estado LIBRE en la base de datos.
     *
     * @return El número de la plaza libre encontrada.
     * @throws Exception Si no hay plazas libres disponibles o hay un error de conexión.
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
     * Carga en una lista el estado actual de todas las plazas registradas.
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

    /**
     * Establece el estado de la plaza a partir de un valor de texto.
     *
     * @param op El estado a asignar (LIBRE, OCUPADA, FUERA_DE_SERVICIO).
     * @throws IllegalArgumentException Si el valor de {@code op} no es un estado válido.
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
     * Verifica si la plaza actual ya existe en la base de datos.
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
     * El número se genera automáticamente y el estado inicial siempre es LIBRE.
     *
     * @throws Exception Si la plaza ya existe o hay un error de inserción.
     */
    public void altaPlaza() throws Exception {
        // Auto-generar número de plaza
        this.numeroPlaza = siguientePlaza();

        // El estado siempre es LIBRE al dar de alta una plaza
        this.estado = EstadoPlaza.LIBRE;

        if (existePlaza()) {
            throw new Exception("La plaza " + String.format("%06d", numeroPlaza) + " ya existe");
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
     * Cambia el estado de la plaza a FUERA_DE_SERVICIO en la base de datos.
     * Solo se puede dar de baja una plaza que esté LIBRE; si está OCUPADA
     * (hay una reserva activa) se lanza excepción para evitar inconsistencias.
     *
     * @throws Exception Si la plaza está ocupada o hay un error al actualizar.
     */
    public void bajaPlaza() throws Exception {

        if (!existePlaza()) {
            throw new Exception("La plaza " + String.format("%06d", numeroPlaza) + " no existe");
        }

        // Comprobar que no está ocupada antes de darla de baja
        String sqlCheck = "SELECT estado FROM plaza WHERE numeroPlaza = ?";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sqlCheck)) {
            pst.setInt(1, numeroPlaza);
            ResultSet rs = pst.executeQuery();
            if (!rs.next()) {
                throw new Exception("La plaza " + String.format("%06d", numeroPlaza) + " no existe");
            }
            if ("OCUPADA".equals(rs.getString(1))) {
                throw new Exception("No se puede dar de baja la plaza " +
                        String.format("%06d", numeroPlaza) + " porque está OCUPADA");
            }
        } catch (SQLException e) {
            throw new Exception("Error en bajaPlaza!!", e);
        }

        String sql = "UPDATE plaza SET estado = 'FUERA_DE_SERVICIO' WHERE numeroPlaza = ?";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {
            pst.setInt(1, numeroPlaza);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error en bajaPlaza!!", e);
        }
    }

    /**
     * Modifica el estado de una plaza existente.
     * No se permite cambiar manualmente a OCUPADA, ya que ese estado
     * lo gestiona el sistema al crear una reserva.
     *
     * @param nuevoEstado El nuevo estado a asignar (LIBRE o FUERA_DE_SERVICIO).
     * @throws Exception Si el estado es inválido, la plaza no existe o está OCUPADA.
     */
    public void modificarEstadoPlaza(String nuevoEstado) throws Exception {

        if ("OCUPADA".equals(nuevoEstado)) {
            throw new Exception("No se puede asignar manualmente el estado OCUPADA. " +
                    "Ese estado lo gestiona el sistema al crear una reserva.");
        }

        // Validar el estado antes de la consulta (setEstado lanza IllegalArgumentException)
        setEstado(nuevoEstado);

        if (!existePlaza()) {
            throw new Exception("La plaza " + String.format("%06d", numeroPlaza) + " no existe");
        }

        String sql = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {
            pst.setString(1, nuevoEstado);
            pst.setInt(2, numeroPlaza);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error en modificarEstadoPlaza!!", e);
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
     * Clase interna para visualizar datos extendidos de la plaza en listados.
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
