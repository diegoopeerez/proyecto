package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Plaza {

    protected int numeroPlaza;
    protected EstadoPlaza estado;

    public Plaza() {
        numeroPlaza = 0;
    }

    public Plaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    // NUEVO: genera automáticamente el siguiente número de plaza disponible
    // Formato 000001, 000002... (se almacena como INT, se muestra con formato)
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

    public void bajaPlaza() throws Exception {

        String sql = "UPDATE plaza SET estado = 'FUERA_DE_SERVICIO' WHERE numeroPlaza = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en bajaPlaza!!", e);
        }
    }

    protected enum EstadoPlaza {
        LIBRE,
        OCUPADA,
        FUERA_DE_SERVICIO
    }

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
