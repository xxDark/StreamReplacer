package me.xdark.streams.impl;

import me.xdark.streams.InstructionVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class CountVisitor implements InstructionVisitor {
    @Override
    public void visitInstruction(MethodNode method, AbstractInsnNode insn) {
        InsnList inject = new InsnList();
        inject.add(new MethodInsnNode(INVOKEINTERFACE, "java/util/Collection", "size", "I()", false));
        inject.add(new InsnNode(I2L));
    }
}
