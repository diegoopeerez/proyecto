package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlazaElectrica extends Plaza {

    private double precioCarga;

    public PlazaElectrica() {
        super();
        precioCarga = 0.0;
    }

    public PlazaElectrica(int numeroPlaza) {
        super(numeroPlaza);
        precioCarga = 0.0;
    }

    public double getPrecioCarga() {
        return precioCarga;
    }

    public void setPrecioCarga(double precioCarga) {
        this.precioCarga = precioCarga;
    }

    @Override
    public void altaPlaza() throws Exception {
        if (existePlaza()) {
            throw new Exception("La plaza ya existe!!");
        }

        String sql = "INSERT INTO plaza VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.setString(2, categoria.toString());
            pst.setNull(3, java.sql.Types.DECIMAL);
            pst.setDouble(4, precioCarga);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaPlaza!!");
        }
    }
}
