package edu.unlam.sistemaCrypto;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class SistemaCriptomonedas {
    private Map<String, Criptomoneda> criptomonedas = new HashMap<>();
    private Map<String, Mercado> mercados = new HashMap<>();
    private Map<String, Usuario> usuarios = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);

    public SistemaCriptomonedas() {
        cargarDatos();
    }

    private void cargarDatos() {
        cargarCriptomonedas();
        cargarMercados();
        cargarUsuarios();
    }
    

    private void cargarCriptomonedas() {
        String archivoCriptomonedas = Paths.get("").toAbsolutePath().toString();
        archivoCriptomonedas = archivoCriptomonedas + File.separator + "criptomonedas.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCriptomonedas))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String nombre = datos[0];
                String simbolo = datos[1];
                double precioBase = Double.parseDouble(datos[2]);
                Criptomoneda cripto = new Criptomoneda(nombre, simbolo, precioBase);
                criptomonedas.put(simbolo, cripto);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de criptomonedas: " + e.getMessage());
        }
    }

    private void cargarMercados() {
        String archivoMercados = Paths.get("").toAbsolutePath().toString();
        archivoMercados = archivoMercados + File.separator + "mercados.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(archivoMercados))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String simbolo = datos[0];
                double capacidad = Double.parseDouble(datos[1]);
                double volumen24h = Double.parseDouble(datos[2].replace("%", ""));
                double variacion7d = Double.parseDouble(datos[3].replace("%", ""));
                Mercado mercado = new Mercado(simbolo, capacidad, volumen24h, variacion7d);
                mercados.put(simbolo, mercado);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de mercados: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        // Obtener la ruta del archivo `usuarios.csv`
        String archivoUsuarios = Paths.get("").toAbsolutePath().toString();
        archivoUsuarios = archivoUsuarios + File.separator + "usuarios.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(archivoUsuarios))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                String nombre = datos[0];
                if (datos[1].equalsIgnoreCase("administrador")) {
                    Administrador admin = new Administrador(nombre);
                    usuarios.put(nombre, admin);
                } else {
                    String numeroCuenta = datos[1];
                    String nombreBanco = datos[2];
                    double saldo = Double.parseDouble(datos[3]);
                    Trader trader = new Trader(nombre, numeroCuenta, nombreBanco, saldo);
                    usuarios.put(nombre, trader);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de usuarios: " + e.getMessage());
        }
    }
    
    private void comprarCriptomoneda(Trader trader) {
        System.out.print("Ingrese el símbolo de la criptomoneda a comprar: ");
        String simbolo = scanner.nextLine();
        simbolo = simbolo.toUpperCase();

        Criptomoneda cripto = criptomonedas.get(simbolo);
        Mercado mercado = mercados.get(simbolo);

        if (cripto == null || mercado == null) {
            System.out.println("Criptomoneda no encontrada.");
            return;
        }

        System.out.println("Cantidad de Dinero disponible: "+trader.getSaldo());
        System.out.println("Valor Actual de Criptomoneda: "+cripto.getPrecio());
        System.out.println("Ingrese la cantidad a comprar: ");
        double cantidad = scanner.nextDouble();
        scanner.nextLine(); 

        double precioTotal = cantidad * cripto.getPrecio();
        System.out.println("Valor en dólares: $" + precioTotal);
        System.out.println("Capacidad disponible: " + mercado.getCapacidad());

        if (precioTotal > trader.getSaldo()) {
            System.out.println("Saldo insuficiente. Por favor, ingrese más dinero en su cuenta.");
            return;
        }
        
        if(mercado.getCapacidad() < cantidad) {
            System.out.println("No hay capacidad suficiente de la criptomoneda");
            return;
        }

        System.out.print("¿Confirma la compra? (s/n): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("s")) {
            mercado.setCapacidad(mercado.getCapacidad() - cantidad);
            mercado.setVolumen24h(mercado.getVolumen24h() * 1.05);
            mercado.setVariacion7d(mercado.getVariacion7d() * 1.05);
            mercado.setCompras(mercado.getCompras()+1);
            
            cripto.setCantidadVendida(cripto.getCantidadVendida()+1);
            if (cripto.getCantidadVendida() > 1000) {
                cripto.setPrecioBase(cripto.getPrecio() * 1.10);
            }

            trader.setSaldo(trader.getSaldo() - precioTotal);

            // Actualizar el histórico de compras del usuario
            Map<String, Double> historico = trader.getHistoricoCompras();
            historico.put(simbolo, historico.getOrDefault(simbolo, 0.0) + cantidad);

            System.out.println("Compra realizada con éxito.");
        } else {
            System.out.println("Compra cancelada.");
        }
    }



    private void iniciarSesion() {
    	escribirDatosEnCSV();
    	System.out.print("Ingrese su nombre de usuario: ");
        String nombre = scanner.nextLine();

        if (usuarios.containsKey(nombre) && usuarios.get(nombre) instanceof Administrador) {
            Administrador admin = (Administrador) usuarios.get(nombre);
            mostrarMenuAdministrador(admin);
        } else if (usuarios.containsKey(nombre) && usuarios.get(nombre) instanceof Trader) {
            Trader trader = (Trader) usuarios.get(nombre);
            mostrarMenuTrader(trader);
        } else {
            System.out.println("Usuario no encontrado");
            registrarUsuario();
        }
    }
    
    private void escribirDatosEnCSV() {
        escribirCriptomonedasEnCSV();
        escribirMercadosEnCSV();
        escribirUsuarios();
    }
    
    private void escribirUsuarios() {
        try (FileWriter writer = new FileWriter("usuarios.csv")) {
            for(String usuario : usuarios.keySet()) {
            	if ( usuarios.get(usuario) instanceof Administrador) {
                    Administrador admin = (Administrador) usuarios.get(usuario);
                    writer.append(admin.getNombre())
                    .append(",")
                    .append("administrador")
                    .append("\n");
                } else {
                    Trader trader = (Trader) usuarios.get(usuario);
                    writer.append(trader.getNombre())
                    .append(",")
                    .append(trader.getNumeroCuentaBancaria())
                    .append(",")
                    .append(trader.getNombreBanco())
                    .append(",")
                    .append(String.valueOf(trader.getSaldo()))
                    .append("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo criptomonedas.csv: " + e.getMessage());
        }
    }



    private void escribirCriptomonedasEnCSV() {
        try (FileWriter writer = new FileWriter("criptomonedas.csv")) {
            for (Criptomoneda cripto : criptomonedas.values()) {
                writer.append(cripto.getNombre())
                        .append(",")
                        .append(cripto.getSimbolo())
                        .append(",")
                        .append(String.valueOf(cripto.getPrecio()))
                        .append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo criptomonedas.csv: " + e.getMessage());
        }
    }

    private void escribirMercadosEnCSV() {
        try (FileWriter writer = new FileWriter("mercados.csv")) {
            for (Mercado mercado : mercados.values()) {
                writer.append(mercado.getSimbolo())
                        .append(",")
                        .append(String.valueOf(mercado.getCapacidad()))
                        .append(",")
                        .append(String.valueOf(mercado.getVolumen24h()))
                        .append(",")
                        .append(String.valueOf(mercado.getVariacion7d()))
                        .append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo mercados.csv: " + e.getMessage());
        }
    }
    
    private void registrarUsuario() {
    	System.out.print("¿Desea registrar un nuevo usuario? 1.Si 2.No");
        int opcion = scanner.nextInt();
        scanner.nextLine(); 
        
        switch (opcion) {
	        case 1:
	        	System.out.print("Usuario:");
	            String usuario = scanner.nextLine();
	            
	            if(usuarios.containsKey(usuario)) {
	            	System.out.print("Nombre de usuario ya registrado, por favor ingrese otro");
	            	registrarUsuario();
	            }
	            
	        	System.out.print("Nro de Cuenta:");
	            String numeroCuenta = scanner.nextLine();
	            
	        	System.out.print("Nombre de Banco:");
	            String nombreBanco = scanner.nextLine();
	            
	        	System.out.print("Saldo:");
	            double saldo = scanner.nextDouble();
	            
	            scanner.nextLine();
	            
	            if(saldo < 0) {
	            	System.out.print("El saldo debe ser superior a 0");
	            	registrarUsuario();
	            }
	            
	            Trader trader = new Trader(usuario, numeroCuenta, nombreBanco, saldo);
	            
	            usuarios.put(usuario, trader);
	            System.out.println("Usuario creado exitosamente");
	            iniciarSesion();
	            break;
	        case 2:
	            iniciarSesion();
	            break;
	        default:
	            System.out.println("Opción no válida. Intente nuevamente.");
	            registrarUsuario();
        }
        
    }

    private void mostrarMenuAdministrador(Administrador admin) {
        while (true) {
            System.out.println("\n--- Menú Administrador ---");
            System.out.println("1. Crear Criptomoneda");
            System.out.println("2. Modificar criptomoneda");
            System.out.println("3. Eliminar criptomoneda");
            System.out.println("4. Consultar Criptomoneda");
            System.out.println("5. Consultar estado actual del mercado");
            System.out.println("6. Salir");
            System.out.print("Ingrese su opción (1 - 6): ");

            int opcion = scanner.nextInt();
            String simbolo;
            scanner.nextLine();  // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    agregarCriptomoneda();
                    break;
                case 2:
                	System.out.println("Ingrese el simbolo de la criptomoneda a modificar:");
                	simbolo = scanner.nextLine();
                    modificarCriptomoneda(simbolo);
                    break;
                case 3:
                	System.out.println("Ingrese el simbolo de la criptomoneda a eliminar:");
                	simbolo = scanner.nextLine();
                    eliminarCriptomoneda(simbolo);
                    break;
                case 4:
                    consultarCriptomoneda();
                    break;
                case 5:
                    consultarMercado();
                    break;
                case 6:
                	System.out.println("Saliendo del Sistema.");
                	//escribirDatosEnCSV();
                	iniciarSesion();
                    return;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    private void agregarCriptomoneda() {
    	System.out.print("Ingrese el nombre de la criptomoneda: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese el símbolo de la criptomoneda: ");
        String simbolo = scanner.nextLine();
        simbolo = simbolo.toUpperCase();
        System.out.print("Ingrese el precio base en dólares: ");
        double precioBase = scanner.nextDouble();
        scanner.nextLine(); // Consumir el salto de línea

        if (criptomonedas.containsKey(simbolo)) {
            System.out.println("La criptomoneda ya existe en el archivo criptomonedas.csv.");
            System.out.print("¿Desea modificar los parámetros existentes? (s/n): ");
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                modificarCriptomoneda(simbolo);
            } else {
                System.out.println("Operación cancelada.");
            }
            return;
        }

        // Crear y agregar la nueva criptomoneda
        Criptomoneda nuevaCripto = new Criptomoneda(nombre, simbolo, precioBase);
        criptomonedas.put(simbolo, nuevaCripto);
        
        // Crear y agregar la entrada en mercados
        Mercado nuevoMercado = new Mercado(simbolo, 500, 1.01, 1.01);
        mercados.put(simbolo, nuevoMercado);
        
        System.out.println("Criptomoneda agregada con éxito.");
    }

    private void modificarCriptomoneda(String simbolo) {
    	simbolo = simbolo.toUpperCase();
    	Criptomoneda cripto = criptomonedas.get(simbolo);
    	if (cripto == null) {
    	    System.out.println("Criptomoneda no encontrada.");
    	    return;
    	}

    	System.out.print("Ingrese el nuevo nombre de la criptomoneda (actual: " + cripto.getNombre() + "): ");
    	String nuevoNombre = scanner.nextLine();

    	System.out.print("Ingrese el nuevo precio base en dólares (actual: " + cripto.getPrecio() + "): ");
    	double nuevoPrecioBase = scanner.nextDouble();
    	scanner.nextLine(); // Consumir el salto de línea

    	// Actualizar los datos de la criptomoneda
    	cripto.setNombre(nuevoNombre);
    	cripto.setPrecioBase(nuevoPrecioBase);


    	System.out.println("Criptomoneda modificada con éxito.");
    }

    private void eliminarCriptomoneda(String simbolo) {
    	simbolo = simbolo.toUpperCase();
    	Criptomoneda cripto = criptomonedas.get(simbolo);
        if (cripto == null) {
            System.out.println("Criptomoneda no encontrada.");
            return;
        }

        // Eliminar la criptomoneda del mapa
        criptomonedas.remove(simbolo);

        // Eliminar del mapa de mercados
        mercados.remove(simbolo);

        System.out.println("Criptomoneda eliminada con éxito.");
    }



    private void mostrarMenuTrader(Trader trader) {
        while (true) {
            System.out.println("\n--- Menú Trader ---");
            System.out.println("1. Comprar criptomoneda");
            System.out.println("2. Vender criptomoneda");
            System.out.println("3. Consultar criptomoneda");
            System.out.println("4. Recomendar Criptomonedas");
            System.out.println("5. Consultar Estado actual del mercado");
            System.out.println("6. Visualizar archivo de transacciones (Historico)");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine();  // Consumir el salto de línea

            switch (opcion) {
                case 1:
                	comprarCriptomoneda(trader);
                    break;
                case 2:
                    venderCriptomoneda(trader);
                    break;
                case 3:
                    consultarCriptomoneda();
                    break;
                case 4:
                    recomendarCompra();
                    mostrarMenuTrader(trader);
                    break;
                case 5:
                	consultarMercado();
                	mostrarMenuTrader(trader);
                    break;
                case 6:
                	mostrarHistoricoCompras(trader);
                    break;
                case 7:
                	System.out.println("Saliendo del Sistema.");
                	//escribirDatosEnCSV();
                	iniciarSesion();
                	break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    private void mostrarHistoricoCompras(Trader trader) {
        System.out.println("\n--- Histórico de Compras ---");
        Map<String, Double> historicoCompras = trader.getHistoricoCompras();
        if (historicoCompras.isEmpty()) {
            System.out.println("No hay transacciones históricas.");
        } else {
            for (Map.Entry<String, Double> entry : historicoCompras.entrySet()) {
                System.out.println("Símbolo: " + entry.getKey() + ", Cantidad: " + entry.getValue());
            }
        }
        mostrarMenuTrader(trader);
    }

	private void consultarMercado() {
    	System.out.println("Estado Actual del Mercado");
    	
    	for (Mercado mercado: mercados.values()) {
    		System.out.print("Simbolo: " + mercado.getSimbolo());
            System.out.print("\tCapacidad: " + mercado.getCapacidad());
            System.out.print("\tVolumen 24h: " + mercado.getVolumen24h());
            System.out.print("\tVariación 7d: " + mercado.getVariacion7d());
            System.out.println();
    	}
		
	}

	private void consultarCriptomoneda() {
    	System.out.println("Ingrese Simbolo de criptomoneda");
    	
    	String simbolo = scanner.nextLine();
    	simbolo = simbolo.toUpperCase();
    	if(criptomonedas.containsKey(simbolo)) {
    		Criptomoneda moneda = criptomonedas.get(simbolo);
    		Mercado mercado = mercados.get(simbolo);
    		System.out.println("Nombre: "+moneda.getNombre()+"\t Símbolo: "+moneda.getSimbolo()+"\t Precio en dólares: "+moneda.getPrecio());
    		System.out.println("\nCapacidad: "+mercado.getCapacidad()+"\t Volumen 24hs: "+mercado.getVolumen24h()+"%\t Volumen Semanal: "+mercado.getVariacion7d()+"%");
    	}
		
	}




    private void venderCriptomoneda(Trader trader) {
        System.out.print("Ingrese el símbolo de la criptomoneda a vender: ");
        String simbolo = scanner.nextLine();
        simbolo = simbolo.toUpperCase();
        
        Criptomoneda cripto = criptomonedas.get(simbolo);
        Mercado mercado = mercados.get(simbolo);

        if (cripto == null || mercado == null) {
            System.out.println("Criptomoneda no encontrada.");
            return;
        }

        Map<String, Double> historicoCompras = trader.getHistoricoCompras();
        Double cantidadMaxima = historicoCompras.get(simbolo);

        if (cantidadMaxima == null || cantidadMaxima <= 0) {
            System.out.println("No tiene suficientes criptomonedas para vender.");
            return;
        }

        System.out.println("Cantidad máxima que puede vender: " + cantidadMaxima);
        System.out.print("Ingrese la cantidad a vender: ");
        double cantidad = scanner.nextDouble();
        scanner.nextLine(); // Consumir el salto de línea

        if (cantidad > cantidadMaxima) {
            System.out.println("Error: La cantidad a vender excede la cantidad disponible.");
            return;
        }

        double precioVenta = cantidad * cripto.getPrecio();
        System.out.println("Valor en dólares de la venta: $" + precioVenta);

        System.out.print("¿Confirma la venta? (s/n): ");
        String confirmacion = scanner.nextLine();

        if (confirmacion.equalsIgnoreCase("s")) {
            mercado.setCapacidad(mercado.getCapacidad() + cantidad);
            mercado.setVolumen24h(mercado.getVolumen24h() * 0.93);
            mercado.setVariacion7d(mercado.getVariacion7d() * 0.93);

            trader.setSaldo(trader.getSaldo() + precioVenta);

            // Actualizar el histórico de compras del usuario
            historicoCompras.put(simbolo, cantidadMaxima - cantidad);

            System.out.println("Venta realizada con éxito.");
        } else {
            System.out.println("Venta cancelada.");
        }
    }
    
    private void recomendarCompra() {
        Criptomoneda criptoMayorValor = null;
        double mayorValor = 0;

        // Identificar la criptomoneda de mayor cotización
        for (Criptomoneda cripto : criptomonedas.values()) {
            if (cripto.getPrecio() > mayorValor) {
                mayorValor = cripto.getPrecio();
                criptoMayorValor = cripto;
            }
        }

        if (criptoMayorValor == null) {
            System.out.println("No hay criptomonedas disponibles para realizar la recomendación.");
            return;
        }

        double mayorPorcentaje = 0;
        Criptomoneda criptoRecomendada = null;

        // Calcular el porcentaje para cada criptomoneda y encontrar la de mayor porcentaje
        for (Map.Entry<String, Mercado> entry : mercados.entrySet()) {
            String simbolo = entry.getKey();
            Mercado mercado = entry.getValue();
            Criptomoneda cripto = criptomonedas.get(simbolo);

            if (cripto != null) {
                double porcentaje = (mercado.getCapacidad() / cripto.getPrecio()) * 100;
                if (porcentaje > mayorPorcentaje) {
                    mayorPorcentaje = porcentaje;
                    criptoRecomendada = cripto;
                }
            }
        }

        if (criptoRecomendada == null) {
            System.out.println("No se pudo realizar la recomendación.");
        } else {
            System.out.println("Se recomienda comprar: " + criptoRecomendada.getNombre() +
                               " (" + criptoRecomendada.getSimbolo() + ")");
            System.out.println("Porcentaje calculado: " + mayorPorcentaje + "%");
        }
    }




    public static void main(String[] args) {
        SistemaCriptomonedas sistema = new SistemaCriptomonedas();
        sistema.iniciarSesion();
    }
}
