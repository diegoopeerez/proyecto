package vista;

import datos.ConexionBD;
import modelo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Clase principal que actúa como punto de entrada (CLI - Interfaz de Línea de Comandos)
 * para la aplicación de gestión de un aparcamiento.
 * <p>
 * Esta clase orquesta el ciclo de vida de la aplicación:
 * <ul>
 * <li>Gestiona la apertura y cierre de la conexión con la base de datos.</li>
 * <li>Presenta el menú interactivo para la gestión de usuarios, plazas y reservas.</li>
 * <li>Invoca los métodos del paquete {@code modelo} para procesar las operaciones solicitadas.</li>
 * </ul>
 *
 * @author Diego Perez, Adrian Cava
 * @version 1.1
 */
public class Main {

    /**
     * Muestra el menú de opciones principales por consola y captura la selección del usuario.
     *
     * @return El número entero de la opción elegida por el usuario.
     */
    public static int menu() {
        int op;
        Scanner sc = new Scanner(System.in);
        System.out.println("========== MENÚ APARCAMIENTO ==========");
        System.out.println("1.-Alta de Usuario");
        System.out.println("2.-Baja de Usuario");
        System.out.println("3.-Modificación de Usuario");
        System.out.println("4.-Alta de Plaza");
        System.out.println("5.-Baja de Plaza");
        System.out.println("6.-Modificar Estado de Plaza");
        System.out.println("7.-Alta de Reserva");
        System.out.println("8.-Baja de Reserva");
        System.out.println("9.-Listado de Usuarios");
        System.out.println("10.-Listado de Plazas");
        System.out.println("11.-Listado de Reservas");
        System.out.println("0.-Salir");
        System.out.println("========================================");
        System.out.print("Opcion?: ");

        op = sc.nextInt();

        return op;
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        int op;

        /* ABRIR CONEXIÓN BD **************************************************/
        try {
            System.out.println("Abriendo conexión BD...");
            ConexionBD.abrirConexion();
            System.out.println("Conexión abierta correctamente.");
        } catch (Exception e) {
            System.out.println("Error!!\n" + e.getMessage());
        }
        /* ********************************************************************/

        do {
            op = menu();
            switch (op) {

                case 1: // Alta de Usuario
                {
                    Usuario usuario = null;

                    System.out.print("¿El usuario es VIP? (s/n): ");
                    char opcion = sc.next().charAt(0);

                    if (opcion == 's' || opcion == 'S') {
                        usuario = new UsuarioVIP();
                    } else if (opcion == 'n' || opcion == 'N') {
                        usuario = new Usuario();
                    } else {
                        System.out.println("Opción no válida.");
                        break;
                    }

                    System.out.print("Introduce el DNI del Usuario: ");
                    usuario.setDni(sc.next());

                    System.out.print("Introduce el nombre del Usuario: ");
                    usuario.setNombre(sc.next());

                    System.out.print("Introduce la matrícula del Usuario: ");
                    usuario.setMatricula(sc.next());

                    if (usuario instanceof UsuarioVIP uv) {
                        System.out.print("Introduce el descuento VIP (%): ");
                        uv.setDescuento(sc.nextDouble());
                    }

                    try {
                        usuario.altaUsuario();
                        System.out.println("Alta de usuario correcta. DNI: " + usuario.getDni());
                    } catch (Exception e) {
                        System.out.println("Alta de usuario incorrecta: " + e.getMessage());
                    }
                }
                break;

                case 2: // Baja de Usuario
                {
                    System.out.print("Introduce el DNI del Usuario: ");
                    Usuario usuario = new Usuario(sc.next());

                    try {
                        usuario.bajaUsuario();
                        System.out.println("Baja de usuario correcta. DNI: " + usuario.getDni());
                    } catch (Exception e) {
                        System.out.println("Baja de usuario incorrecta: " + e.getMessage());
                    }
                }
                break;

                case 3: // Modificar Usuario
                {
                    Usuario usuario = new Usuario();

                    System.out.print("Introduce el DNI del Usuario: ");
                    usuario.setDni(sc.next());

                    System.out.print("Introduce el nuevo nombre: ");
                    String nombre = sc.next();

                    System.out.print("Introduce la nueva matrícula: ");
                    String matricula = sc.next();

                    try {
                        usuario.modificarUsuario(nombre, matricula);
                        System.out.println("Modificación de usuario correcta. DNI: " + usuario.getDni());
                    } catch (Exception e) {
                        System.out.println("Error en modificación de usuario: " + e.getMessage());
                    }
                }
                break;

                case 4: // Alta de Plaza
                {
                    Plaza plaza = null;

                    System.out.print("¿De qué tipo es la plaza? Normal, Eléctrica o Minusválida (n/e/m): ");
                    char opcion = sc.next().charAt(0);

                    if (opcion == 'n' || opcion == 'N') {
                        plaza = new Plaza();
                    } else if (opcion == 'e' || opcion == 'E') {
                        plaza = new PlazaElectrica();
                    } else if (opcion == 'm' || opcion == 'M') {
                        plaza = new PlazaMinusvalida();
                    } else {
                        System.out.println("Tipo de plaza no válido.");
                        break;
                    }

                    // El estado siempre es LIBRE al dar de alta (lo fija altaPlaza())
                    if (plaza instanceof PlazaElectrica pe) {
                        System.out.print("Introduce el precio de carga (€): ");
                        pe.setPrecioCarga(sc.nextDouble());
                    } else if (plaza instanceof PlazaMinusvalida pm) {
                        System.out.print("Introduce el descuento para esta plaza (%): ");
                        pm.setDescuento(sc.nextDouble());
                    }

                    try {
                        plaza.altaPlaza();
                        System.out.printf("Alta de plaza correcta. NÚMERO: %06d%n", plaza.getNumeroPlaza());
                    } catch (Exception e) {
                        System.out.println("Alta de plaza incorrecta: " + e.getMessage());
                    }
                }
                break;

                case 5: // Baja de Plaza (poner a FUERA_DE_SERVICIO)
                {
                    System.out.print("Introduce el número de plaza (ej: 000001): ");
                    int numPlaza = sc.nextInt();
                    Plaza plaza = new Plaza(numPlaza);

                    try {
                        plaza.bajaPlaza();
                        System.out.printf("Baja de plaza correcta. NÚMERO: %06d%n", plaza.getNumeroPlaza());
                    } catch (Exception e) {
                        System.out.println("Baja de plaza incorrecta: " + e.getMessage());
                    }
                }
                break;

                case 6: // Modificar Estado de Plaza
                {
                    System.out.print("Introduce el número de plaza (ej: 000001): ");
                    int numPlaza = sc.nextInt();
                    Plaza plaza = new Plaza(numPlaza);

                    System.out.print("Introduce el nuevo estado (LIBRE / FUERA_DE_SERVICIO): ");
                    String nuevoEstado = sc.next();

                    try {
                        plaza.modificarEstadoPlaza(nuevoEstado);
                        System.out.printf("Estado de plaza %06d modificado correctamente a %s.%n",
                                plaza.getNumeroPlaza(), nuevoEstado);
                    } catch (Exception e) {
                        System.out.println("Error al modificar estado de plaza: " + e.getMessage());
                    }
                }
                break;

                case 7: // Alta de Reserva
                {
                    Reserva reserva = new Reserva();

                    System.out.print("Introduce tu DNI: ");
                    reserva.setDniCliente(sc.next());

                    try {
                        reserva.altaReserva();
                        System.out.printf("Alta de reserva correcta. NÚMERO: %06d  |  Plaza asignada: %06d%n",
                                reserva.getNumeroReserva(), reserva.getNumeroPlaza());
                    } catch (Exception e) {
                        System.out.println("Alta de reserva incorrecta: " + e.getMessage());
                    }
                }
                break;

                case 8: // Baja de Reserva
                {
                    Reserva reserva = new Reserva();

                    System.out.print("Introduce el número de reserva (ej: 000001): ");
                    reserva.setNumeroReserva(sc.nextInt());

                    System.out.print("Introduce el DNI asociado a la reserva: ");
                    reserva.setDniCliente(sc.next());

                    System.out.print("Introduce el número de plaza asociado a la reserva: ");
                    reserva.setNumeroPlaza(sc.nextInt());

                    try {
                        reserva.bajaReserva();
                        System.out.printf("Baja de reserva correcta. NÚMERO: %06d  |  Coste: %.2f €%n",
                                reserva.getNumeroReserva(), reserva.getCoste());
                    } catch (Exception e) {
                        System.out.println("Baja de reserva incorrecta: " + e.getMessage());
                    }
                }
                break;

                case 9: // Listado de Usuarios
                {
                    System.out.println("========== LISTADO DE USUARIOS ==========");
                    System.out.printf("%-12s %-20s %-12s %s%n", "DNI", "NOMBRE", "MATRÍCULA", "DESCUENTO");
                    System.out.println("-----------------------------------------");
                    List<Usuario.UsuarioListado> usuariosListado = new ArrayList<>();
                    try {
                        Usuario.listadoUsuario(usuariosListado);
                        if (usuariosListado.isEmpty()) {
                            System.out.println("No hay usuarios registrados.");
                        } else {
                            for (Usuario.UsuarioListado u : usuariosListado) {
                                System.out.printf("%-12s %-20s %-12s %s%n",
                                        u.getDni(),
                                        u.getNombre(),
                                        u.getMatricula(),
                                        u.getDescuento() != null
                                                ? String.format("%.2f%%", u.getDescuento())
                                                : "-"
                                );
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener listado de usuarios: " + e.getMessage());
                    }
                    System.out.println("=========================================");
                }
                break;

                case 10: // Listado de Plazas
                {
                    System.out.println("========== LISTADO DE PLAZAS ==========");
                    System.out.printf("%-10s %-20s %-14s %s%n", "NÚMERO", "ESTADO", "PRECIO CARGA", "DESCUENTO");
                    System.out.println("----------------------------------------");
                    List<Plaza.PlazaListado> plazasListado = new ArrayList<>();
                    try {
                        Plaza.listadoPlaza(plazasListado);
                        if (plazasListado.isEmpty()) {
                            System.out.println("No hay plazas registradas.");
                        } else {
                            for (Plaza.PlazaListado p : plazasListado) {
                                System.out.printf("%-10s %-20s %-14s %s%n",
                                        String.format("%06d", p.getNumeroPlaza()),
                                        p.getEstado(),
                                        p.getPrecioCarga() != null
                                                ? String.format("%.2f €", p.getPrecioCarga())
                                                : "-",
                                        p.getDescuento() != null
                                                ? String.format("%.2f%%", p.getDescuento())
                                                : "-"
                                );
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener listado de plazas: " + e.getMessage());
                    }
                    System.out.println("========================================");
                }
                break;

                case 11: // Listado de Reservas
                {
                    System.out.println("========== LISTADO DE RESERVAS ==========");
                    List<Reserva> reservas = new ArrayList<>();
                    try {
                        Reserva.listadoReserva(reservas);
                        if (reservas.isEmpty()) {
                            System.out.println("No hay reservas registradas.");
                        } else {
                            for (Reserva r : reservas) {
                                System.out.printf(
                                        "Reserva: %06d | DNI: %s | Plaza: %06d | Entrada: %s | Salida: %s | Coste: %s%n",
                                        r.getNumeroReserva(),
                                        r.getDniCliente(),
                                        r.getNumeroPlaza(),
                                        r.getFechaHoraEntrada(),
                                        r.getFechaHoraSalida() != null ? r.getFechaHoraSalida() : "En curso",
                                        r.getCoste() != 0.0 ? String.format("%.2f €", r.getCoste()) : "Pendiente"
                                );
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error al obtener listado de reservas: " + e.getMessage());
                    }
                    System.out.println("=========================================");
                }
                break;

                case 0: // Salir
                {
                    /* CERRAR CONEXIÓN BD *************************************/
                    try {
                        System.out.println("Cerrando conexión BD...");
                        ConexionBD.cerrarConexion();
                        System.out.println("Conexión cerrada correctamente.");
                    } catch (Exception e) {
                        System.out.println("Error!!\n" + e.getMessage());
                    }
                    /* ********************************************************/
                }
                break;

                default:
                    System.out.println("Opción no válida. Introduce un número entre 0 y 11.");
                    break;
            }
        } while (op != 0);

    }

}