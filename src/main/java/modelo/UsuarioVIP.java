package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase que representa a un usuario VIP en el sistema, extendiendo de {@link Usuario}.
 * Añade la funcionalidad específica para gestionar un porcentaje de descuento personalizado
 * aplicado al cliente.
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.0
 */
public class UsuarioVIP extends Usuario {
    private double descuento;

    /**
     * Constructor por defecto. Inicializa al usuario con un descuento base de 0.0.
     */
    public UsuarioVIP() {
        super();
        descuento = 0.00;
    }

    /**
     * Constructor sobrecargado que inicializa al usuario VIP con su DNI.
     *
     * @param dni El DNI identificador del usuario.
     */
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

    /**
     * Registra un nuevo usuario VIP en la base de datos.
     * Sobrescribe el método de {@link Usuario} para incluir el campo específico
     * de descuento en la inserción de la base de datos.
     *
     * @throws Exception Si el usuario ya existe en la base de datos o si ocurre
     *                   un error durante la ejecución de la sentencia SQL.
     */
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
