package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;

/**
 * Incomplete demo implementation of an interpreter following the decode-dispatch-loop
 * strategy.
 */
public class DecodeDispatchIntepreter extends Interpreter {

	int pc;
	
	public DecodeDispatchIntepreter(File file) throws IOException {
		super(file);
	}

	@Override
	protected void interpret() {
		
		pc = 0;
		
		while(!halted()) {
			directJump();
			
			int opcode = ((int) getBytes()[pc]) & 0xff;
			extraction();
			
			switch (opcode) {
			case 0x03 -> { 
				indirectJump(); 
				interp_iconst_0(); 
			}
			case 0x3b -> {
				indirectJump();
				interp_istore_0();
			}
			case 0xb1 -> {
				indirectJump();
				interp_return();
			}
			// TODO add further cases
			default -> throw new IllegalArgumentException("Unsupported opcode: " + opcode);
			}
			directJump();
		}
	}

	private void interp_return() {
		halt();
		pc += 1;
		indirectJump();
	}

	private void interp_istore_0() {
		// TODO interpret istore_0
		pc += 1;
		indirectJump();
	}

	private void interp_iconst_0() {
		// TODO interpret iconst_0
		pc += 1;
		indirectJump();
	}
	
	// TODO add more routines

	@Override
	protected Object getVariableValue(int index) {
		// TODO implement properly
		return null;
	}

}
