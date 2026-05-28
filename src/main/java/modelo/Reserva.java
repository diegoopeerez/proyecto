package modelo;

import datos.ConexionBD;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Reserva {

    private int numeroReserva;
    private String dniCliente;
    private int numeroPlaza;
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraEntrada;
    private double coste;

    public Reserva() {
        numeroReserva = 0;
        dniCliente = "";
        numeroPlaza = 0;
        fechaHoraSalida = null;
        fechaHoraEntrada = LocalDateTime.now();
        coste = 0.0;
    }

    public Reserva(int numeroReserva, String dniCliente, int numeroPlaza) {
        this.numeroReserva = numeroReserva;
        this.dniCliente = dniCliente;
        this.numeroPlaza = numeroPlaza;
        this.fechaHoraSalida = null;
        this.fechaHoraEntrada = null;
        this.coste = 0.0;
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

    public boolean existeReserva() throws Exception {

        String sql = "SELECT * from reserva WHERE numeroReserva = ? AND dniCliente = ? AND numeroPlaza = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroReserva);
            pst.setString(2, dniCliente);
            pst.setInt(3, numeroPlaza);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new Exception("Error en existeReserva!!");
        }

    }

    public void altaReserva() throws Exception {

        if (existeReserva()) {
            throw new Exception("La reserva ya existe!!");
        }

        String sqlReserva = "INSERT INTO reserva VALUES(?,?,?,?,?,?)";
        String sqlPlaza = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";

        try (Connection con = ConexionBD.getConexionBD()) {

            con.setAutoCommit(false);

            try (
                    PreparedStatement pstReserva = con.prepareStatement(sqlReserva);
                    PreparedStatement pstPlaza = con.prepareStatement(sqlPlaza)
            ) {

                // INSERT reserva
                pstReserva.setInt(1, numeroReserva);
                pstReserva.setString(2, dniCliente);
                pstReserva.setInt(3, numeroPlaza);
                pstReserva.setTimestamp(4,fechaHoraSalida != null ? Timestamp.valueOf(fechaHoraSalida) : null);
                pstReserva.setTimestamp(5, Timestamp.valueOf(fechaHoraEntrada));
                pstReserva.setNull(6, java.sql.Types.DECIMAL);

                pstReserva.executeUpdate();

                // UPDATE plaza
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
    }

    public void bajaReserva() throws Exception {

        String sqlSelect = "SELECT fechaHoraEntrada FROM reserva WHERE numeroReserva = ? AND dniCliente = ? AND numeroPlaza = ?";

        String sqlUpdateReserva = "UPDATE reserva SET fechaHoraSalida = ?, coste = ? WHERE numeroReserva = ? AND dniCliente = ? AND numeroPlaza = ?";

        String sqlUpdatePlaza = "UPDATE plaza SET estado = ? WHERE numeroPlaza = ?";

        double precioHora = 2.5;

        try (Connection con = ConexionBD.getConexionBD()) {

            con.setAutoCommit(false);

            try {

                LocalDateTime fechaEntrada;

                // Obtener fecha entrada
                try (PreparedStatement pstSelect = con.prepareStatement(sqlSelect)) {

                    pstSelect.setInt(1, numeroReserva);
                    pstSelect.setString(2, dniCliente);
                    pstSelect.setInt(3, numeroPlaza);

                    ResultSet rs = pstSelect.executeQuery();

                    if (!rs.next()) {
                        throw new Exception("La reserva no existe");
                    }

                    fechaEntrada = rs.getTimestamp("fechaHoraEntrada").toLocalDateTime();
                }

                // Fecha salida
                LocalDateTime fechaSalida = LocalDateTime.now();

                // Tiempo
                long minutos = Duration.between(fechaEntrada, fechaSalida).toMinutes();

                double horas = Math.ceil(minutos / 60.0);

                // Coste
                double coste = horas * precioHora;

                // UPDATE reserva
                try (PreparedStatement pstUpdateReserva =
                             con.prepareStatement(sqlUpdateReserva)) {

                    pstUpdateReserva.setTimestamp(1, Timestamp.valueOf(fechaSalida));
                    pstUpdateReserva.setDouble(2, coste);

                    pstUpdateReserva.setInt(3, numeroReserva);
                    pstUpdateReserva.setString(4, dniCliente);
                    pstUpdateReserva.setInt(5, numeroPlaza);

                    pstUpdateReserva.executeUpdate();
                }

                // UPDATE plaza
                try (PreparedStatement pstUpdatePlaza =
                             con.prepareStatement(sqlUpdatePlaza)) {

                    pstUpdatePlaza.setString(1, "LIBRE");
                    pstUpdatePlaza.setInt(2, numeroPlaza);

                    pstUpdatePlaza.executeUpdate();
                }

                con.commit();

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new Exception("Error en bajaReserva!!", e);
        }
    }

    public static void listadoReserva(List<Reserva> reservas) throws Exception {

        String sql = "SELECT * from reserva ORDER BY numeroReserva,dniCliente,numeroPlaza";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            Reserva reserva;

            while (rs.next()) {
                reserva = new Reserva();
                reserva.setNumeroReserva(rs.getInt(1));
                reserva.setDniCliente(rs.getString(2));
                reserva.setNumeroPlaza(rs.getInt(3));
                reserva.setFechaHoraSalida(rs.getObject(4, LocalDateTime.class));
                reserva.setFechaHoraEntrada(rs.getTimestamp(5).toLocalDateTime());
                reserva.setCoste(rs.getObject(6, Double.class));
                reservas.add(reserva);
            }

        } catch (SQLException e) {
            throw new Exception("Error en listadoReserva!!");
        }

    }


}
