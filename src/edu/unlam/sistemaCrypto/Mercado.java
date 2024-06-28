package edu.unlam.sistemaCrypto;

public class Mercado {
    private String simbolo;
    private double capacidad;
    private double volumen24Horas;
    private double variacion7Dias;
    private int compras = 0;

    public Mercado(String simbolo, double capacidad, double volumen24Horas, double variacion7Dias) {
        this.simbolo = simbolo.toUpperCase();;
        this.capacidad = capacidad;
        this.volumen24Horas = volumen24Horas;
        this.variacion7Dias = variacion7Dias;
    }

	public double getCapacidad() {
		return this.capacidad;
	}

	public double getVolumen24h() {
		return this.volumen24Horas;
	}

	public double getVariacion7d() {
		return this.variacion7Dias;
	}

	public String getSimbolo() {
		return this.simbolo;
	}

	public void setCapacidad(double capacidad) {
		this.capacidad = capacidad;
	}

	public void setVolumen24h(double volumen24h) {
		this.volumen24Horas = volumen24h;
		
	}

	public void setVariacion7d(double variacion7d) {
		this.variacion7Dias = variacion7d;
	}

	public int getCompras() {
		return compras;
	}

	public void setCompras(int compras) {
		this.compras = compras;
	}
}
