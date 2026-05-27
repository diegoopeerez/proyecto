package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioVIP extends Usuario {
    private double descuento;

    public UsuarioVIP() {
        super();
        descuento = 0.00;
    }

    public UsuarioVIP(String dni) {
        super(dni);
        this.descuento = 0.00;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    @Override
    public void altaUsuario() throws Exception {
        if (existeUsuario()) {
            throw new Exception("El usuario ya existe!!");
        }

        String sql = "INSERT INTO usuario VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, dni);
            pst.setString(2, nombre);
            pst.setString(3, matricula);
            pst.setDouble(4, descuento);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaUsuario!!");
        }
    }
}
