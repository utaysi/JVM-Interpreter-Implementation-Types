package de.pltlab.teaching.vms.bytecodemachine.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BmClassVisitor extends ClassVisitor {
	
	ByteArrayContainer bytes = new ByteArrayContainer();

	public BmClassVisitor(int api, ClassVisitor classVisitor) {
		super(api, classVisitor);
	}

	public BmClassVisitor(int api) {
		super(api);
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
			String[] exceptions) {
		MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
		if (name.equals("main") && descriptor.equals("()V"))
			return new BmMethodVisitor(api, visitor, bytes);
		else
			return visitor;
	}
	
	public byte[] getBytecodeBlock() {
		return bytes.array;
	}
	
	public String getBytecodeBlockDisassembled() {
		return bytes.disassembled;
	}
	
	public List<Variable> getBytecodeBlockVariables() {
		List<Variable> result = new ArrayList<>(bytes.variables.values());
		Collections.sort(result, (v1, v2) -> Integer.compare(v1.index, v2.index));
		return Collections.unmodifiableList(result);
	}

	public static byte[] getMainMethodBytecode(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(0);
			
			BmClassVisitor bmClassVisitor = new BmClassVisitor(Opcodes.ASM9, cw);
			cr.accept(bmClassVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
			byte[] bytes = bmClassVisitor.getBytecodeBlock();
			if (bytes == null) {
				throw new IllegalArgumentException("The class file " + file.getPath() + "does not implement the method 'void main()'.");
			} else {
				return bytes;
			}
		}
	}

	public static String getMainMethodCode(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(0);
			
			BmClassVisitor bmClassVisitor = new BmClassVisitor(Opcodes.ASM9, cw);
			cr.accept(bmClassVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
			String code = bmClassVisitor.getBytecodeBlockDisassembled();
			if (code == null) {
				throw new IllegalArgumentException("The class file " + file.getPath() + "does not implement the method 'void main()'.");
			} else {
				return code;
			}
		}
	}

	public static List<Variable> getMainMethodVariables(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			ClassReader cr = new ClassReader(in);
			ClassWriter cw = new ClassWriter(0);
			
			BmClassVisitor bmClassVisitor = new BmClassVisitor(Opcodes.ASM9, cw);
			cr.accept(bmClassVisitor, ClassReader.SKIP_FRAMES);
			List<Variable> variables = bmClassVisitor.getBytecodeBlockVariables();
			if (variables == null) {
				throw new IllegalArgumentException("The class file " + file.getPath() + "does not implement the method 'void main()'.");
			} else {
				return variables;
			}
		}
	}

}
