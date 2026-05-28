package vista;

import datos.ConexionBD;
import modelo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static int menu() {
        int op;
        Scanner sc = new Scanner(System.in);
        System.out.println("1.-Alta de Usuario");
        System.out.println("2.-Baja de Usuario");
        System.out.println("3.-Modificación de Usuario");
        System.out.println("4.-Alta de Plaza");
        System.out.println("5.-Baja de Plaza");
        System.out.println("6.-Alta de Reserva");
        System.out.println("7.-Baja de Reserva");
        System.out.println("8.-Listado de Usuarios");
        System.out.println("9.-Listado de Plazas");
        System.out.println("10.-Listado de Reservas");
        System.out.println("0.-Salir");
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
                case 1: //Alta de Usuario
                {
                    Usuario usuario = null;

                    System.out.println("El usuario es VIP? (s,n): ");
                    char opcion = sc.next().charAt(0);

                    if (opcion == 's') {
                        usuario = new UsuarioVIP();
                    } else if (opcion == 'n') {
                        usuario = new Usuario();
                    }

                    System.out.print("Introduce el dni del Usuario: ");
                    usuario.setDni(sc.next());

                    System.out.print("Introduce el nombre del Usuario: ");
                    usuario.setNombre(sc.next());

                    System.out.print("Introduce la matricula del Usuario: ");
                    usuario.setMatricula(sc.next());

                    if (usuario instanceof UsuarioVIP uv) {
                        System.out.print("Introduce el descuento del Usuario: ");
                        uv.setDescuento(sc.nextDouble());
                    }

                    try {
                        usuario.altaUsuario();
                        System.out.println("Alta de usuario correcta. DNI: " + usuario.getDni());
                    } catch (Exception e) {
                        System.out.println("Alta de usuario incorrecta" + e.getMessage());
                    }

                }

                break;
                case 2: //Baja de Usuario.
                {
                    System.out.print("Introduce el dni del Usuario: ");
                    Usuario usuario = new Usuario(sc.next());

                    try {
                        usuario.bajaUsuario();
                        System.out.println("Baja de usuario correcta. DNI: " + usuario.getDni());
                    } catch (Exception e) {
                        System.out.println("Baja de usuario incorrecta" + e.getMessage());
                    }

                }
                break;

                case 3: // Modificar Usuario
                {
                    Usuario usuario = new Usuario();

                    System.out.print("Introduce el dni del Usuario: ");
                    usuario.setDni(sc.next());

                    System.out.print("Introduce el nuevo nombre (o deja igual): ");
                    String nombre = sc.next();

                    System.out.print("Introduce la nuevo matricula (o deja igual): ");
                    String matricula = sc.next();

                    try {
                        usuario.modificarUsuario(nombre, matricula);
                    } catch (Exception e) {
                        System.out.println("Error" + e.getMessage());
                    }

                }
                break;

                case 4: //Alta de Plaza
                {
                    Plaza plaza = null;

                    System.out.println("De que tipo es la plaza normal, electrica o minusvalida? (n,e,m): ");
                    char opcion = sc.next().charAt(0);

                    if (opcion == 'n') {
                        plaza = new Plaza();
                    } else if (opcion == 'e') {
                        plaza = new PlazaElectrica();
                    } else if (opcion == 'm') {
                        plaza = new PlazaMinusvalida();
                    }

                    System.out.print("Introduce el numero de Plaza: ");
                    plaza.setNumeroPlaza(sc.nextInt());

                    System.out.print("Introduce el estado de la plaza, OCUPADA, RESERVADA, LIBRE o FUERA_DE_SERVICIO");
                    plaza.setEstado(sc.next());

                    if (plaza instanceof PlazaElectrica pe) {
                        System.out.print("Introduce el precio de carga: ");
                        pe.setPrecioCarga(sc.nextDouble());
                    } else if (plaza instanceof PlazaMinusvalida pm) {
                        System.out.print("Introduce el descuento: ");
                        pm.setDescuento(sc.nextDouble());
                    }

                    try {
                        plaza.altaPlaza();
                        System.out.println("Alta de plaza correcta. NUMERO: " + plaza.getNumeroPlaza());
                    } catch (Exception e) {
                        System.out.println("Alta de plaza incorrecta" + e.getMessage());
                    }

                }
                break;

                case 5: //Baja de Plaza (poner a fuera de servicio)
                {
                    Plaza plaza = new Plaza();

                    System.out.print("Introduce el numero de Plaza: ");
                    plaza.setNumeroPlaza(sc.nextInt());

                    try {
                        plaza.bajaPlaza();
                        System.out.println("Baja de plaza correcta. NUMERO: " + plaza.getNumeroPlaza());
                    } catch (Exception e) {
                        System.out.println("Baja de plaza incorrecta" + e.getMessage());
                    }

                }
                break;

                case 6: //Alta de Reserva
                {

                    Reserva reserva = new Reserva();

                    System.out.print("Introduce el numero de reserva: ");
                    reserva.setNumeroReserva(sc.nextInt());

                    System.out.print("Introduce tu dni: ");
                    reserva.setDniCliente(sc.next());

                    //No porque se supone que te asigna una que este libre automaticamente
                    System.out.print("Introduce el numero de plaza: ");
                    reserva.setNumeroPlaza(sc.nextInt());


                    try {
                        reserva.altaReserva();
                        System.out.println("Alta de reserva correcta. NUMERO: " + reserva.getNumeroReserva());
                    } catch (Exception e) {
                        System.out.println("Alta de reserva incorrecta " + e.getMessage());
                    }

                }
                break;

                case 7: //Baja de Reserva
                {

                    Reserva reserva = new Reserva();

                    System.out.print("Introduce el numero (RXXXXXX) de reserva: ");
                    reserva.setNumeroReserva(sc.nextInt());

                    System.out.print("Introduce el dni asociado a la reserva: ");
                    reserva.setDniCliente(sc.next());

                    System.out.print("Introduce el numero de la plaza asociado a la reserva: ");
                    reserva.setNumeroPlaza(sc.nextInt());

                    try {
                        reserva.bajaReserva();
                        System.out.println("Baja de reserva correcta NUMERO: " + reserva.getNumeroReserva());
                    } catch (Exception e) {
                        System.out.println("Baja de reserva incorrecta " + e.getMessage());
                    }

                }
                break;

                case 8: //Listado de Usuarios
                {
                    System.out.println("LISTADO DE USUARIOS");
                    System.out.println("-------------------");
                    List<Usuario.UsuarioListado> usuariosListado = new ArrayList<>();
                    Usuario.listadoUsuario(usuariosListado);
                    for (Usuario.UsuarioListado usuarioListado : usuariosListado) {
                        System.out.printf(
                                "DNI: %s, NOMBRE: %s, MATRICULA: %s, DESCUENTO: %.2f%n",
                                usuarioListado.getDni(),
                                usuarioListado.getNombre(),
                                usuarioListado.getMatricula(),
                                usuarioListado.getDescuento() != null ? usuarioListado.getDescuento() : 0.0
                        );
                    }
                }
                break;

                case 9: //Listado de Plazas
                {
                    System.out.println("LISTADO DE PLAZAS");
                    System.out.println("-----------------");
                    List<Plaza.PlazaListado> plazasListado = new ArrayList<>();
                    Plaza.listadoPlaza(plazasListado);
                    for (Plaza.PlazaListado plazaListado : plazasListado) {
                        System.out.printf(
                                "NUMERO DE PLAZA: %d, ESTADO: %s, PRECIO DE CARGA: %.2f, DESCUENTO: %.2f",
                                plazaListado.getNumeroPlaza(),
                                plazaListado.getEstado(),
                                plazaListado.getPrecioCarga() != null ? plazaListado.getPrecioCarga() : 0.0,
                                plazaListado.getDescuento() != null ? plazaListado.getDescuento() : 0.0
                        );
                    }
                }
                break;

                case 10: //Listado de Reservas
                {
                    System.out.println("LISTADO DE RESERVAS");
                    System.out.println("-------------------");
                    List<Reserva> reservas = new ArrayList<>();
                    Reserva.listadoReserva(reservas);
                    for (Reserva reserva : reservas) {
                        System.out.printf(
                                "NUMERO DE RESERVA: %d, DNI DEL CLIENTE: %s, NUMERO DE PLAZA: %d, FECHA Y HORA DE ENTRADA: %s, FECHA Y HORA DE SALIDA: %s, COSTE: %s%n",
                                reserva.getNumeroReserva(),
                                reserva.getDniCliente(),
                                reserva.getNumeroPlaza(),
                                reserva.getFechaHoraEntrada(),
                                reserva.getFechaHoraSalida() != null ? reserva.getFechaHoraSalida() : "",
                                reserva.getCoste() != 0 ? String.format("%.2f", reserva.getCoste()) : ""
                        );
                    }
                }
                break;

                case 0: //Salir
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
                    break;
                }
                default:

            }
        } while (op != 0);

    }

}
