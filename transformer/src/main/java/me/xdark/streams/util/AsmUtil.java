package me.xdark.streams.util;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.SWAP;
import static org.objectweb.asm.Opcodes.V1_8;

public final class AsmUtil {
    static final String FUNCTION_UNARY_OPERATOR = "me/xdark/streams/UnaryOperatorFunction";
    static final String COMPARABLE_SORTER = "me/xdark/streams/ComparableSorter";

    private AsmUtil() { }

    public static ClassNode functionToUnaryOperator() {
        ClassNode node = new ClassNode();
        node.visit(V1_8, ACC_PUBLIC + ACC_FINAL + ACC_SYNTHETIC, FUNCTION_UNARY_OPERATOR, null, "java/lang/Object", new String[]{"java/util/function/UnaryOperator"});
        node.visitField(ACC_PRIVATE + ACC_FINAL, "delegate", "Ljava/util/Function;", null, null);
        MethodVisitor init = node.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/Function;)V", null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitInsn(DUP);
        init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitVarInsn(ALOAD, 1);
        init.visitFieldInsn(PUTFIELD, FUNCTION_UNARY_OPERATOR, "delegate", "Ljava/util/Function;");
        init.visitInsn(RETURN);
        init.visitMaxs(2, 2);
        init.visitEnd();
        MethodVisitor apply = node.visitMethod(ACC_PUBLIC, "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
        apply.visitCode();
        apply.visitVarInsn(ALOAD, 0);
        apply.visitVarInsn(ALOAD, 1);
        apply.visitMethodInsn(INVOKESPECIAL, "java/util/Function;", "apply", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        apply.visitInsn(ARETURN);
        apply.visitMaxs(1, 2);
        apply.visitEnd();
        node.visitEnd();
        return node;
    }

    public static ClassNode comparableSorter() {
        ClassNode node = new ClassNode();
        node.visit(V1_8, ACC_PUBLIC + ACC_FINAL + ACC_SYNTHETIC, COMPARABLE_SORTER, null, "java/lang/Object", new String[]{"java/util/Comparator"});
        MethodVisitor init = node.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/util/Function;)V", null, null);
        init.visitCode();
        init.visitVarInsn(ALOAD, 0);
        init.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        init.visitInsn(RETURN);
        init.visitEnd();
        MethodVisitor compare = node.visitMethod(ACC_PUBLIC, "compare", "(Ljava/lang/Object;Ljava/lang/Object;)I", null, null);
        compare.visitCode();
        compare.visitVarInsn(ALOAD, 0);
        compare.visitVarInsn(ALOAD, 1);
        compare.visitVarInsn(ALOAD, 0);
        compare.visitVarInsn(ALOAD, 2);
        compare.visitMethodInsn(INVOKESPECIAL, "java/util/Comparable", "compareTo", "(Ljava/lang/Object;)I", false);
        compare.visitInsn(IRETURN);
        compare.visitMaxs(3, 1);
        compare.visitEnd();
        node.visitEnd();
        return node;
    }

    public static InsnList createListLengthStack() {
        InsnList list = new InsnList(); // LENGTH
        list.add(new TypeInsnNode(NEW, "java/util/ArrayList")); // LENGTH LIST
        list.add(new InsnNode(DUP_X1)); // LIST LENGTH LIST
        list.add(new InsnNode(SWAP)); // LIST LIST LENGTH
        list.add(new MethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V", false));
        return list;
    }

    public static InsnList createList(int size) {
        InsnList list = new InsnList();
        list.add(new TypeInsnNode(NEW, "java/util/ArrayList"));
        list.add(new InsnNode(DUP));
        if (size <= 5) {
            list.add(new InsnNode(ICONST_5 - 4));
        } else if (size <= Byte.MAX_VALUE) {
            list.add(new IntInsnNode(SIPUSH, size));
        } else if (size <= Short.MAX_VALUE) {
            list.add(new IntInsnNode(SIPUSH, size));
        } else {
            list.add(new LdcInsnNode(size));
        }
        list.add(new MethodInsnNode(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V", false));
        return list;
    }

    public static void set(InsnList list, AbstractInsnNode from, InsnList to) {
        int j = to.size();
        if (j != 0) {
            while (j-- > 0) {
                list.insert(from, to.get(j));
            }
        }
        list.remove(from);
    }
}
