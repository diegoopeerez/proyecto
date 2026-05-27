package vista;

import datos.ConexionBD;
import modelo.Usuario;
import modelo.UsuarioVIP;

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
        System.out.println("8.-Modificación de Reserva");
        System.out.println("9.-Listado de Usuarios");
        System.out.println("10.-Listado de Plazas");
        System.out.println("11.-Listado de Reservas");
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

                    System.out.print("Introduce el nombre del Usuario");
                    usuario.setNombre(sc.next());

                    System.out.print("Introduce la matricula del Usuario");
                    usuario.setNombre(sc.next());

                    if (usuario instanceof UsuarioVIP uv) {
                        System.out.print("Introduce el descuento del Usuario");
                        uv.setDescuento(sc.nextDouble());
                    }

                    try {
                        usuario.altaUsuario();
                        System.out.println("Alta de usuario correcta. DNI: " + usuario.getDni());
                    } catch (Exception e) {
                        System.out.println("Alta de usuario incorrecta" + e.getMessage());
                    }

                }

//                break;
//                case 2: //Baja de Usuario.
//                {
//                    System.out.print("Introduce el id del Aula: ");
//                    Aula aula = new Aula(sc.nextInt());
//
//                    try {
//                        aula.bajaAula();
//                        System.out.println("Baja de aula correcta. ID: " + aula.getId());
//                    } catch (Exception e) {
//                        System.out.println("Baja de aula incorrecta" + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 3: // Modificar Usuario
//                {
//
//                    Aula aula = new Aula();
//
//                    System.out.print("Introduce el id de Aula: ");
//                    aula.setId(sc.nextInt());
//
//                    System.out.print("Introduce el nuevo nombre: ");
//                    String nombre = sc.next();
//
//                    try {
//                        aula.modificarAula(nombre);
//                    } catch (Exception e) {
//                        System.out.println("Error" + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 4: //Alta de Plaza
//                {
//                    Armario armario = new Armario();
//
//                    System.out.print("Introduce el id del Aula: ");
//                    armario.setIdAula(sc.nextInt());
//
//                    System.out.print("Introduce el id de Armario: ");
//                    armario.setId(sc.nextInt());
//
//                    System.out.print("Introduce el nombre del Armario");
//                    armario.setNombre(sc.next());
//
//                    try {
//                        armario.altaArmario();
//                        System.out.println("Alta de armario correcta. ID: " + armario.getId());
//                    } catch (Exception e) {
//                        System.out.println("Alta de armario incorrecta" + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 5: //Baja de Plaza
//                {
//
//                    Armario armario = new Armario();
//
//                    System.out.print("Introduce el id del Aula: ");
//                    armario.setIdAula(sc.nextInt());
//
//                    System.out.print("Introduce el id de Armario: ");
//                    armario.setId(sc.nextInt());
//
//                    try {
//                        armario.bajaArmario();
//                        System.out.println("Baja de armario correcta. ID: " + armario.getId());
//                    } catch (Exception e) {
//                        System.out.println("Baja de armario incorrecta" + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 6: //Alta de Reserva
//                {
//
//                    Producto producto = new Producto();
//
//                    System.out.print("Introduce el id del producto: ");
//                    producto.setId(sc.next());
//
//                    System.out.print("Introduce el nombre del producto: ");
//                    producto.setNombre(sc.next());
//
//                    System.out.print("Introduce el id del Aula: ");
//                    producto.setIdAula(sc.nextInt());
//
//                    System.out.print("Introduce el id del Armario: ");
//                    producto.setIdArmario(sc.nextInt());
//
//                    char seguir;
//
//                    do {
//                        Referencia referencia = new Referencia();
//                        System.out.print("Introduce una referencia del producto: ");
//                        referencia.setNumRef(sc.next());
//
//                        referencia.setIdProducto(producto.getId());
//
//                        if (referencia.altaReferencia(producto.getNumRefs())) {
//                            System.out.print("Quiere introducir otra referencia? (s/n): ");
//                            seguir = sc.next().charAt(0);
//                        } else {
//                            break;
//                        }
//
//                    } while (seguir == 's');
//
//                    int opp;
//                    System.out.print("Introduce la categoría del producto (0:OTRA,1:HW,2:SW): ");
//                    opp = sc.nextInt();
//                    producto.setCategoria(opp);
//
//                    System.out.print("Introduce la descripcion del producto: ");
//                    producto.setDescripcion(sc.next());
//
//                    try {
//                        producto.altaProducto();
//                        System.out.println("Alta de producto correcta. ID: " + producto.getId());
//                    } catch (Exception e) {
//                        System.out.println("Alta de producto incorrecta" + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 7: //Baja de Reserva
//                {
//
//                    Producto producto = new Producto();
//
//                    System.out.print("Introduce el id (PXXXXXX) del producto: ");
//                    producto.setId(sc.next());
//
//                    String numRef;
//                    System.out.print("Introduce el número de referencia del producto: ");
//                    numRef = sc.next();
//
//                    String causa;
//                    System.out.print("Causa de la baja?: ");
//                    causa = sc.next();
//
//                    try {
//                        producto.bajaProducto(numRef, causa);
//                        System.out.println("Baja de producto correcta ID: " + producto.getId());
//                    } catch (Exception e) {
//                        System.out.println("Error " + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 8: // Modificar Reserva
//                {
//
//                    Aula aula = new Aula();
//
//                    System.out.print("Introduce el id de Aula: ");
//                    aula.setId(sc.nextInt());
//
//                    System.out.print("Introduce el nuevo nombre: ");
//                    String nombre = sc.next();
//
//                    try {
//                        aula.modificarAula(nombre);
//                    } catch (Exception e) {
//                        System.out.println("Error" + e.getMessage());
//                    }
//
//                }
//                break;
//
//                case 9: //Listado de Usuarios
//                {
//                    System.out.println("LISTADO DE AULAS");
//                    System.out.println("---------------------------");
//                    List<Aula> aulas = new ArrayList<>();
//                    Aula.listadoAulas(aulas);
//                    for (Aula aula : aulas) {
//                        System.out.printf("ID: %d, NOMBRE: %s", aula.getId(), aula.getNombre());
//                        System.out.println("");
//                    }
//
//                    break;
//
//                }
//
//                case 10: //Listado de Plazas
//                {
//                    System.out.println("LISTADO DE ARMARIOS");
//                    System.out.println("---------------------------");
//                    List<Armario> armarios = new ArrayList<>();
//                    Armario.listadoArmarios(armarios);
//
//                    for (Armario armario : armarios) {
//                        System.out.printf("\tID: %d, NOMBRE: %s", armario.getId(), armario.getNombre());
//                        System.out.println("");
//                    }
//
//                    break;
//
//                }
//
//                case 11: //Listado de Reservas
//                {
//
//                    int idAula;
//                    System.out.print("Introduce el id del aula");
//                    idAula = sc.nextInt();
//
//                    int idArmario;
//                    System.out.print("Introduce el id del armario");
//                    idArmario = sc.nextInt();
//
//                    System.out.println("LISTADO DE PRODUCTOS");
//                    System.out.println("--------------------");
//                    List<Producto> productos = new ArrayList<>();
//                    List<Referencia> referencias = new ArrayList<>();
//                    Producto.listadoProductos(productos, idAula, idArmario);
//
//                    if (idAula != 0 && idArmario != 0) {
//                        for (Producto producto : productos) {
//                            if (producto.getIdAula() == idAula && producto.getIdArmario() == idArmario) {
//                                System.out.printf("ID: %s, NOMBRE: %s, AULA: %s, ARMARIO: %s, CATEGORIA: %s,DESCRIPCION: %s",
//                                        producto.getId(),
//                                        producto.getNombre(),
//                                        producto.getIdAula(),
//                                        producto.getIdArmario(),
//                                        producto.getCategoria(),
//                                        producto.getDescripcion());
//                                System.out.println("");
//                                referencias.clear();
//                                Referencia.listadoReferencias(referencias, producto.getId());
//                                for (Referencia referencia : referencias) {
//                                    System.out.printf("\tNUMREF: %s, BAJA: %s",
//                                            referencia.getNumRef(),
//                                            referencia.isBaja());
//                                    System.out.println("");
//                                }
//                                System.out.println("");
//                            }
//                        }
//                    }
//
//                }
//                break;
//
//                case 0: //Salir
//                {
//                    /* CERRAR CONEXIÓN BD *************************************/
//                    try {
//                        System.out.println("Cerrando conexión BD...");
//                        ConexionBD.cerrarConexion();
//                        System.out.println("Conexión cerrada correctamente.");
//                    } catch (Exception e) {
//                        System.out.println("Error!!\n" + e.getMessage());
//                    }
//                    /* ********************************************************/
//                    break;
//                }
//                default:
//
           }
        } while (op != 0);

    }

}
