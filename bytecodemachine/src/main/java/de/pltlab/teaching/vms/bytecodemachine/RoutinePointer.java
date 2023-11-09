package de.pltlab.teaching.vms.bytecodemachine;

/**
 * Simulates the pointer to a routine to which control can be transferred.
 * Control transfer is simulated by the method {@link Interpreter#bridgeTo(RoutinePointer)}. 
 */
@FunctionalInterface
public interface RoutinePointer {

	void run();
	
}
