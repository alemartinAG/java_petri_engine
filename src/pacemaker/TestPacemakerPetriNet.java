package pacemaker;

import org.unc.lac.javapetriconcurrencymonitor.monitor.PetriMonitor;
import org.unc.lac.javapetriconcurrencymonitor.monitor.policies.FirstInLinePolicy;
import org.unc.lac.javapetriconcurrencymonitor.monitor.policies.RandomPolicy;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.TimedPetriNet;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.factory.PetriNetFactory;
import org.unc.lac.javapetriconcurrencymonitor.petrinets.factory.PetriNetFactory.petriNetType;

public class TestPacemakerPetriNet {

	public static PetriMonitor monitor;
	public static TimedPetriNet petri;
	public static FirstInLinePolicy policy;
	public static PetriNetFactory factory;
	public static Logger logger;
	public static String PNML = "pacemakerPetriNet/PacemakerPetriNet.pnml";
	static boolean flag = true; //bandera cortar la ejecucion de los hilos
	public static int w1 = 0;
	public static int w2 = 0;
	public static int w3 = 0;
	public static int w4 = 0;
	public static int w5 = 0;
	
	public static void main(String[] args) {
		////////////////////////////////////////////////////////////////////////////////////////
		//Inicializacion de los objetos necesarios para la creacion de la Petri
		factory = new PetriNetFactory(PNML);
		petri = (TimedPetriNet) factory.makePetriNet(petriNetType.TIMED);
		logger = new Logger(petri);
		policy = new FirstInLinePolicy();
		monitor = new PetriMonitor(petri, policy);
		
		////////////////////////////////////////////////////////////////////////////////////////
		//Creacion de los hilos, los cuales tienen la responsabilidad de:
		// - Disparar la transicion que se les explicita,
		// - Disparar las transiciones automaticas que quedaron sensibilizadas luego de su primer disparo
		// - Logear el estado de la red despues de los disparos efectuados
		
		Thread worker0 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Leer_Sensores");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w1++;
			}
		});
		Thread worker1 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Leer_Sen_Bat");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w2++;
			}
		});
		Thread worker2 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Vaciar_Buffer");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w3++;
			}
		});
		Thread worker3 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Necesita_Estimulacion");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w4++;
			}
		});
		Thread worker4 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Registrar_Shock");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w5++;
			}
		});		
		Thread worker5 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Pul");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker6 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Acel");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker7 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Resp");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker8 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Met");
					//logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
		Thread worker9 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Poner_En_Buffer_Bat");
					//logger.loggearEstadoPetri();
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
		//worker5.start();
		//worker6.start();
		//worker7.start();
		//worker8.start();
		//worker9.start();
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Aqui se duerme al hilo principal, con el fin de dejar a los demas hilos que ejecuten la red
		int timeToSleep = 6000;
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
		petri.flush();
		
		System.out.println("Leer sensores se disparo: " + w1 +" veces");
		System.out.println("Leer_Sen_Bat se disparo: " + w2 +" veces");
		System.out.println("Vaciar_Buffer se disparo: " + w3 +" veces");
		System.out.println("Necesita_Estimulacion se disparo: " + w4 +" veces");
		System.out.println("Registrar_Shock se disparo: " + w5 +" veces");
		
		System.out.println("Poner en buffer bat tiene en su cola: " + monitor.getQueuesState()[petri.getTransition("Poner_En_Buffer_Bat").getIndex()]);
		System.out.println("Leer sen bat tiene en su cola: " + monitor.getQueuesState()[petri.getTransition("Leer_Sen_Bat").getIndex()]);
		System.out.println("Poner en buffer bat2 tiene en su cola: " + monitor.getQueuesState()[petri.getTransition("Poner_En_Buffer_Bat2").getIndex()]);
		
		System.out.println("Sen bat disp tiene: "+ petri.getPlace("Sen_Bat_Disp").getMarking() +" tokens");
		System.out.println("Sen bat ocup tiene: "+ petri.getPlace("Sen_Bat_Ocup").getMarking() +" tokens");
		
		System.out.println("Buffer " + petri.getPlace("Buffer").getIndex());
		System.out.println("Sensores_Ocup " + petri.getPlace("Sensores_Ocup").getIndex());
		System.out.println("Sensores_Disp " + petri.getPlace("Sensores_Disp").getIndex());
		System.out.println("Sen_Bat_Ocup " + petri.getPlace("Sen_Bat_Ocup").getIndex());
		System.out.println("Sen_Bat_Disp " + petri.getPlace("Sen_Bat_Disp").getIndex());
		
		System.out.println("Usando_Buffer_Pul " + petri.getPlace("Usando_Buffer_Pul").getIndex());
		System.out.println("Usando_Buffer_Acel " + petri.getPlace("Usando_Buffer_Acel").getIndex());
		System.out.println("Usando_Buffer_Resp " + petri.getPlace("Usando_Buffer_Resp").getIndex());
		System.out.println("Usando_Buffer_Met " + petri.getPlace("Usando_Buffer_Met").getIndex());
		System.out.println("Usando_Buffer_Bat " + petri.getPlace("Usando_Buffer_Bat").getIndex());
		System.out.println("Exc_Buffer " + petri.getPlace("Exc_Buffer").getIndex());
		
		System.out.println("END");
	}

}
