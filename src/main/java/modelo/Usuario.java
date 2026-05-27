package modelo;

import datos.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class Usuario {
    protected String dni;
    protected String nombre;
    protected String matricula;

    public Usuario() {
        dni = "";
        nombre = "";
        matricula = "";
    }


    public Usuario(String dni) {
        this.dni = dni;
        this.nombre = "";
        this.matricula = "";
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public boolean existeUsuario() throws Exception {

        String sql = "SELECT * from usuario WHERE dni = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, dni);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new Exception("Error en existeUsuario!!");
        }

   }

    public void altaUsuario() throws Exception {

        if (existeUsuario()) {
            throw new Exception("El usuario ya existe!!");
        }

        String sql = "INSERT INTO usuario VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, dni);
            pst.setString(2, nombre);
            pst.setString(3, matricula);
            pst.setNull(4, java.sql.Types.DECIMAL);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaUsuario!!");
        }

    }

    public void bajaUsuario() throws Exception {

        String sql = "DELETE FROM usuario WHERE dni = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, dni);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en bajaUsuario!!", e);
        }
    }

    public void modificarUsuario(String nombre, String matricula) throws Exception {

        String sql = "UPDATE Usuario SET nombre = ?, matricula = ? WHERE DNI = ?";

        try (Connection con = ConexionBD.getConexionBD()) {

            try (PreparedStatement pst1 = con.prepareStatement(sql)) {

                pst1.setString(1, nombre);
                pst1.setString(2, matricula);
                pst1.setString(3, dni);
                pst1.executeUpdate();

            } catch (SQLException e) {
                throw new Exception("Error en modificarUsuario");
            }

        } catch (SQLException e) {
            throw new Exception("Error en modificarUsuario");
        }

    }

    public static void listadoUsuario(List<UsuarioListado> usuarios) throws Exception {

        String sql = "SELECT * from usuario ORDER BY dni";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            ResultSet rs = pst.executeQuery();
            UsuarioListado usuarioListado;

            while (rs.next()) {
                usuarioListado = new UsuarioListado();
                usuarioListado.setDni(rs.getString(1));
                usuarioListado.setNombre(rs.getString(2));
                usuarioListado.setMatricula(rs.getString(3));
                usuarioListado.setDescuento(rs.getObject(4, Double.class));
                usuarios.add(usuarioListado);
            }

        } catch (SQLException e) {
            throw new Exception("Error en listadoUsuarios!!");
        }

    }

    public static class UsuarioListado extends Usuario {
        ;
        private Double descuento;

        public UsuarioListado() {
            descuento = null;
        }

        public Double getDescuento() {
            return descuento;
        }

        public void setDescuento(Double descuento) {
            this.descuento = descuento;
        }

    }


}


