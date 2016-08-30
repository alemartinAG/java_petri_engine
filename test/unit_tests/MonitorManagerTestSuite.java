package unit_tests;


import java.util.ArrayList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Petri.PetriNet;
import Petri.PetriNet.PetriNetBuilder;
import Petri.Transition;
import monitor_petri.FirstInLinePolicy;
import monitor_petri.MonitorManager;
import monitor_petri.TransitionsPolicy;
import test_utils.TransitionEventObserver;

public class MonitorManagerTestSuite {
	
	MonitorManager monitor;
	PetriNet petri;
	static TransitionsPolicy policy;
	static PetriNetBuilder builder;
	
	private static final String MonitorTest01Petri = "test/unit_tests/testResources/monitorTest01.pnml";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		builder = new PetriNetBuilder(MonitorTest01Petri);
		policy = new FirstInLinePolicy();
	}

	@Before
	public void setUp() throws Exception {
		petri = builder.buildPetriNet();
		monitor = new MonitorManager(petri, policy);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * <li>Given no thread is sleeping for any transition</li>
	 * <li>And I know that the initial marking is {1, 0, 0, 0}</li>
	 * <li>And I know only t0 is enabled</li>
	 * <li>When I fire t0</li>
	 * <li>Then t1 and t2 are enabled (not testable from this scope)</li>
	 * <li>And t1 is fired for being automatic</li>
	 * <li>And the final marking is {0, 0, 1, 1}</li>
	 * <li>And t2 is enabled</li>
	 */
	@Test
	public void testFireTransitionWhenNoThreadIsSleeping() {
		Integer[] expectedInitialMarking = {1, 0, 0, 0};
		Assert.assertArrayEquals(expectedInitialMarking , this.petri.getCurrentMarking());
		
		Transition t0 = petri.getTransitions()[0];
		Transition t1 = petri.getTransitions()[1];
		
		TransitionEventObserver obs = new TransitionEventObserver();
		monitor.subscribeToTransition(t1, obs);
		
		monitor.fireTransition(t0);
		
		// this means that t1 emmited an event when it was fired
		Assert.assertTrue(obs.getEvents().get(0).endsWith(t1.getId()));
		
		Integer[] expectedMarkingAfterT0 = {0, 0, 1, 1};
		Assert.assertArrayEquals(expectedMarkingAfterT0 , this.petri.getCurrentMarking());
		
		boolean[] expectedEnabled = {false, false, true};
		Assert.assertArrayEquals(expectedEnabled, petri.getEnabledTransitions());
	}
	
	/**
	 * <li>Given no thread is sleeping for any transition</li>
	 * <li>And I know that the initial marking is {1, 0, 0, 0}</li>
	 * <li>And I know only t0 is enabled</li>
	 * <li>When I try to fire t2 using a worker thread</li>
	 * <li>And I wait for t2 to go to sleep</li>
	 * <li>And I fire t0</li>
	 * <li>Then t1 and t2 are enabled (not testable from this scope)</li>
	 * <li>And t1 is fired for being automatic</li>
	 * <li>And t2 is fired because a worker thread was waiting for it to enable</li>
	 * <li>And the final marking is {0, 0, 0, 2}</li>
	 * <li>And no transition is enabled</li>
	 */
	@Test
	public void testFireTransitionWhenAThreadIsSleepingInT2() {
		Integer[] expectedInitialMarking = {1, 0, 0, 0};
		Assert.assertArrayEquals(expectedInitialMarking , this.petri.getCurrentMarking());
		
		final Transition t0 = petri.getTransitions()[0];
		final Transition t1 = petri.getTransitions()[1];
		final Transition t2 = petri.getTransitions()[2];
		
		Thread worker = new Thread(() -> {
			monitor.fireTransition(t2);
		});
		worker.start();
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Assert.assertArrayEquals(expectedInitialMarking , this.petri.getCurrentMarking());
		
		TransitionEventObserver obs = new TransitionEventObserver();
		monitor.subscribeToTransition(t1, obs);
		monitor.subscribeToTransition(t2, obs);
		
		monitor.fireTransition(t0);
		
		ArrayList<String> events = obs.getEvents();
		
		Assert.assertEquals(1, events.size());
		Assert.assertTrue(events.get(0).endsWith(t1.getId()));
		
		try {
			boolean finishedWaiting = false;
			int retries = 10;
			while(!finishedWaiting && retries > 0){
				for( String str : obs.getEvents() ){
					finishedWaiting = str.endsWith(t2.getId());
				}
				retries--;
				Thread.sleep(10);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		events = obs.getEvents();
		
		Assert.assertEquals(2, events.size());
		Assert.assertTrue(events.get(1).endsWith(t2.getId()));
		
		Integer[] expectedMarkingAfterT0 = {0, 0, 0, 2};
		Assert.assertArrayEquals(expectedMarkingAfterT0 , this.petri.getCurrentMarking());
		
		boolean[] expectedEnabled = {false, false, false};
		Assert.assertArrayEquals(expectedEnabled, petri.getEnabledTransitions());
	}
	
	@Test
	public void testFireTransitionShouldThrowErrorWhenFiringAnAutomaticTransition() {
		try{
			Transition t1 = petri.getTransitions()[1];
			monitor.fireTransition(t1);
			Assert.fail("An IllegalTransitionFiringError should've been thrown before this point");
		} catch (Error err){
			Assert.assertEquals("IllegalTransitionFiringError", err.getClass().getSimpleName());
		}
	}
	
	/**
	 * Given I know t1 is automatic and informed
	 * And obs is an observer subscript to t1 events
	 * When I fire t0
	 * Then t0 is fired
	 * And t1 is enabled
	 * And t1 is fired
	 * And an obs recieves an event with t1's ID
	 */
	@Test
	public void FireInformedTransitionShouldSendAnEvent(){
		
		TransitionEventObserver obs = new TransitionEventObserver();
		
		Transition t0 = petri.getTransitions()[0];
		Transition t1 = petri.getTransitions()[1];
		
		monitor.subscribeToTransition(t1, obs);
		
		monitor.fireTransition(t0);
		
		ArrayList<String> events = obs.getEvents();
		
		Assert.assertEquals(1, events.size());
		Assert.assertTrue(events.get(0).endsWith(t1.getId()));
		
	}
	
	/**
	 * Given I know t0 is not informed
	 * When I try to subscribe obs to t0
	 * Then an IllegalArgumentException is thrown
	 */
	@Test
	public void SubscribeToNotInformedTransitionShouldThrowException(){
		try{
			TransitionEventObserver obs = new TransitionEventObserver();
			
			Transition t0 = petri.getTransitions()[0];
			
			monitor.subscribeToTransition(t0, obs);
			
			Assert.fail("An exception should've been thrown before this point");	
		} catch (Exception e){
			Assert.assertEquals("IllegalArgumentException", e.getClass().getSimpleName());
		}
	}
	
	
	/**
	 * Given I have a policy object but no petri
	 * When I try to create a monitor without petri
	 * Then an IllegalArgumentException is thrown 
	 */
	@Test
	public void CreatingMonitorWithoutPetriShouldThrowException(){
		try{
			MonitorManager aMonitor = new MonitorManager(null, policy);
			Assert.fail("An exception should've been thrown before this point");
		} catch (Exception e){
			Assert.assertEquals("IllegalArgumentException", e.getClass().getSimpleName());
		}
	}
	
	/**
	 * Given I have a petri object but no policy
	 * When I try to create a monitor without policy
	 * Then an IllegalArgumentException is thrown 
	 */
	@Test
	public void CreatingMonitorWithoutPolicyShouldThrowException(){
		try{
			MonitorManager aMonitor = new MonitorManager(petri, null);
			Assert.fail("An exception should've been thrown before this point");
		} catch (Exception e){
			Assert.assertEquals("IllegalArgumentException", e.getClass().getSimpleName());
		}
	}
	
	

}
