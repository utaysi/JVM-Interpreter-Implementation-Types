package de.pltlab.teaching.vms.bytecodemachine.internal;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ByteVector;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.Textifier;

public class BmMethodVisitor extends MethodVisitor {

	private MethodVisitor visitor;
	private ByteArrayContainer bytes;
	private Textifier textifier;
	private Map<Integer, Variable> variables = new HashMap<>();
	private LinkedHashMap<Label, String> instructionTexts = new LinkedHashMap<>();
	private Map<Label, String> textifierLabelNames;
	private List<Label> labels = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public BmMethodVisitor(int api, MethodVisitor visitor, ByteArrayContainer bytes) {
		super(api, visitor);
		this.visitor = visitor;
		this.bytes = bytes;
		
		textifier = new Textifier();
		
		try {
			Field labelNamesField = Textifier.class.getDeclaredField("labelNames");
			labelNamesField.setAccessible(true);
			// the following is needed to force textifier to initialize this field
			textifier.visitLabel(new Label());
			this.textifierLabelNames = (Map<Label, String>) labelNamesField.get(textifier);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void constructInstructionText(Runnable textifierVisit) {
		Label label = new Label();
		visitLabel(label);
		
		textifierVisit.run();
		
		String instructionText = textifier.text.get(textifier.text.size() - 1).toString();
		textifier.visitLabel(label);
		
		instructionTexts.put(label, instructionText);
	}
	
	@Override
	public void visitInsn(int opcode) {
		constructInstructionText(() -> textifier.visitInsn(opcode));
		super.visitInsn(opcode);
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
		throw new IllegalArgumentException("The bytecode block must not contain a 'field instruction', opcode: " + Textifier.OPCODES[opcode] + ".");
	}
	
	@Override
	public void visitIincInsn(int varIndex, int increment) {
		constructInstructionText(() -> textifier.visitIincInsn(varIndex, increment));
		super.visitIincInsn(varIndex, increment);
	}
	
	@Override
	public void visitIntInsn(int opcode, int operand) {
		constructInstructionText(() -> textifier.visitIntInsn(opcode, operand));
		super.visitIntInsn(opcode, operand);
	}
	
	@Override
	public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
			Object... bootstrapMethodArguments) {
		throw new IllegalArgumentException("The bytecode block must not contain an 'invokedynamic instruction', opcode: INVOKEDYNAMIC.");
	}
	
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		textifierLabelNames.put(label, label.toString());
		constructInstructionText(() -> textifier.visitJumpInsn(opcode, label));
		super.visitJumpInsn(opcode, label);
	}
	
