package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlazaMinusvalida extends Plaza {

    private double descuento;

    public PlazaMinusvalida() {
        super();
        descuento = 0.0;
    }

    public PlazaMinusvalida(int numeroPlaza) {
        super(numeroPlaza);
        this.descuento = 0.0;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    @Override
    public void altaPlaza() throws Exception {
        if (existePlaza()) {
            throw new Exception("La plaza ya existe!!");
        }

        String sql = "INSERT INTO plaza VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.setString(2, estado.toString());
            pst.setDouble(3, descuento);
            pst.setNull(4, java.sql.Types.DECIMAL);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaPlaza!!");
        }
    }

}
