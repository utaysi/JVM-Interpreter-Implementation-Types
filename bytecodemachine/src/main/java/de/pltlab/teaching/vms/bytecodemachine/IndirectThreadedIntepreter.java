package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IndirectThreadedIntepreter extends Interpreter {

	int pc;
	
	Map<Integer, RoutinePointer> lookupTable = new HashMap<>();
	
	public IndirectThreadedIntepreter(File file) throws IOException {
		super(file);
		lookupTable.put(0x03, this::interp_iconst_0);
		lookupTable.put(0x3b, this::interp_istore_0);
		lookupTable.put(0xb1, this::interp_return);
		
		// TODO add additional routines to lookup table
	}

	private RoutinePointer lookup(int opcode) {
		RoutinePointer result = lookupTable.get(opcode);
		if (result == null)
			throw new IllegalArgumentException("Unsupported opcode: " + opcode);
		return result;
	}
	
	@Override
	protected void interpret() {
		pc = 0;
		
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();
		
		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_return() {
		halt();
		pc += 1;
		
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_istore_0() {
		// TODO interpret istore_0
		
		pc += 1;
		
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_iconst_0() {
		// TODO interpret iconst_0
		
		pc += 1;
		
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();
		
		indirectJump();
		bridgeTo(lookup(opcode));
	}

	// TODO additional routines

	@Override
	protected Object getVariableValue(int index) {
		// TODO implement properly
		return null;
	}

}
