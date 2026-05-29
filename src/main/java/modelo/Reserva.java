package modelo;

import datos.ConexionBD;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que gestiona el ciclo de vida de una reserva de plaza de aparcamiento.
 * Controla la persistencia, el cálculo de costes basado en estancias y descuentos,
 * y la actualización sincronizada del estado de las plazas.
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.0
 */
public class Reserva {

    private static final double PRECIO_HORA = 2.5;

    private int numeroReserva;
    private String dniCliente;
    private int numeroPlaza;
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraEntrada;
    private double coste;

    /**
     * Constructor por defecto. Inicializa una nueva reserva con la hora de entrada actual.
     */
    public Reserva() {
        numeroReserva = 0;
        dniCliente = "";
        numeroPlaza = 0;
        fechaHoraSalida = null;
        fechaHoraEntrada = LocalDateTime.now();
        coste = 0.0;
    }

    /**
     * Constructor sobrecargado para identificar una reserva existente.
     *
     * @param numeroReserva Identificador único de la reserva.
     * @param dniCliente    DNI del cliente que realiza la reserva.
     * @param numeroPlaza   Número de la plaza reservada.
     */
    public Reserva(int numeroReserva, String dniCliente, int numeroPlaza) {
        this.numeroReserva = numeroReserva;
        this.dniCliente = dniCliente;
        this.numeroPlaza = numeroPlaza;
        this.fechaHoraSalida = null;
        this.fechaHoraEntrada = null;
        this.coste = 0.0;
    }

