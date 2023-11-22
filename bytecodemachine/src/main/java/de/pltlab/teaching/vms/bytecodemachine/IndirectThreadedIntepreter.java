package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IndirectThreadedIntepreter extends Interpreter {

	int pc;

	private int[] stack = new int[9];
	private int localVariables[] = new int[3];

	private int sp;
	
	Map<Integer, RoutinePointer> lookupTable = new HashMap<>();
	
	public IndirectThreadedIntepreter(File file) throws IOException {
		super(file);
		this.sp= -1;
		lookupTable.put(0x03, this::interp_iconst_0);
		lookupTable.put(0x3b, this::interp_istore_0);
		lookupTable.put(0xb1, this::interp_return);
		lookupTable.put(0x10, this::interp_bipush);
		lookupTable.put(0x04, this::interp_iconst_1);
		lookupTable.put(0x3c, this::interp_istore_1);
		lookupTable.put(0x3d, this::interp_istore_2);
		lookupTable.put(0xa7, this::interp_goto);
		lookupTable.put(0x1b, this::interp_iload_1);
		lookupTable.put(0x1c, this::interp_iload_2);
		lookupTable.put(0x68, this::interp_imul);
		lookupTable.put(0x84, this::interp_iinc);
		lookupTable.put(0x1a, this::interp_iload_0);
		lookupTable.put(0xa4, this::interp_if_icmple);
		lookupTable.put(0xa3, this::interp_if_icmpgt);
		lookupTable.put(0x9f, this::interp_IF_ICMPEQ);
		lookupTable.put(0x64, this::interp_ISUB);




		
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
		int value = pop();
		localVariables[0] = value;
		pc += 1;
		
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_bipush() {
		// TODO interpret bipush
		byte value = (byte) getBytes()[pc + 1];
		push((int) value);
		pc += 2;
		
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();
		
		indirectJump();
		bridgeTo(lookup(opcode));
	}

	// TODO additional routines
	private void interp_iconst_0() {
		// TODO interpret iconst_0

		push(0);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_iconst_1() {
		// TODO interpret interp_iconst_1
		push(1);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_istore_1() {
		// TODO interpret interp_istore_1
		int value = pop();
		localVariables[1] = value;
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_istore_2() {
		// TODO interpret interp_istore_2
		int value = pop();
		localVariables[2] = value;
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_goto() {
		// TODO interpret interp_goto
		int branchOffset = (int) getBytes()[pc + 1] << 8 | (int) getBytes()[pc + 2];
		pc += branchOffset; // Update the program counter based on the branch offset
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();
		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_iload_1() {
		// TODO interpret interp_iload_1
		int value = (int) getVariableValue(1);
		push(value);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_iload_2() {
		// TODO interpret interp_iload_2
		int value = (int) getVariableValue(2);
		push(value);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_imul() {
		// TODO interpret interp_imul

		int value2 = pop();
		int value1 = pop();
		int result = value1 * value2;
		push(result);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_iinc() {
		// TODO interpret interp_iinc
		int index = (int) getBytes()[pc + 1];
		int constant = (int) getBytes()[pc + 2];
		localVariables[index] += constant;
		pc += 3;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_iload_0() {
		// TODO interpret interp_iload_0
		int value = (int) getVariableValue(0);
		push(value);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}
	private void interp_if_icmple() {
		// TODO interpret interp_if_icmple
		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if(value1 <= value2){
			pc += branchOffSet;
		}else {
			pc += 3;
		}

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_if_icmpgt() {
		// TODO interpret interp_if_icmpgt

		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if(value1 > value2){
			pc += branchOffSet;
		}else {
			pc += 3;
		}
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}
	private void interp_IF_ICMPEQ() {
		// TODO interpret interp_IF_ICMPEQ
		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if(value1 == value2){
			pc += branchOffSet;
		}else {
			pc += 3;
		}
		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}

	private void interp_ISUB() {
		// TODO interpret interp_ISUB
		int value2 = pop();
		int value1 = pop();
		int result = value1 - value2;
		push(result);
		pc += 1;

		int opcode = ((int) getBytes()[pc]) & 0xff;
		extraction();

		indirectJump();
		bridgeTo(lookup(opcode));
	}
	private void push(int value) {
		stack[++sp] = value;
	}

	private int pop() {
		return stack[sp--];
	}
	@Override
	protected Object getVariableValue(int index) {
		// TODO implement properly
		if (index >= 0 && index < localVariables.length) {
			return localVariables[index];
		} else {
			// Handle index out of bounds or other error cases
			return null;
		}
	}

}
