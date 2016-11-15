package pacemaker;

import pacemaker.Logger.codigoLogueo;

public class Procesador {
	
	public enum Estado {
		STOP,
		NECESITA_ESTIMULACION,
		NO_NECESITA_ESTIMULACION,
		ERROR_CALCULO
		}
	
	private Logger logger;
	private Estado estado;
	
	public Procesador(Logger _logger){
		estado = Estado.STOP;
		logger = _logger;
	}
	
	public void calcular(int[] valores){
		long time = System.currentTimeMillis();
		int resultado = 0;
		for(int valor : valores){
			resultado += valor;
		}
		if(resultado < 0){
			estado = Estado.ERROR_CALCULO;
			logger.loggearError(codigoLogueo.ERROR_CALCULO, time);
		}
		else if (resultado > 1000){
			estado = Estado.NECESITA_ESTIMULACION;
			logger.loggearEstadoCalculo(codigoLogueo.PULSO_CORRECTO, time);
			logger.loggearEstadoCorazon(codigoLogueo.NECESITA, time);
		}
		else{
			estado = Estado.NO_NECESITA_ESTIMULACION;
			logger.loggearEstadoCalculo(codigoLogueo.PULSO_CORRECTO, time);
			logger.loggearEstadoCorazon(codigoLogueo.NO_NECESITA, time);
		}
	}
	public Estado getEstado(){
		return this.estado;
	}
}

