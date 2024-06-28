package edu.unlam.sistemaCrypto;

import java.util.*;

public class Trader extends Usuario {
    private String numeroCuentaBancaria;
    private String nombreBanco;
    private double saldo;
    private Map<String, Double> historicoCompras = new HashMap<>();

    public Trader(String nombre, String numeroCuentaBancaria, String nombreBanco, double saldo) {
        super(nombre);
        this.setNumeroCuentaBancaria(numeroCuentaBancaria);
        this.setNombreBanco(nombreBanco);
        this.saldo = saldo;
    }


	public void consultarSaldo() {
		System.out.println("Saldo Actual $"+saldo);
		
	}
	
	 public Map<String, Double> getHistoricoCompras() {
	        return historicoCompras;
	 }


	public double getSaldo() {
		return this.saldo;
	}


	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}


	public String getNombreBanco() {
		return nombreBanco;
	}


	public void setNombreBanco(String nombreBanco) {
		this.nombreBanco = nombreBanco;
	}


	public String getNumeroCuentaBancaria() {
		return numeroCuentaBancaria;
	}


	public void setNumeroCuentaBancaria(String numeroCuentaBancaria) {
		this.numeroCuentaBancaria = numeroCuentaBancaria;
	}
}