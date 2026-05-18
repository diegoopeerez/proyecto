package modelo;

public class usuarioVIP extends Usuario {
    private String descuento;

    public usuarioVIP() {
        super();
        descuento = "";
    }

    public usuarioVIP(String dni) {
        super(dni);
        this.descuento = "";
    }

    public String getDescuento() {
        return descuento;
    }

    public void setDescuento(String descuento) {
        this.descuento = descuento;
    }
}
