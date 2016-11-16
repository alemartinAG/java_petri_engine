package pacemaker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import Petri.PetriNet;

public class Logger {
	
	public enum codigoLogueo{
		PULSO_CORRECTO,
		PULSO_INCORRECTO,
		NECESITA,
		NO_NECESITA,
		ERROR_CALCULO
	}
	
	private PetriNet petri;
	private FileWriter archivo;
	private BufferedWriter printer;
	private int contador;
	
	public Logger(PetriNet _petri){
		contador = 0;
		petri = _petri;
		try {
			archivo = new FileWriter("logger/logueo.txt", true);
			printer = new BufferedWriter(archivo);
			
			printer.write("estados = []\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void loggearEstadoPetri(){
		// El logueo se realiza en formato de una lista de python,
		// con el fin de poder comparar los estados facilmente con un script
		Integer[] aux = new Integer[petri.getCurrentMarking().length];
		aux = petri.getCurrentMarking();
		
		try {
			printer.write("estados.append([");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int j=0; j<aux.length; j++){
			try {
				printer.write(String.valueOf(aux[j]));
				if(j != aux.length-1){
					printer.write(",");
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			printer.write("])\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		aumentarContador();
	}
	
	public void loggearEstadoCalculo(codigoLogueo codigo, long time){
		switch(codigo){
		case PULSO_CORRECTO:
			try {
				printer.write(time + ": Pulso calculado correctamente\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
		case PULSO_INCORRECTO:
			try {
				printer.write(time + ": Pulso calculado incorrectamente\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
	public void loggearEstadoCorazon(codigoLogueo codigo, long time){
		switch(codigo){
		case NECESITA:
			try {
				printer.write(time + ": Necesita estimulacion\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case NO_NECESITA:
			try {
				printer.write(time + ": No neceista estimulacion\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
	public void loggearError(codigoLogueo codigo, long time){
		switch(codigo){
		case ERROR_CALCULO:
			try {
				printer.write(time + ": Error en calculo del algoritmo\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	public void close(){
		try {
			printer.close();
			archivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void flush(){
		try {
			printer.flush();
			archivo.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getContador() {
		return contador;
	}

	private synchronized void aumentarContador() {
		this.contador += 1;
	}
}
