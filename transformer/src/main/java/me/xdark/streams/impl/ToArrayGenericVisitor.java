package me.xdark.streams.impl;

import me.xdark.streams.InstructionVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class ToArrayGenericVisitor implements InstructionVisitor {
    @Override
    public void visitInstruction(MethodNode method, AbstractInsnNode insn) {
        method.instructions.set(insn,new MethodInsnNode(INVOKEINTERFACE, "java/util/Collection", "toArray", "([Ljava/lang/Object;)[Ljava/lang/Object;", true));
    }
}
