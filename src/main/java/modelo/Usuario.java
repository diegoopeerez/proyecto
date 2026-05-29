package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase que representa el modelo de un usuario en el sistema.
 * Gestiona la persistencia de los datos del cliente y su vinculación con la base de datos.
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.0
 */
public class Usuario {
    protected String dni;
    protected String nombre;
    protected String matricula;

    /**
     * Constructor por defecto que inicializa al usuario con valores vacíos.
     */
    public Usuario() {
        dni = "";
        nombre = "";
        matricula = "";
    }

    /**
     * Constructor sobrecargado que inicializa a un usuario con su DNI.
     *
     * @param dni El DNI único del usuario.
     */
    public Usuario(String dni) {
        this.dni = dni;
        this.nombre = "";
        this.matricula = "";
    }

    /**
     * Carga en una lista proporcionada el listado de todos los usuarios registrados.
     *
     * @param usuarios Lista de objetos {@link UsuarioListado} donde se añadirán los resultados.
     * @throws Exception Si ocurre un error al consultar la tabla de usuarios.
     */
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
            throw new Exception("Error en listadoUsuarios!!", e);
        }

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

    /**
     * Verifica si el usuario actual ya existe en la base de datos según su DNI.
     *
     * @return {@code true} si el usuario existe, {@code false} en caso contrario.
     * @throws Exception Si ocurre un error al ejecutar la consulta SQL.
     */
    public boolean existeUsuario() throws Exception {

        String sql = "SELECT * from usuario WHERE dni = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, dni);
            ResultSet rs = pst.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new Exception("Error en existeUsuario!!", e);
        }

    }

    /**
     * Registra un nuevo usuario en la base de datos.
     *
     * @throws Exception Si el usuario ya existe o hay un error de inserción.
     */
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
            throw new Exception("Error en altaUsuario!!", e);
        }

    }

    /**
     * Elimina al usuario actual de la base de datos basándose en su DNI.
     *
     * @throws Exception Si ocurre un error durante la ejecución del borrado.
     */
    public void bajaUsuario() throws Exception {

        if (!existeUsuario()) {
            throw new Exception("El usuario no existe!!");
        }

        String sql = "DELETE FROM usuario WHERE dni = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, dni);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en bajaUsuario!!", e);
        }
    }

    /**
     * Modifica los datos personales de un usuario existente.
     *
     * @param nombre    El nuevo nombre para el usuario.
     * @param matricula La nueva matrícula del vehículo.
     * @throws Exception Si ocurre un error durante la actualización en la base de datos.
     */
    public void modificarUsuario(String nombre, String matricula) throws Exception {

        String sql = "UPDATE Usuario SET nombre = ?, matricula = ? WHERE DNI = ?";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setString(1, nombre);
            pst.setString(2, matricula);
            pst.setString(3, dni);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en modificarUsuario", e);
        }

    }

    /**
     * Clase interna que extiende de {@link Usuario} para incluir información adicional de descuentos.
     */
    public static class UsuarioListado extends Usuario {
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


