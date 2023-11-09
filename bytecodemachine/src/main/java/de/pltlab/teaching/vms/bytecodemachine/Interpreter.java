package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import de.pltlab.teaching.vms.bytecodemachine.internal.BmClassVisitor;
import de.pltlab.teaching.vms.bytecodemachine.internal.Variable;

public abstract class Interpreter {

	@SuppressWarnings("serial")
	private static class Termination extends RuntimeException {

	}

	/**
	 * Bytecode of the code to interpret.
	 */
	private byte[] bytes;

	/**
	 * Textual representation of the code.
	 */
	private String code;

	/**
	 * Information about the name, type and index of the local variables used in the
	 * code.
	 */
	private List<Variable> variables;

	/**
	 * Number of extraction operations performed during interpretation.
	 */
	private int extracts = 0;

	/**
	 * Number of direct jump operations performed during interpretation.
	 */
	private int directJumps = 0;

	/**
	 * Number of indirect jump operations performed during interpretation.
	 */
	private int indirectJumps = 0;

	/**
	 * Remembers whether dynamic bridging has been used, i.e., transferring control
	 * to another routine immediately, instead of calling the routine. For internal
	 * purposes only.
	 */
	private boolean bridging;

	/**
	 * Records, whether the execution of the code has finished and the
	 * interpretation should terminate.
	 */
	private boolean halt;

	/**
	 * Remembers the file containing the code to be interpreted.
	 */
	private File file;

	/**
	 * Create a new interpreter for the code in the passed file. The file must be a
	 * .class file containing a compiled Java class with a method
	 * <code>public static void main() {...}</code>. This method will be
	 * interpreted.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public Interpreter(File file) throws IOException {
		this.file = file;
		bytes = BmClassVisitor.getMainMethodBytecode(file);
		code = BmClassVisitor.getMainMethodCode(file);
		variables = BmClassVisitor.getMainMethodVariables(file);
	}

	/**
	 * Print all details of the code and start interpretation. The details which are
	 * printed are the disassembled representation of the code, the code represented
	 * as bytecode shown in hexadecimal numbers, the code represented as bytecode
	 * shown in binary numbers, the local variables of the code and their values
	 * after the execution and a report on the operations that occurred during the
	 * execution.
	 */
	public void run() {
		run(true, true, true);
	}

	/**
	 * Print some details of the code and start interpretation. The details which
	 * can be printed are the disassembled representation of the code, the code
	 * represented as bytecode shown in hexadecimal numbers, the code represented as
	 * bytecode shown in binary numbers, the local variables of the code and their
	 * values after the execution and a report on the operations that occurred
	 * during the execution.
	 *
	 * @param printCode        toggle printing the disassembled representation of
	 *                         the code
	 * @param printBytecodeHex toggle printing the hexadecimal representation of the
	 *                         bytecode
	 * @param printBytecodeBin toggle printing the binary representation of the
	 *                         bytecode
	 */
	public void run(boolean printCode, boolean printBytecodeHex, boolean printBytecodeBin) {
		System.out.println("Program code");
		System.out.println("============\n");

		System.out.println("Filename: " + file.getPath());
		System.out.println();

		if (printCode) {
			System.out.println("Code:");
			System.out.println(code);
		}
		if (printBytecodeHex) {
			System.out.println("Bytecode (hex):");
			System.out.println(bytesToHexString(bytes));
			System.out.println();
		}
		if (printBytecodeBin) {
			System.out.println("Bytecode (bin):");
			System.out.println(bytesToBinString(bytes));
			System.out.println();
		}

		extracts = 0;
		directJumps = 0;
		indirectJumps = 0;

		System.out.println("Execution");
		System.out.println("=========\n");

		try {
			interpret();
		} catch (Termination te) {
		}

		System.out.println("Result");
		System.out.println("======\n");

		System.out.println("Local Variables:");
		System.out.println(variablesToString(variables));
		System.out.println();

		System.out.println("Report on number of operations");
		System.out.println("==============================\n");

		System.out.println(report());
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			sb.append(String.format("%02x ", b & 0xff));
			if ((i + 1) % 16 == 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private static String bytesToBinString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			sb.append(String.format("%8s", Integer.toBinaryString(b & 0xff)).replace(' ', '0')).append(" ");
			if ((i + 1) % 4 == 0) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private String variablesToString(List<Variable> variables) {
		return variables.stream().map(
				var -> String.format("%2d : %s (%s) = %s", var.index, var.name, var.type, getVariableValue(var.index)))
				.collect(Collectors.joining("\n"));
	}

	/**
	 * Perform the interpretation. This method is invoked by the <code>run</code>
	 * method. Do not invoke this method directly.
	 */
	protected abstract void interpret();

	/**
	 * Return the value of the variable with the provided index. This method is used
	 * by the <code>run</code> method to produce the output of variables after the
	 * interpretation.
	 * 
	 * @param index
	 * @return
	 */
	protected abstract Object getVariableValue(int index);

	/**
	 * Simulates dynamic bridging to a routine represented by a
	 * <code>RoutinePointer</code>. This is intended to be used by a threaded
	 * interpreter. The "pointer" of the routine to which control should be
	 * transferred must be an instance of the functional interface
	 * <code>RoutinePointer</code> with the signature "not parameters, result type
	 * void". This can be created by using Java method references. Consider that
	 * your subclass of <code>Interpreter</code> has a method
	 * <code>void routine()</code>, to bridge to this method you can invoke this
	 * helper method by <code>brideTo(this::routine);</code>.
	 * 
	 * @param routine
	 */
	protected void bridgeTo(RoutinePointer routine) {
		bridging = true;
		routine.run();
	}

	/**
	 * Notify the compiler that the interpreted program has terminated. This method
	 * must be called by a subclass of <code>Interpreter</code> when a "return"
	 * instruction has been encountered. It will update the halt flag recorded by
	 * this Interpreter, which can be inquired by calling the method
	 * <code>halted()</code>.
	 * 
	 * This method also terminates the execution of the interpreter if bridging was
	 * used.
	 */
	protected void halt() {
		halt = true;
		if (bridging) {
			throw new Termination();
		}

	}

	/**
	 * Check whether an the <code>halt()</code> method has been invoked already
	 * during the interpretation, this is whether the interpretation should stop.
	 * 
	 * @return
	 */
	public boolean halted() {
		return halt;
	}

	/**
	 * Get the bytecode of the code to be interpreted.
	 * 
	 * The bytes in the returned array should be interpreted as unsigned integer values, but
	 * the Java data type <code>byte<code> is signed. To interpret a byte <code>b</code> as
	 * unsigned integer perform the following transformation: <code>int unsigned = ((int) b) & 0xff;</code>.
	 * transformed into t 
	 * 
	 * @return
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Record that an extract operation has been performed by the interpreter.
	 */
	public void extraction() {
		extracts++;
	}

	/**
	 * Record that a direct jump operation has been performed by the interpreter.
	 */
	public void directJump() {
		directJumps++;
	}

	/**
	 * Record that an indirect jump operation has been performed by the interpreter.
	 */
	public void indirectJump() {
		indirectJumps++;
	}

	/**
	 * Create a report of the operations that have been performed during the
	 * interpreation.
	 * 
	 * @return
	 */
	public String report() {
		StringBuilder sb = new StringBuilder();
		sb.append("Extraction operations   : ").append(extracts).append("\n")
		.append("Jump operations (total) : ").append(directJumps + indirectJumps).append("\n")
		.append("  - direct jumps        : ").append(directJumps).append("\n")
		.append("  - indirect jumps      : ").append(indirectJumps).append("\n");

		return sb.toString();
	}

}
