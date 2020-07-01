package me.xdark.streams.impl;

import me.xdark.streams.InstructionVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class StreamVisitor implements InstructionVisitor {
    @Override
    public void visitInstruction(MethodNode method, AbstractInsnNode insn) {
        method.instructions.remove(insn);
    }
}
