package vista;

import datos.ConexionBD;
import modelo.*;

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
                    plaza.setCategoria(sc.next());

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
