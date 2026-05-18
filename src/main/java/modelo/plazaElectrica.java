package modelo;

public class plazaElectrica extends Plaza {

    private double precioCarga;

    public plazaElectrica() {
        super();
        precioCarga = 0.0;
    }

    public plazaElectrica(int numeroPlaza) {
        super(numeroPlaza);
        precioCarga = 0.0;
    }

    public double getPrecioCarga() {
        return precioCarga;
    }

    public void setPrecioCarga(double precioCarga) {
        this.precioCarga = precioCarga;
    }
}
