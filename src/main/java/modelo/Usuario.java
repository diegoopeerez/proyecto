package modelo;

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
}


