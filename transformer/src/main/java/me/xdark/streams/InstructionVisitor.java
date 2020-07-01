package me.xdark.streams;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

@FunctionalInterface
public interface InstructionVisitor extends Opcodes {
    void visitInstruction(MethodNode method, AbstractInsnNode insn);
}
