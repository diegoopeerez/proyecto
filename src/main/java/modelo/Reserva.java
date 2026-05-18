package modelo;

import java.time.LocalDateTime;

public class Reserva {

    private int numeroReserva;
    private String dniCliente;
    private int numeroPlaza;
    private LocalDateTime fechaHoraSalida;
    private LocalDateTime fechaHoraEntrada;
    private double coste;

    public Reserva() {
        numeroReserva = 0;
        dniCliente = "";
        numeroPlaza = 0;
        fechaHoraSalida = null;
        fechaHoraEntrada = null;
        coste = 0.0;
    }

    public Reserva(int numeroReserva, String dniCliente, int numeroPlaza) {
        this.numeroReserva = numeroReserva;
        this.dniCliente = dniCliente;
        this.numeroPlaza = numeroPlaza;
        this.fechaHoraSalida = null;
        this.fechaHoraEntrada = null;
        this.coste = 0.0;
    }

    public int getNumeroReserva() {
        return numeroReserva;
    }

    public void setNumeroReserva(int numeroReserva) {
        this.numeroReserva = numeroReserva;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public int getNumeroPlaza() {
        return numeroPlaza;
    }

    public void setNumeroPlaza(int numeroPlaza) {
        this.numeroPlaza = numeroPlaza;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public LocalDateTime getFechaHoraEntrada() {
        return fechaHoraEntrada;
    }

    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) {
        this.fechaHoraEntrada = fechaHoraEntrada;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }
}
