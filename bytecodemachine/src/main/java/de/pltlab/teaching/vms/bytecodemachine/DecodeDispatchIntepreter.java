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
				case 0x60->{
					indirectJump();
					interp_IADD();
				}
				case 0xa2->{
					indirectJump();
					interp_if_icmpge();
				}
				case 0x5->{
					indirectJump();
					interp_iconst_2();
				}
				case 0x3e->{
					indirectJump();
					interp_istore_3();
				}
				case 0x1d->{
					indirectJump();
					interp_iload_3();
				}
				case 0x54->{
					indirectJump();
					interp_bastore();
				}
				case 0x50->{
					indirectJump();
					interp_lastore();
				}
				// TODO add further cases
				default -> throw new IllegalArgumentException("Unsupported opcode: " + opcode);
			}
			directJump();

		}
	}

	private void interp_lastore() {
		// Stores a long value into an array
		long value = pop(); // Pop the long value from the operand stack
		int index = pop(); // Pop the index from the operand stack
		int[] arrayRef = new int[]{pop()}; // Pop the array reference from the operand stack
		arrayRef[index] = (int) value; // Store the long value into the array at the specified index
		pc += 1;
		indirectJump();
	}
	private void interp_bastore() {
		// Stores a byte or boolean value into an array
		int value = pop(); // Pop the value to be stored from the operand stack
		int index = pop(); // Pop the index from the operand stack
		int[] arrayRef = new int[]{pop()}; // Pop the array reference from the operand stack and cast it to byte array
		arrayRef[index] = (byte) value; // Store the value in the array at the specified index
		pc += 1;
		indirectJump();
	}
	private void interp_iload_3() {
		// Loads the integer value from local variable 3 onto the operand stack
		int value = (int )getVariableValue(3); // Load the value from local variable 3
		push(value); // Push the value onto the operand stack
		pc += 1;
		indirectJump();
	}
	private void interp_istore_3() {
		// Stores the integer value from the top of the operand stack into local variable 3
		int value = pop(); // Pop the value from the operand stack
		localVariables[3] = value; // Store the value in local variable 3
		pc += 1;
		indirectJump();
	}

	private void interp_iconst_2() {
		// Pushes the integer value 2 onto the operand stack
		push(2);
		pc += 1;
		indirectJump();
	}
	private void interp_if_icmpge() {
		// Conditional branch if the second value is greater than or equal to the first value
		int value2 = pop(); // Pop the second value from the operand stack
		int value1 = pop(); // Pop the first value from the operand stack
		if (value1 >= value2) {
			pc += 1; // Increment the program counter to perform the branch
		} else {
			pc += 3; // Skip the branch offset if the condition is not met
		}
		indirectJump();
	}
	private void interp_IADD() {
		// Adds two integers from the top of the operand stack
		int value2 = pop(); // Pop the second value from the operand stack
		int value1 = pop(); // Pop the first value from the operand stack
		int result = value1 + value2; // Add the two values
		push(result); // Push the result back onto the operand stack
		pc += 1;
		indirectJump();
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
