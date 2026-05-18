package modelo;

public class PlazaMinusvalida extends Plaza {

    private double descuento;

    public PlazaMinusvalida() {
        super();
        descuento = 0.0;
    }

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
}
