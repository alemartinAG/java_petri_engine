package pacemaker;

import Petri.PetriNetFactory;
import Petri.PetriNetFactory.petriNetType;
import Petri.TimedPetriNet;
import Petri.Transition;
import monitor_petri.FirstInLinePolicy;
import monitor_petri.MonitorManager;

public class TestPacemakerPetriNet {

	public static MonitorManager monitor;
	public static TimedPetriNet petri;
	public static FirstInLinePolicy policy;
	public static PetriNetFactory factory;
	public static Logger logger;
	public static String PNML = "pacemakerPetriNet/PacemakerPetriNet.pnml";
	static boolean flag = true;
	
	public static void main(String[] args) {
		////////////////////////////////////////////////////////////////////////////////////////
		//Inicializacion de los objetos necesarios para la creacion de la Petri
		factory = new PetriNetFactory(PNML);
		petri = (TimedPetriNet) factory.makePetriNet(petriNetType.TIMED);
		logger = new Logger(petri);
		policy = new FirstInLinePolicy();
		monitor = new MonitorManager(petri, policy);
		
		////////////////////////////////////////////////////////////////////////////////////////
		//Creacion de los hilos, los cuales tienen la responsabilidad de:
		// - Disparar la transicion que se les explicita,
		// - Disparar las transiciones automaticas que quedaron sensibilizadas luego de su primer disparo
		// - Logear el estado de la red despues de los disparos efectuados
		
		Thread worker0 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Leer_Sensores");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Thread worker1 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Leer_Sen_Bat");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Thread worker2 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Vaciar_Buffer");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Thread worker3 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Necesita_Estimulacion");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Thread worker4 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Registrar_Shock");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker5 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Pul");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker6 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Acel");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker7 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Resp");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker8 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Met");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker9 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Bat");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Inicializacion de la red de petr
		petri.initializePetriNet();
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Inicializacion de los hilos creados anteriormente
		worker0.start();
		worker1.start();
		worker2.start();
		worker3.start();
		worker4.start();
		worker5.start();
		worker6.start();
		worker7.start();
		worker8.start();
		worker9.start();
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Aqui se duerme al hilo principal, con el fin de dejar a los demas hilos que ejecuten la red
		int timeToSleep = 5000;
		try {
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Esta bandera hace que los hilos terminen su ejecucion
		flag = false;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Se escribe el archivo con todos los logueos de los hilos
		logger.flush();
		
		System.out.println("END");
	}

}
