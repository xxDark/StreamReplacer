package me.xdark.streams.impl;

import me.xdark.streams.InstructionVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class CloseVisitor implements InstructionVisitor {
    @Override
    public void visitInstruction(MethodNode method, AbstractInsnNode insn) {
        method.instructions.set(insn, new MethodInsnNode(INVOKESTATIC, "me/xdark/streams/StreamSupport", "close", "(Ljava/util/Collection;)V", false));
    }
}
