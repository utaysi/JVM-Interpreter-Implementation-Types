package de.pltlab.teaching.vms.bytecodemachine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class BytecodeMachine {
	
	public static void main(String[] args) throws IOException {
		File file = Path.of("target/test-classes/Simple.class".replace('/', File.separatorChar)).toFile();
//		File file = Path.of("target/test-classes/GGT.class".replace('/', File.separatorChar)).toFile();
//		File file = Path.of("target/test-classes/Factorial.class".replace('/', File.separatorChar)).toFile();

		Interpreter interpreter = new DecodeDispatchIntepreter(file);
//		Interpreter interpreter = new IndirectThreadedIntepreter(file);
//		Interpreter interpreter = new DirectThreadedIntepreter(file);

		interpreter.run();
	}
	
}
