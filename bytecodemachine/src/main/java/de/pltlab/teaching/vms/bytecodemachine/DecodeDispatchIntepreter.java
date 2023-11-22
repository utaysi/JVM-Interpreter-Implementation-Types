package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;

/**
 * Incomplete demo implementation of an interpreter following the decode-dispatch-loop
 * strategy.
 */
public class DecodeDispatchIntepreter extends Interpreter {

	int pc;
	private int[] stack = new int[9];
	private int localVariables[] = new int[3];
	private int sp;


	public DecodeDispatchIntepreter(File file) throws IOException {
		super(file);
		this.sp= -1;
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
				case 0x10 -> {
					indirectJump();
					interp_bipush();
				}
				case 0x04 -> {
					indirectJump();
					interp_iconst_1();
				}
				case 0x3c ->{
					indirectJump();
					interp_istore_1();
				}
				case 0x3d ->{
					indirectJump();
					interp_istore_2();
				}
				case 0xa7 ->{
					indirectJump();
					interp_goto();
				}
				case 0x1b ->{
					indirectJump();
					interp_iload_1();
				}
				case 0x1c ->{
					indirectJump();
					interp_iload_2();
				}
				case 0x68 ->{
					indirectJump();
					interp_imul();
				}
				case 0x84 ->{
					indirectJump();
					interp_iinc();
				}
				case 0x1a ->{
					indirectJump();
					interp_iload_0();
				}
				case 0xa4 ->{
					indirectJump();
					interp_if_icmple();
				}
				case 0xa3 ->{
					indirectJump();
					interp_if_icmpgt();
				}
				case 0x9f ->{
					indirectJump();
					interp_IF_ICMPEQ();
				}
				case 0x64->{
					indirectJump();
					interp_ISUB();
				}
				// TODO add further cases
				default -> throw new IllegalArgumentException("Unsupported opcode: " + opcode);
			}
			directJump();
		}
	}

	private void interp_ISUB(){
		int value2 = pop();
		int value1 = pop();
		int result = value1 - value2;
		push(result);
		pc += 1;
		indirectJump();
	}
	private void interp_IF_ICMPEQ(){
		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if(value1 == value2){
			pc += branchOffSet;
		}else {
			pc += 3;
		}
		indirectJump();
	}
	private void interp_if_icmpgt(){
		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if(value1 > value2){
			pc += branchOffSet;
		}else {
			pc += 3;
		}
		indirectJump();
	}
	private void interp_istore_2() {
		int value = pop();
		localVariables[2] = value;
		pc +=1;
		indirectJump();
	}
	private void interp_istore_1(){
		int value = pop();
		localVariables[1] = value;
		pc += 1;
		indirectJump();
	}
	private void interp_if_icmple(){
		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		int value2 = pop();
		int value1 = pop();
		if(value1 <= value2){
			pc += branchOffSet;
		}else {
			pc += 3;
		}
		indirectJump();

	}
	private void interp_iload_0(){
		int value = (int) getVariableValue(0);
		push(value);
		pc += 1;
		indirectJump();

	}
	private void interp_iinc(){
		int index = (int) getBytes()[pc + 1];
		int constant = (int) getBytes()[pc + 2];
		localVariables[index] += constant;
		pc += 3;
		indirectJump();
	}
	private void interp_imul(){
		int value2 = pop();
		int value1 = pop();
		int result = value1 * value2;
		push(result);
		pc += 1;
		indirectJump();

	}
	private void interp_iload_2(){
		int value = (int) getVariableValue(2);
		push(value);
		pc += 1;
		indirectJump();
	}
	private void interp_goto() {
		int branchOffSet = (getBytes()[pc + 1] << 8) | (getBytes()[pc + 2] & 0xFF);
		pc += branchOffSet;
		indirectJump();
	}
	private void interp_iconst_1(){
		push(1);
		pc += 1;
		indirectJump();
	}
	private void interp_iload_1(){

		int value = (int) getVariableValue(1);
		push(value);
		pc += 1;
		indirectJump();
	}

	private void interp_return() {
		halt();
		pc += 1;
		indirectJump();
	}

	private void interp_istore_0() {
		// TODO interpret istore_0
		int value = pop();
		localVariables[0] = value;
		pc += 1;
		indirectJump();
	}

	private void interp_iconst_0() {
		// TODO interpret iconst_0
		push(0);
		pc += 1;
		indirectJump();
	}

	private void interp_bipush(){
		byte value = (byte) getBytes()[pc + 1];
		push((int) value);
		//(int) value
		pc += 2;
		indirectJump();
	}

	// TODO add more routines
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
