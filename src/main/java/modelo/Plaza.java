package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Plaza {

    protected int numeroPlaza;
    protected EstadoPlaza categoria;

    protected enum EstadoPlaza {
        LIBRE,
        OCUPADA,
        RESERVADA,
        FUERA_DE_SERVICIO
    }

    public Plaza() {
        numeroPlaza = 0;
    }

    public Plaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    public int getNumeroPlaza() {
        return numeroPlaza;
    }

    public void setNumeroPlaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    public String getCategoria() {
        return categoria.toString();
    }

    public void setCategoria(String op) {
        switch (op) {
            case "LIBRE":
                categoria = EstadoPlaza.LIBRE;
                break;
            case "OCUPADA":
                categoria = EstadoPlaza.OCUPADA;
                break;
            case "RESERVADA":
                categoria = EstadoPlaza.RESERVADA;
                break;
            case "FUERA_DE_SERVICIO":
                categoria = EstadoPlaza.FUERA_DE_SERVICIO;
                break;
        }
    }

    public boolean existePlaza() throws Exception {

        String sql = "SELECT * from plaza WHERE numeroPlaza = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);

            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new Exception("Error en existePlaza!!");
        }

    }

    public void altaPlaza() throws Exception {

        if (existePlaza()) {
            throw new Exception("La plaza ya existe!!");
        }

        String sql = "INSERT INTO plaza VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.setString(2, categoria.toString());
            pst.setNull(3, java.sql.Types.DECIMAL);
            pst.setNull(4, java.sql.Types.DECIMAL);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaPlaza!!");
        }

    }

    public void bajaPlaza() throws Exception {

        String sql = "UPDATE FROM plaza SET categoria = 'FUERA_DE_SERVICIO' WHERE numeroPlaza = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en bajaPlaza!!", e);
        }
    }

    public static void listadoPlaza(List<Plaza> plazas) throws Exception {

        String sql = "SELECT * from plaza ORDER BY numeroPlaza";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            PlazaListado plazaListado;

            while (rs.next()) {
                plazaListado = new PlazaListado();
                plazaListado.setNumeroPlaza(rs.getInt(1));
                plazaListado.setCategoria(rs.getString(2));
                plazaListado.setDescuento(rs.getObject(3, Double.class));
                plazaListado.setPrecioCarga(rs.getObject(4, Double.class));
                plazas.add(plazaListado);
            }

        } catch (SQLException e) {
            throw new Exception("Error en listadoPlaza!!");
        }

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
