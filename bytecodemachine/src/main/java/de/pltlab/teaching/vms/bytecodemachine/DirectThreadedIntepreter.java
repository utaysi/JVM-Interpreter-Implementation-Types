package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;

public class DirectThreadedIntepreter extends Interpreter {
    private Runnable[] preDecodedInstructions; // Instance variable
    private int[] localVariables;              // Instance variable
    private int[] stack;                       // Instance variable
    private int sp;                            // Instance variable
	int pc;
	public DirectThreadedIntepreter(File file) throws IOException {
		super(file);
        // Initialize the local variables and operand stack
        localVariables = new int[3];
        stack = new int[100];
        sp = -1; // Initialize the stack pointer
		}

	@Override
	protected void interpret() {
		byte[] bytecode = getBytes();
		preDecodedInstructions = new Runnable[bytecode.length];

		for (int i = 0; i < bytecode.length; i++) {
			int opcode = ((int) getBytes()[pc]) & 0xff; // Ensure it's treated as unsigned
			extraction();

			switch (opcode) {
				case 0x03: // iconst_0
					preDecodedInstructions[i] = this::interp_iconst_0;
					directJump();
					break;
				case 0x09: // lconst_0
					preDecodedInstructions[i] = this::interp_lconst_0;
					directJump();
					break;
				case 0x04: // iconst_1
					preDecodedInstructions[i] = this::interp_iconst_1;
					directJump();
					break;
				case 0x10: // bipush
					preDecodedInstructions[i] = this::interp_bipush;
					i++; // Skip the next byte as it's part of the bipush instruction
					directJump();
					break;
				case 0x1a: // iload_0
					preDecodedInstructions[i] = this::interp_iload_0;
					directJump();
					break;
				case 0x1b: // iload_1
					preDecodedInstructions[i] = this::interp_iload_1;
					directJump();
					break;
				case 0x1c: // iload_2
					preDecodedInstructions[i] = this::interp_iload_2;
					directJump();
					break;
				case 0x3b: // istore_0
					preDecodedInstructions[i] = this::interp_istore_0;
					directJump();
					break;
				case 0x3c: // istore_1
					preDecodedInstructions[i] = this::interp_istore_1;
					directJump();
					break;
				case 0x3d: // istore_2
					preDecodedInstructions[i] = this::interp_istore_2;
					directJump();
					break;
				case 0x60: // iadd
					// You need to implement interp_iadd method for this
					preDecodedInstructions[i] = this::interp_iadd;
					directJump();
					break;
				case 0x68: // imul
					preDecodedInstructions[i] = this::interp_imul;
					directJump();
					break;
				case 0x84: // iinc
					preDecodedInstructions[i] = this::interp_iinc;
					i += 2; // Skip the next two bytes (index and increment)
					directJump();
					break;
				case 0x9f: // if_icmpeq
					preDecodedInstructions[i] = this::interp_IF_ICMPEQ;
					directJump();
					break;
				case 0xa3: // if_icmpgt
					preDecodedInstructions[i] = this::interp_if_icmpgt;
					directJump();
					break;
				case 0xa4: // if_icmple
					preDecodedInstructions[i] = this::interp_if_icmple;
					directJump();
					break;
				case 0xa7: // goto
					preDecodedInstructions[i] = this::interp_goto;
					directJump();
					break;
				case 0xb1: // return
					preDecodedInstructions[i] = this::interp_return;
					directJump();
					break;
				case 0x64: // isub
					preDecodedInstructions[i] = this::interp_ISUB;
					directJump();
					break;
				default:
					preDecodedInstructions[i] = () -> {
						throw new UnsupportedOperationException("Unsupported opcode: 0x" + Integer.toHexString(opcode));
					};
					indirectJump();
//					System.out.println("Unknown opcode: " + opcode);
					break;
			}
			//System.out.println("Unknown opcode: " + opcode);
		}
		//pc = 0;
		// Implementation of the interpret method
		// Iterate over preDecodedInstructions and execute each one
		//extraction();
		for (Runnable instruction : preDecodedInstructions) {

//			if (instruction != null) {
//				instruction.run();
//			}
//			else {
//				throw new IllegalStateException("Null instruction encountered at pc: " + pc);
//			}
		}
	}

	// Implementation of opcode handling methods
	// For example:
	private void interp_return() {
		halt();
		pc += 1;
		extraction();

	}

	private void interp_istore_0() {
		int value = pop();
		localVariables[0] = value;
		pc += 1;
		extraction();

	}

	private void interp_bipush() {
		byte value = (byte) getBytes()[pc + 1];
		push((int) value);
		pc += 2;
		extraction();

	}

	private void interp_iconst_0() {
		push(0);
		pc += 1;
		extraction();

	}
	private void interp_lconst_0() {
		// Pushing the long constant 0 (as two int values)
		push(0); // Lower bits
		push(0); // Upper bits
		pc += 1;
		extraction();

	}

	private void interp_iconst_1() {
		push(1);
		pc += 1;
		extraction();

		// No further instruction lookup
	}

	private void interp_istore_1() {
		int value = pop();
		localVariables[1] = value;
		pc += 1;
		extraction();
		// No further instruction lookup
	}

	private void interp_istore_2() {
		int value = pop();
		localVariables[2] = value;
		pc += 1;
		extraction();
		// No further instruction lookup
	}

	private void interp_goto() {
		int branchOffset = (int) getBytes()[pc + 1] << 8 | (int) getBytes()[pc + 2];
		pc += branchOffset;
		extraction();
		// No further instruction lookup
	}

	private void interp_iload_1() {
		int value = (int) getVariableValue(1);
		push(value);
		pc += 1;
		extraction();
		// No further instruction lookup
	}

	private void interp_iload_2() {
		int value = (int) getVariableValue(2);
		push(value);
		pc += 1;
		extraction();
		// No further instruction lookup
	}

	private void interp_imul() {
		int value2 = pop();
		int value1 = pop();
		int result = value1 * value2;
		push(result);
		pc += 1;
		extraction();
		// No further instruction lookup
	}

	private void interp_iinc() {
		int index = (int) getBytes()[pc + 1];
		int constant = (int) getBytes()[pc + 2];
		localVariables[index] += constant;
		pc += 3;
		extraction();
		directJump();
		// No further instruction lookup
	}

	private void interp_iload_0() {
		int value = (int) getVariableValue(0);
		push(value);
		pc += 1;
		extraction();
		// No further instruction lookup
	}

	private void interp_if_icmple() {
		int branchOffset = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if (value1 <= value2) {
			pc += branchOffset;
		} else {
			pc += 3;
		}
		extraction();
	}

	private void interp_if_icmpgt() {
		int branchOffset = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if (value1 > value2) {
			pc += branchOffset;
		} else {
			pc += 3;
		}
		extraction();
	}

	private void interp_IF_ICMPEQ() {
		int branchOffset = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if (value1 == value2) {
			pc += branchOffset;
		} else {
			pc += 3;
		}
		extraction();
	}

	private void interp_ISUB() {
		int value2 = pop();
		int value1 = pop();
		int result = value1 - value2;
		push(result);
		pc += 1;
		extraction();
	}

	private void interp_iadd() {
		int value1 = pop();
		int value2 = pop();
		push(value1 + value2);
		pc += 1;
		extraction();
	}

	private void push(int value) {
		stack[++sp] = value;
	}

	private int pop() {
		return stack[sp--];
	}

	@Override
	protected Object getVariableValue(int index) {

		if (index >= 0 && index < localVariables.length) {
			return localVariables[index];
		} else {
			return null;
		}

	}
}
