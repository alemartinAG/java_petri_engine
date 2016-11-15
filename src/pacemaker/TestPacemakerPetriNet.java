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
	public static int w0=0, w1=0, w2=0, w3=0, w4=0;
	static boolean flag = true;
	static long timeDesde = 0;
	static long timeHasta = 0;
	
	public static void main(String[] args) {
		factory = new PetriNetFactory(PNML);
		petri = (TimedPetriNet) factory.makePetriNet(petriNetType.TIMED);
		logger = new Logger(petri);
		policy = new FirstInLinePolicy();
		monitor = new MonitorManager(petri, policy);
		
		//worker0 thread fires "Leer sensores" 2
		Thread worker0 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Leer_Sensores");
					
					timeDesde = System.currentTimeMillis();
					logger.loggearEstadoPetri();
					timeHasta = System.currentTimeMillis();
					
					System.out.println(timeHasta - timeDesde);
				} catch (Exception e) {
					e.printStackTrace();
				}
				w0++;
			}
		});
		//worker1 thread fires "Leer sensor bateria" 1
		Thread worker1 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Leer_Sen_Bat");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w1++;
			}
		});
		//worker2 thread fires "vaciar buffer" 23
		Thread worker2 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Vaciar_Buffer");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w2++;
			}
		});
		//worker3 thread fires "necesita estimulacion" 3
		Thread worker3 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Necesita_Estimulacion");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w3++;
			}
		});
		//worker4 thread fires "registrar shock" 15
		Thread worker4 = new Thread(() -> {
			while(flag){
				try {
					monitor.fireTransition("Registrar_Shock");
					logger.loggearEstadoPetri();
				} catch (Exception e) {
					e.printStackTrace();
				}
				w4++;
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
		
		petri.initializePetriNet();
		
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
		int timeToSleep = 10000;
		try {
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		flag = false;
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		logger.flush();
		//logger.close();
		
		System.out.println("Run time: " + timeToSleep + " milliseconds");
		System.out.println("Transition Leer_Sensores was fired " + w0 + " times");
		System.out.println("Transition Leer_Sen_Bateria was fired " + w1 + " times");
		System.out.println("Transition Vaciar_Buffer was fired " + w2 + " times");
		System.out.println("Transition Necesita_Estimulacion was fired " + w3 + " times");
		System.out.println("Transition Registrar Shock was fired " + w4 + " times");
		
		System.out.println(petri.getPlaces()[22].getMarking());
		
		System.out.println("END");
	}

}
