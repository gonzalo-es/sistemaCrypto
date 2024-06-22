package edu.unlam.sistemaCrypto;

public class Criptomoneda {
	private String nombre;
    private String simbolo;
    private double precioDolarBase;

    public Criptomoneda(String nombre, String simbolo, double precioDolarBase) {
        this.nombre = nombre.toUpperCase();
        this.simbolo = simbolo.toUpperCase();;
        this.precioDolarBase = precioDolarBase;
    }

	public String getNombre() {
		// TODO Auto-generated method stub
		return this.nombre;
	}

	public String getSimbolo() {
		return this.simbolo;
	}

	public double getPrecio() {
		return this.precioDolarBase;
	}

	public void setPrecioBase(double precio) {
		this.precioDolarBase = precio;
		
	}

	public void setNombre(String nuevoNombre) {
		this.nombre = nuevoNombre.toUpperCase();
	}
	
	

	
}