	@Override
	public void visitLdcInsn(Object value) {
		throw new IllegalArgumentException("The bytecode block must not contain a 'load-constant instruction', opcode: LDC.");
	}
	
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		textifierLabelNames.put(dflt, dflt.toString());
		for (Label label : labels) {
			textifierLabelNames.put(label, label.toString());
		}
		constructInstructionText(() -> textifier.visitLookupSwitchInsn(dflt, keys, labels));
		super.visitLookupSwitchInsn(dflt, keys, labels);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
		throw new IllegalArgumentException("The bytecode block must not contain a 'method instruction', opcode: " + Textifier.OPCODES[opcode] + ".");
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String descriptor) {
		throw new IllegalArgumentException("The bytecode block must not contain a 'method instruction', opcode: " + Textifier.OPCODES[opcode] + ".");
	}
	
	@Override
	public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
		constructInstructionText(() -> textifier.visitMultiANewArrayInsn(descriptor, numDimensions));
		super.visitMultiANewArrayInsn(descriptor, numDimensions);
	}
	
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
		textifierLabelNames.put(dflt, dflt.toString());
		for (Label label : labels) {
			textifierLabelNames.put(label, label.toString());

		}
		constructInstructionText(() -> textifier.visitTableSwitchInsn(min, max, dflt, labels));
		super.visitTableSwitchInsn(min, max, dflt, labels);
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type) {
		throw new IllegalArgumentException("The bytecode block must not contain a 'type instruction', opcode: " + Textifier.OPCODES[opcode] + ".");
	}
	
	@Override
	public void visitVarInsn(int opcode, int varIndex) {
		int optimizedOpcode;
		if (varIndex < 4) {
			optimizedOpcode = switch (opcode) {
			case Opcodes.ISTORE -> 0x3b + varIndex;
			case Opcodes.LSTORE -> 0x3f + varIndex;
			case Opcodes.FSTORE -> 0x43 + varIndex;
			case Opcodes.DSTORE -> 0x47 + varIndex;
			case Opcodes.ASTORE -> 0x4b + varIndex;
			case Opcodes.ILOAD -> 0x1a + varIndex;
			case Opcodes.LLOAD -> 0x1e + varIndex;
			case Opcodes.FLOAD -> 0x22 + varIndex;
			case Opcodes.DLOAD -> 0x26 + varIndex;
			case Opcodes.ALOAD -> 0x2a + varIndex;
			default -> -1;
			};
		} else {
			optimizedOpcode = -1;
		}
		
		if (optimizedOpcode >=0 )
			constructInstructionText(() -> textifier.visitInsn(optimizedOpcode));
		else
			constructInstructionText(() -> textifier.visitVarInsn(opcode, varIndex));
		super.visitVarInsn(opcode, varIndex);
	}
	
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		textifierLabelNames.put(start, start.toString());
		textifierLabelNames.put(end, end.toString());
		textifierLabelNames.put(handler, handler.toString());

		throw new IllegalArgumentException("The bytecode block must not contain try-catch statements.");
	}
	
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		super.visitMaxs(maxStack, maxLocals);
		try {
			Field codeField = visitor.getClass().getDeclaredField("code");
			codeField.setAccessible(true);
			ByteVector code = (ByteVector) codeField.get(visitor);
			
			Field codeDataField = code.getClass().getDeclaredField("data");
			codeDataField.setAccessible(true);
			
			Field codeLengthField = code.getClass().getDeclaredField("length");
			codeLengthField.setAccessible(true);
			
			byte[] data = (byte[]) codeDataField.get(code);
			int length = (int) codeLengthField.get(code);
			
			bytes.array = new byte[length];
			System.arraycopy(data, 0, bytes.array, 0, length);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		int maxOffset = instructionTexts.keySet().stream().mapToInt(l -> l.getOffset()).max().orElseGet(() -> 0);
		if (maxOffset == 0) {
			maxOffset = 1;
		}
		int offsetDigits = (int) Math.log10(maxOffset) + 1;
		StringBuffer sb = new StringBuffer();
		instructionTexts.forEach((label, text) -> {
			sb.append(String.format("%0" + offsetDigits + "d: %s", label.getOffset(), text));
		});
		bytes.disassembled = sb.toString();
		
		labels.forEach(l -> {
			bytes.disassembled = bytes.disassembled.replace(l.toString(), l.getOffset() + ":");
		});
		bytes.disassembled = "(Attention: jump targets shown as absolute offsts. In the bytecode, they are actually stored as relative offsets.)\n"+ bytes.disassembled;
		
		bytes.variables = Collections.unmodifiableMap(variables);
		
	}
	
	@Override
	public void visitLabel(Label label) {
		textifierLabelNames.put(label, label.toString());
		textifier.visitLabel(label);
		labels.add(label);
		super.visitLabel(label);
	}

	@Override
	public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end,
			int index) {
		super.visitLocalVariable(name, descriptor, signature, start, end, index);
		if (variables.containsKey(index)) {
			throw new IllegalArgumentException("The bytecode re-uses local variable slots. Please use another compiler.");
		} else {
			variables.put(index, new Variable(index, name, typeToString(Type.getType(descriptor))));
		}
	}
	
	private String typeToString(Type type) {
		return switch (type.getSort()) {
		case Type.VOID -> "void";
		case Type.BOOLEAN -> "boolean";
		case Type.CHAR -> "char";
		case Type.BYTE -> "byte";
		case Type.SHORT -> "short";
		case Type.INT -> "int";
		case Type.LONG -> "long";
		case Type.FLOAT -> "float";
		case Type.DOUBLE -> "double";
		case Type.OBJECT -> type.getClassName();
		case Type.ARRAY -> typeToString(type.getElementType()) + "[]";
		default -> throw new IllegalArgumentException("Unsupported type: " + type);
		};
	}

}
