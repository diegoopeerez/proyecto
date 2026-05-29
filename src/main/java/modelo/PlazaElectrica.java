package modelo;

import datos.ConexionBD;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Clase que representa una plaza eléctrica, extendiendo de la clase base {@link Plaza}.
 * Añade la funcionalidad específica de gestión de precio de carga para vehículos eléctricos.
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.1
 */
public class PlazaElectrica extends Plaza {

    private double precioCarga;

    /**
     * Constructor por defecto. Inicializa la plaza con un precio de carga de 0.0.
     */
    public PlazaElectrica() {
        super();
        precioCarga = 0.0;
    }

    /**
     * Constructor sobrecargado que inicializa una plaza eléctrica con su número identificador.
     *
     * @param numeroPlaza El número identificador de la plaza eléctrica.
     */
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

    /**
     * Registra una nueva plaza eléctrica en la base de datos.
     * El número se genera automáticamente y el estado inicial siempre es LIBRE.
     * Sobrescribe el método de {@link Plaza} para incluir el campo específico
     * de precio de carga en la inserción SQL.
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
            pst.setNull(3, java.sql.Types.DECIMAL);   // descuento NULL
            pst.setDouble(4, precioCarga);
            pst.executeUpdate();

        } catch (SQLException e) {
            throw new Exception("Error en altaPlaza (PlazaElectrica)!!", e);
        }
    }
}
