package pacemaker;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import Petri.PetriNet;
import Petri.PetriNetFactory;
import Petri.TimedPetriNet;
import Petri.PetriNetFactory.petriNetType;
import monitor_petri.FirstInLinePolicy;
import monitor_petri.MonitorManager;
import monitor_petri.TransitionsPolicy;
import pacemaker.Procesador.Estado;

public class PacemakerTestSuite {
	MonitorManager monitor;
	PetriNet petri;
	TimedPetriNet timedPetriNet;
	static TransitionsPolicy policy;
	static PetriNetFactory factory;
	
	private static final String PACEMAKER_PETRI = "pacemakerPetriNet/PacemakerPetriNet.pnml";
	private static final String PACEMAKER_PETRI2 = "pacemakerPetriNet/PacemakerPetriNet2.pnml";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		policy = new FirstInLinePolicy();
	}
	
	/**
	 * Creates factory, petri and monitor from given PNML, and initialized the petri net
	 * @param PNML Path to the PNML file
	 * @param type The petri type to create
	 */
	private void setUpMonitor(String PNML, petriNetType type){
		factory = new PetriNetFactory(PNML);
		petri = factory.makePetriNet(type);
		monitor = new MonitorManager(petri, policy);
		petri.initializePetriNet();
	}
	
	@Test
	public void pruebaSensores(){
		setUpMonitor(PACEMAKER_PETRI, petriNetType.TIMED);
		
		Assert.assertEquals(1, petri.getPlaces()[20].getMarking()); //20
		
		Assert.assertEquals(0, petri.getPlaces()[14].getMarking());//14
		Assert.assertEquals(0, petri.getPlaces()[11].getMarking());//11
		Assert.assertEquals(0, petri.getPlaces()[15].getMarking());//15
		Assert.assertEquals(0, petri.getPlaces()[13].getMarking()); //13
		//worker0 thread fires "Leer sensores" 2
		Thread worker0 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Leer_Sensores");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		petri.initializePetriNet();
		
		worker0.start();
		
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(1, petri.getPlaces()[14].getMarking());//14
		Assert.assertEquals(1, petri.getPlaces()[11].getMarking());//11
		Assert.assertEquals(1, petri.getPlaces()[15].getMarking());//15
		Assert.assertEquals(1, petri.getPlaces()[13].getMarking()); //13
				
	}
	
	@Test
	public void pruebaCalculoCorrectoProcesadorCuandoNoNecesitaEstimulacion(){
		setUpMonitor(PACEMAKER_PETRI2, petriNetType.TIMED);
		Logger logger = new Logger(petri);
		Procesador procesador = new Procesador(logger);
		
		Assert.assertEquals(Estado.STOP, procesador.getEstado());
		Assert.assertEquals(1, petri.getPlaces()[1].getMarking()); //Plaza "Calculando"
		Assert.assertEquals(0, petri.getPlaces()[22].getMarking()); //Plaza "Telemetria"
		
		Thread worker0 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Necesita_Estimulacion");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread worker1 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("No_Necesita_Estimulacion");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread worker2 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Reportar_Err_Calculo");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		petri.initializePetriNet();
		
		int[] valores = {1,2,3,4};
		procesador.calcular(valores);
		Assert.assertEquals(Estado.NO_NECESITA_ESTIMULACION, procesador.getEstado());
		//ACA PONGO LA COMPROBACION Y DEPENDE EL ESTADO TIRO DIFERENTES HILOS

		Estado estado = procesador.getEstado();
		switch(estado){
		case NECESITA_ESTIMULACION:
			worker0.start();
			break;
		case NO_NECESITA_ESTIMULACION:	
			worker1.start();
			break;
		case ERROR_CALCULO:
			worker2.start();
			break;
		case STOP:
			break;
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(0, petri.getPlaces()[1].getMarking()); //Plaza "Calculando"
		Assert.assertEquals(1, petri.getPlaces()[22].getMarking()); //Plaza "Telemetria"
		
	}
	
	@Test
	public void pruebaCalculoCorrectoProcesadorCuandoNecesitaEstimulacion(){
		setUpMonitor(PACEMAKER_PETRI2, petriNetType.TIMED);
		Logger logger = new Logger(petri);
		Procesador procesador = new Procesador(logger);
		
		Assert.assertEquals(Estado.STOP, procesador.getEstado());
		Assert.assertEquals(1, petri.getPlaces()[1].getMarking()); //Plaza "Calculando"
		Assert.assertEquals(0, petri.getPlaces()[16].getMarking()); //Plaza "Lectura Shock"
		Assert.assertEquals(0, petri.getPlaces()[22].getMarking()); //Plaza "Telemetria"
		
		Thread worker0 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Necesita_Estimulacion");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread worker1 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("No_Necesita_Estimulacion");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread worker2 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Reportar_Err_Calculo");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		petri.initializePetriNet();
		
		int[] valores = {1000,2,3,4};
		procesador.calcular(valores);
		Assert.assertEquals(Estado.NECESITA_ESTIMULACION, procesador.getEstado());
		//ACA PONGO LA COMPROBACION Y DEPENDE EL ESTADO TIRO DIFERENTES HILOS

		Estado estado = procesador.getEstado();
		switch(estado){
		case NECESITA_ESTIMULACION:
			worker0.start();
			break;
		case NO_NECESITA_ESTIMULACION:
			worker1.start();
			break;
		case ERROR_CALCULO:
			worker2.start();
			break;
		case STOP:
			break;
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(0, petri.getPlaces()[1].getMarking()); //Plaza "Calculando"
		Assert.assertEquals(1, petri.getPlaces()[16].getMarking()); //Plaza "Lectura Shock"
		Assert.assertEquals(1, petri.getPlaces()[22].getMarking()); //Plaza "Telemetria"
		
	}
	
	@Test
	public void pruebaCalculoIncorrecto(){
		setUpMonitor(PACEMAKER_PETRI2, petriNetType.TIMED);
		Logger logger = new Logger(petri);
		Procesador procesador = new Procesador(logger);
		
		Assert.assertEquals(Estado.STOP, procesador.getEstado());
		Assert.assertEquals(1, petri.getPlaces()[1].getMarking()); //Plaza "Calculando"
		Assert.assertEquals(0, petri.getPlaces()[4].getMarking()); //Plaza "Error_Calculo"
		
		Thread worker0 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Necesita_Estimulacion");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread worker1 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("No_Necesita_Estimulacion");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread worker2 = new Thread(() -> {
			while(true){
				try {
					monitor.fireTransition("Reportar_Err_Calculo");					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		petri.initializePetriNet();
		
		int[] valores = {-100,2,3,4};
		procesador.calcular(valores);
		Assert.assertEquals(Estado.ERROR_CALCULO, procesador.getEstado());
		//ACA PONGO LA COMPROBACION Y DEPENDE EL ESTADO TIRO DIFERENTES HILOS

		Estado estado = procesador.getEstado();
		switch(estado){
		case NECESITA_ESTIMULACION:
			worker0.start();
			break;
		case NO_NECESITA_ESTIMULACION:
			worker1.start();
			break;
		case ERROR_CALCULO:
			worker2.start();
			break;
		case STOP:
			break;
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertEquals(0, petri.getPlaces()[1].getMarking()); //Plaza "Calculando"
		Assert.assertEquals(1, petri.getPlaces()[4].getMarking()); //Plaza "Error_Calculo"
		
	}
	
	@Test
	public void pruebaLogueoPulsoCorrecto(){
		Logger logger = new Logger(petri);
		Procesador procesador = new Procesador(logger);
		Assert.assertEquals(Estado.STOP, procesador.getEstado());
		
		int[] valores = {1000,2,3,4};
		procesador.calcular(valores);
		logger.close();
		Assert.assertEquals(Estado.NECESITA_ESTIMULACION, procesador.getEstado());
		
	}
	
	@Test
	public void pruebaLogueoEstado(){
		setUpMonitor(PACEMAKER_PETRI2, petriNetType.TIMED);
		Logger logger = new Logger(petri);
		
		petri.initializePetriNet();
		
		logger.loggearEstadoPetri();
		logger.close();
		
	}
}
