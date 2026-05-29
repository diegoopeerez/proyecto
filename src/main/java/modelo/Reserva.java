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

    /**
     * Genera automáticamente el siguiente identificador de reserva disponible.
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
     * Verifica si existe una reserva con el número asignado a la instancia actual.
     *
     * @return {@code true} si se encuentra la reserva, {@code false} en caso contrario.
     * @throws Exception Si ocurre un error de ejecución con la base de datos.
     */
    public boolean existeReserva() throws Exception {
        String sql = "SELECT * FROM reserva WHERE numeroReserva = ? AND dniCliente = ? AND numeroPlaza = ?";
        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {
            pst.setInt(1, numeroReserva);
            pst.setString(2, dniCliente);
            pst.setInt(3, numeroPlaza);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new Exception("Error en existeReserva!!", e);
        }
    }

    /**
     * Registra una nueva reserva y marca la plaza como OCUPADA.
     * El número de reserva y la plaza se asignan automáticamente.
     * Operación transaccional: si algo falla se revierte todo.
     *
     * @throws Exception Si no hay plazas libres, el usuario no existe o hay error de BD.
     */
    public void altaReserva() throws Exception {

        // Auto-generar número de reserva
        this.numeroReserva = siguienteReserva();

        // Auto-asignar primera plaza libre
        this.numeroPlaza = Plaza.plazaLibre();

        if (existeReserva()) {
            throw new Exception("La reserva " + String.format("%06d", numeroReserva) + " ya existe");
        }

        String sqlReserva = "INSERT INTO reserva VALUES(?,?,?,?,?,?)";
        String sqlPlaza = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";

        Connection con = ConexionBD.getConexionBD();
        con.setAutoCommit(false);

        try (
                PreparedStatement pstReserva = con.prepareStatement(sqlReserva);
                PreparedStatement pstPlaza = con.prepareStatement(sqlPlaza)
        ) {
            pstReserva.setInt(1, numeroReserva);
            pstReserva.setString(2, dniCliente);
            pstReserva.setInt(3, numeroPlaza);
            pstReserva.setNull(4, java.sql.Types.TIMESTAMP);
            pstReserva.setTimestamp(5, Timestamp.valueOf(fechaHoraEntrada));
            pstReserva.setNull(6, java.sql.Types.DECIMAL);
            pstReserva.executeUpdate();

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

    /**
     * Finaliza una reserva activa, calcula el coste proporcional al tiempo real de estancia
     * y libera la plaza en el sistema.
     * <p>
     * Lógica de cálculo:
     * <ul>
     *   <li>Coste base = (minutos / 60.0) * {@value #PRECIO_HORA} €/h → proporcional al minuto</li>
     *   <li>Si el cliente es VIP, se descuenta su porcentaje sobre el coste base</li>
     *   <li>Si la plaza es minusválida, se suma ese descuento adicional</li>
     *   <li>Si la plaza es eléctrica, se suma el precioCarga al total final</li>
     * </ul>
     * Operación transaccional: si algo falla se revierte todo.
     *
     * @throws Exception Si la reserva no existe, ya fue cerrada anteriormente, o hay error de BD.
     */
    public void bajaReserva() throws Exception {

        if (!existeReserva()) {
            throw new Exception("La reserva " + String.format("%06d", numeroReserva) + " no existe");
        }

        // consulta extendida que también recupera fechaHoraSalida
        // para detectar si la reserva ya fue cerrada
        String sqlSelect = "SELECT r.fechaHoraEntrada, "
                + "r.fechaHoraSalida, "
                + "u.descuento AS descuentoUsuario, "
                + "p.descuento AS descuentoPlaza, "
                + "p.precioCarga "
                + "FROM reserva r "
                + "JOIN usuario u ON r.dniCliente = u.DNI "
                + "JOIN plaza p ON r.numeroPlaza = p.numeroPlaza "
                + "WHERE r.numeroReserva = ? AND r.dniCliente = ? AND r.numeroPlaza = ?";

        String sqlUpdateReserva = "UPDATE reserva SET fechaHoraSalida = ?, coste = ? "
                + "WHERE numeroReserva = ? AND dniCliente = ? AND numeroPlaza = ?";

        String sqlUpdatePlaza = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";

        Connection con = ConexionBD.getConexionBD();
        con.setAutoCommit(false);

        try {
            LocalDateTime fechaEntrada;
            Double descuentoUsuario;
            Double descuentoPlaza;
            Double precioCarga;

            try (PreparedStatement pstSelect = con.prepareStatement(sqlSelect)) {

                pstSelect.setInt(1, numeroReserva);
                pstSelect.setString(2, dniCliente);
                pstSelect.setInt(3, numeroPlaza);

                ResultSet rs = pstSelect.executeQuery();

                if (!rs.next()) {
                    throw new Exception("La reserva no existe o los datos introducidos no coinciden");
                }

                // si ya tiene fecha de salida, la reserva ya fue cerrada
                Timestamp tsSalida = rs.getTimestamp("fechaHoraSalida");
                if (tsSalida != null) {
                    throw new Exception("La reserva " + String.format("%06d", numeroReserva)
                            + " ya fue cerrada el " + tsSalida.toLocalDateTime()
                            + ". No se puede dar de baja de nuevo.");
                }

                fechaEntrada = rs.getTimestamp("fechaHoraEntrada").toLocalDateTime();
                descuentoUsuario = rs.getObject("descuentoUsuario", Double.class);
                descuentoPlaza = rs.getObject("descuentoPlaza", Double.class);
                precioCarga = rs.getObject("precioCarga", Double.class);
            }

            LocalDateTime fechaSalida = LocalDateTime.now();

            // coste proporcional al minuto (30 min → 1.25 €, no redondeo a hora)
            long minutos = Duration.between(fechaEntrada, fechaSalida).toMinutes();
            double horas = minutos / 60.0;
            double costeBase = horas * PRECIO_HORA;

            // Descuentos acumulados (VIP + minusválida), máximo 100%
            double descuentoTotal = (descuentoUsuario != null ? descuentoUsuario : 0.0)
                    + (descuentoPlaza != null ? descuentoPlaza : 0.0);
            if (descuentoTotal > 100.0) descuentoTotal = 100.0;

            double costeConDescuento = costeBase * (1.0 - descuentoTotal / 100.0);

            // Si plaza eléctrica, sumar precio de carga
            double costeTotal = costeConDescuento + (precioCarga != null ? precioCarga : 0.0);

            try (PreparedStatement pstUpdateReserva = con.prepareStatement(sqlUpdateReserva)) {
                pstUpdateReserva.setTimestamp(1, Timestamp.valueOf(fechaSalida));
                pstUpdateReserva.setDouble(2, costeTotal);
                pstUpdateReserva.setInt(3, numeroReserva);
                pstUpdateReserva.setString(4, dniCliente);
                pstUpdateReserva.setInt(5, numeroPlaza);
                pstUpdateReserva.executeUpdate();
            }

            try (PreparedStatement pstUpdatePlaza = con.prepareStatement(sqlUpdatePlaza)) {
                pstUpdatePlaza.setString(1, "LIBRE");
                pstUpdatePlaza.setInt(2, numeroPlaza);
                pstUpdatePlaza.executeUpdate();
            }

            con.commit();

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