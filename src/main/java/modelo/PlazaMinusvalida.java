package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase que representa una plaza reservada para personas con movilidad reducida (minusválida),
 * extendiendo la funcionalidad de la clase base {@link Plaza}.
 * Añade la gestión específica de un valor de descuento aplicado a la tarifa de la plaza.
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.1
 */
public class PlazaMinusvalida extends Plaza {

    private double descuento;

    /**
     * Constructor por defecto. Inicializa la plaza con un valor de descuento de 0.0.
     */
    public PlazaMinusvalida() {
        super();
        descuento = 0.0;
    }

    /**
     * Constructor sobrecargado que inicializa una plaza minusválida con su número identificador.
     *
     * @param numeroPlaza El número identificador único de la plaza.
     */
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

    /**
     * Registra una nueva plaza para minusválidos en la base de datos.
     * El número se genera automáticamente y el estado inicial siempre es LIBRE.
     * Sobrescribe el método de {@link Plaza} para incluir el campo específico
     * de descuento y establecer el precio de carga como nulo en la inserción SQL.
     *
     * @throws Exception Si ocurre un error durante la ejecución de la sentencia SQL.
     */
    @Override
    public void altaPlaza() throws Exception {
        // Auto-generar número de plaza
        this.numeroPlaza = siguientePlaza();

        // El estado siempre es LIBRE al dar de alta
        this.estado = EstadoPlaza.LIBRE;

        if (existePlaza()) {
            throw new Exception("La plaza " + String.format("%06d", numeroPlaza) + " ya existe");
        }

        String sql = "INSERT INTO plaza VALUES(?,?,?,?)";

        try (PreparedStatement pst = ConexionBD.getConexionBD().prepareStatement(sql)) {

            pst.setInt(1, numeroPlaza);
            pst.setString(2, estado.toString());
            pst.setDouble(3, descuento);
            pst.setNull(4, java.sql.Types.DECIMAL);   // precioCarga NULL
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaPlaza (PlazaMinusvalida)!!", e);
        }
    }

}
