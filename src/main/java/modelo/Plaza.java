package modelo;

public class Plaza {

    protected int numeroPlaza;
    protected EstadoPlaza categoria;

    protected enum EstadoPlaza {
        LIBRE,
        OCUPADA,
        RESERVADA,
        FUERA_DE_SERVICIO
    }

    public Plaza() {
        numeroPlaza = 0;
    }

    public Plaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    public int getNumeroPlaza() {
        return numeroPlaza;
    }

    public void setNumeroPlaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

}