    // NUEVO: genera automáticamente el siguiente número de reserva disponible
    /**
     * Genera automáticamente el siguiente identificador de reserva disponible en la base de datos.
     *
     * @return El siguiente número entero para una reserva nueva.
     * @throws Exception Si ocurre un error al consultar la base de datos.
     */
    public static int siguienteReserva() throws Exception {
        String sql = "SELECT COALESCE(MAX(numeroReserva), 0) + 1 FROM reserva";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 1;
        } catch (SQLException e) {
            throw new Exception("Error en siguienteReserva!!", e);
        }
    }

    /**
     * Recupera todas las reservas registradas y las carga en la lista proporcionada.
     *
     * @param reservas Lista donde se almacenarán las instancias de {@link Reserva} recuperadas.
     * @throws Exception Si ocurre un error durante la consulta SQL.
     */
    public static void listadoReserva(List<Reserva> reservas) throws Exception {

        String sql = "SELECT * from reserva ORDER BY numeroReserva";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            Reserva reserva;

            while (rs.next()) {
                reserva = new Reserva();
                reserva.setNumeroReserva(rs.getInt(1));
                reserva.setDniCliente(rs.getString(2));
                reserva.setNumeroPlaza(rs.getInt(3));
                // fechaHoraSalida puede ser null si la reserva sigue activa
                Timestamp tsSalida = rs.getTimestamp(4);
                reserva.setFechaHoraSalida(tsSalida != null ? tsSalida.toLocalDateTime() : null);
                reserva.setFechaHoraEntrada(rs.getTimestamp(5).toLocalDateTime());
                reserva.setCoste(rs.getDouble(6));
                reservas.add(reserva);
            }

        } catch (SQLException e) {
            throw new Exception("Error en listadoReserva!!", e);
        }

    }

    public int getNumeroReserva() {
        return numeroReserva;
    }

    public void setNumeroReserva(int numeroReserva) {
        this.numeroReserva = numeroReserva;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public int getNumeroPlaza() {
        return numeroPlaza;
    }

    public void setNumeroPlaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    /**
     * Verifica si existe una reserva registrada en la base de datos con el número
     * asignado a la instancia actual.
     *
     * @return {@code true} si se encuentra la reserva, {@code false} en caso contrario.
     * @throws Exception Si ocurre un error de ejecución con la base de datos.
     */
    public boolean existeReserva() throws Exception {

        String sql = "SELECT * from reserva WHERE numeroReserva = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroReserva);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new Exception("Error en existeReserva!!", e);
        }

    }

    // CORREGIDO: ya no pide numeroReserva ni numeroPlaza manualmente.
    // - numeroReserva se genera automáticamente con siguienteReserva()
    // - numeroPlaza se asigna automáticamente con Plaza.plazaLibre()
    // CORREGIDO: eliminado try-with-resources sobre Connection (cerraba la conexión compartida)
    /**
     * Registra una nueva reserva en el sistema y marca la plaza correspondiente como 'OCUPADA'.
     * Esta operación es transaccional: si el registro de la reserva o la actualización
     * de la plaza falla, se revertirán todos los cambios.
     *
     * @throws Exception Si la reserva ya existe o ocurre un fallo de integridad en la base de datos.
     */
    public void altaReserva() throws Exception {

        // Auto-generar número de reserva
        this.numeroReserva = siguienteReserva();

        // Auto-asignar primera plaza libre
        this.numeroPlaza = Plaza.plazaLibre();

        if (existeReserva()) {
            throw new Exception("La reserva ya existe!!");
        }

        String sqlReserva = "INSERT INTO reserva VALUES(?,?,?,?,?,?)";
        String sqlPlaza = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";

        Connection con = ConexionBD.getConexionBD();
        con.setAutoCommit(false);

        try (
                PreparedStatement pstReserva = con.prepareStatement(sqlReserva);
                PreparedStatement pstPlaza = con.prepareStatement(sqlPlaza)
        ) {
            // INSERT reserva
            pstReserva.setInt(1, numeroReserva);
            pstReserva.setString(2, dniCliente);
            pstReserva.setInt(3, numeroPlaza);
            pstReserva.setNull(4, java.sql.Types.TIMESTAMP);
            pstReserva.setTimestamp(5, Timestamp.valueOf(fechaHoraEntrada));
            pstReserva.setNull(6, java.sql.Types.DECIMAL);
            pstReserva.executeUpdate();

            // UPDATE plaza → OCUPADA
            pstPlaza.setString(1, "OCUPADA");
            pstPlaza.setInt(2, numeroPlaza);
            pstPlaza.executeUpdate();

            con.commit();

        } catch (SQLException e) {
            con.rollback();
            throw new Exception("Error en altaReserva!!", e);
        } finally {
            con.setAutoCommit(true);
        }
    }

    // CORREGIDO: eliminado try-with-resources sobre Connection (cerraba la conexión compartida)
    // NUEVO: cálculo de coste con descuentos:
    //   - Si el cliente es VIP (tiene descuento en BD): se aplica ese % de descuento
    //   - Si la plaza es minusválida (tiene descuento en BD): se aplica ese % de descuento
    //   - Los descuentos se suman entre sí
    //   - Si la plaza es eléctrica (tiene precioCarga): se suma al coste final
    /**
     * Finaliza una reserva activa, calcula el coste basado en el tiempo de estancia,
     * aplica descuentos acumulados (usuario y plaza) y libera la plaza en el sistema.
     * <p>
     * Lógica de cálculo: {@code (horas * precio) * (1 - descuentos) + precioCarga}.
     * </p>
     *
     * @throws Exception Si los datos no coinciden, la reserva no existe o hay error de SQL.
     */
    public void bajaReserva() throws Exception {

        String sqlSelect = "SELECT r.fechaHoraEntrada, "
                + "u.descuento AS descuentoUsuario, "
                + "p.descuento AS descuentoPlaza, "
                + "p.precioCarga "
                + "FROM reserva r "
                + "JOIN usuario u ON r.dniCliente = u.DNI "
                + "JOIN plaza p ON r.numeroPlaza = p.numeroPlaza "
                + "WHERE r.numeroReserva = ? AND r.dniCliente = ? AND r.numeroPlaza = ?";

        String sqlUpdateReserva = "UPDATE reserva SET fechaHoraSalida = ?, coste = ? WHERE numeroReserva = ? AND dniCliente = ? AND numeroPlaza = ?";

        String sqlUpdatePlaza = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";

        Connection con = ConexionBD.getConexionBD();
        con.setAutoCommit(false);

        try {
            LocalDateTime fechaEntrada;
            Double descuentoUsuario;
            Double descuentoPlaza;
            Double precioCarga;

            // Obtener datos de la reserva, usuario y plaza en una sola consulta
            try (PreparedStatement pstSelect = con.prepareStatement(sqlSelect)) {

                pstSelect.setInt(1, numeroReserva);
                pstSelect.setString(2, dniCliente);
                pstSelect.setInt(3, numeroPlaza);

                ResultSet rs = pstSelect.executeQuery();

                if (!rs.next()) {
                    throw new Exception("La reserva no existe o los datos no coinciden");
                }

                fechaEntrada = rs.getTimestamp("fechaHoraEntrada").toLocalDateTime();
                descuentoUsuario = rs.getObject("descuentoUsuario", Double.class);
                descuentoPlaza = rs.getObject("descuentoPlaza", Double.class);
                precioCarga = rs.getObject("precioCarga", Double.class);
            }

            // Fecha y hora de salida = ahora
            LocalDateTime fechaSalida = LocalDateTime.now();

            // Horas redondeadas hacia arriba (mínimo 1 hora)
            long minutos = Duration.between(fechaEntrada, fechaSalida).toMinutes();
            double horas = Math.max(1, Math.ceil(minutos / 60.0));

            // Coste base
            double costeBase = horas * PRECIO_HORA;

            // Descuento total = descuento VIP + descuento plaza minusválida (ambos en %)
            // Si ninguno aplica, descuentoTotal = 0
            double descuentoTotal = (descuentoUsuario != null ? descuentoUsuario : 0.0)
                    + (descuentoPlaza != null ? descuentoPlaza : 0.0);

            // Aplicar descuento (máximo 100%)
            if (descuentoTotal > 100.0) descuentoTotal = 100.0;
            double costeConDescuento = costeBase * (1.0 - descuentoTotal / 100.0);

            // Si la plaza es eléctrica, sumar precio de carga
            double costeTotal = costeConDescuento + (precioCarga != null ? precioCarga : 0.0);

            // UPDATE reserva con fecha salida y coste final
            try (PreparedStatement pstUpdateReserva = con.prepareStatement(sqlUpdateReserva)) {

                pstUpdateReserva.setTimestamp(1, Timestamp.valueOf(fechaSalida));
                pstUpdateReserva.setDouble(2, costeTotal);
                pstUpdateReserva.setInt(3, numeroReserva);
                pstUpdateReserva.setString(4, dniCliente);
                pstUpdateReserva.setInt(5, numeroPlaza);
                pstUpdateReserva.executeUpdate();
            }

            // UPDATE plaza → LIBRE
            try (PreparedStatement pstUpdatePlaza = con.prepareStatement(sqlUpdatePlaza)) {

                pstUpdatePlaza.setString(1, "LIBRE");
                pstUpdatePlaza.setInt(2, numeroPlaza);
                pstUpdatePlaza.executeUpdate();
            }

            con.commit();

            // Actualizar el objeto en memoria con el coste calculado
            this.coste = costeTotal;
            this.fechaHoraSalida = fechaSalida;

        } catch (Exception e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }


}
