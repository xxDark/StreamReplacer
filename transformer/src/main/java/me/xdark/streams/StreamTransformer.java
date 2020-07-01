package me.xdark.streams;

import me.xdark.streams.impl.AllMatchVisitor;
import me.xdark.streams.impl.AnyMatchVisitor;
import me.xdark.streams.impl.ArrayVisitor;
import me.xdark.streams.impl.CloseVisitor;
import me.xdark.streams.impl.CountVisitor;
import me.xdark.streams.impl.EmptyVisitor;
import me.xdark.streams.impl.FindAnyVisitor;
import me.xdark.streams.impl.FindFirstVisitor;
import me.xdark.streams.impl.ForEachVisitor;
import me.xdark.streams.impl.IteratorVisitor;
import me.xdark.streams.impl.LimitVisitor;
import me.xdark.streams.impl.MapVisitor;
import me.xdark.streams.impl.NoneMatchVisitor;
import me.xdark.streams.impl.OnCloseVisitor;
import me.xdark.streams.impl.PeekVisitor;
import me.xdark.streams.impl.SingleVisitor;
import me.xdark.streams.impl.SkipVisitor;
import me.xdark.streams.impl.SpliteratorVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

import java.util.HashMap;
import java.util.Map;

public final class StreamTransformer {
    private static final Map<VisitTarget, InstructionVisitor> VISITOR_MAP;

    private StreamTransformer() { }

    public static void transform(MethodNode method) {
        InsnList list = method.instructions;
        if (list.size() > 0) {
            for (AbstractInsnNode insn : list) {
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode node = (MethodInsnNode) insn;
                    InstructionVisitor visitor = visitFor(node);
                    if (visitor != null) {
                        visitor.visitInstruction(method, node);
                    }
                } else if (insn instanceof TypeInsnNode && insn.getOpcode() == Opcodes.CHECKCAST) {
                    String desc = ((TypeInsnNode)insn).desc;
                    if ("java/util/stream/BaseStream".equals(desc) || "java/util/stream/Stream".equals(desc)) {
                        list.remove(insn);
                    }
                }
            }
        }
    }

    public static InstructionVisitor visitFor(String owner, String name, String desc) {
        return VISITOR_MAP.get(new VisitTarget(owner, name, desc));
    }

    public static InstructionVisitor visitFor(MethodInsnNode node) {
        return VISITOR_MAP.get(new VisitTarget(node.owner, node.name, node.desc));
    }

    private static VisitTarget base(String name, String desc) {
        return new VisitTarget("java/util/stream/BaseStream", name, desc);
    }

    private static VisitTarget stream(String name, String desc) {
        return new VisitTarget("java/util/stream/Stream", name, desc);
    }

    static {
        Map<VisitTarget, InstructionVisitor> visitorMap = VISITOR_MAP = new HashMap<>();
        visitorMap.put(stream("empty", "()Ljava/util/stream/Stream;"), new EmptyVisitor());
        visitorMap.put(stream("of", "(Ljava/lang/Object;)Ljava/util/stream/Stream;"), new SingleVisitor());
        visitorMap.put(stream("of", "([Ljava/lang/Object;)Ljava/util/stream/Stream;"), new ArrayVisitor());
        visitorMap.put(stream("map", "(Ljava/util/function/Function;)Ljava/util/stream/Stream;"), new MapVisitor());
        visitorMap.put(stream("forEach", "(Ljava/util/function/Consumer;)V"), new ForEachVisitor());
        visitorMap.put(stream("peek", "(Ljava/util/function/Consumer;)Ljava/util/stream/Stream;"), new PeekVisitor());

        visitorMap.put(base("onClose", "(Ljava/lang/Runnable;)Ljava/util/stream/BaseStream;"), new OnCloseVisitor());
        visitorMap.put(base("close", "()V"), new CloseVisitor());
        visitorMap.put(stream("onClose", "(Ljava/lang/Runnable;)Ljava/util/stream/BaseStream;"), new OnCloseVisitor());
        visitorMap.put(stream("close", "()V"), new CloseVisitor());

        visitorMap.put(stream("skip", "(J)Ljava/util/stream/Stream;"), new SkipVisitor());
        visitorMap.put(stream("limit", "(J)Ljava/util/stream/Stream;"), new LimitVisitor());

        visitorMap.put(stream("anyMatch", "(Ljava/util/function/Predicate;)Z"), new AnyMatchVisitor());
        visitorMap.put(stream("allMatch", "(Ljava/util/function/Predicate;)Z"), new AllMatchVisitor());
        visitorMap.put(stream("noneMatch", "(Ljava/util/function/Predicate;)Z"), new NoneMatchVisitor());
        visitorMap.put(stream("count", "()J"), new CountVisitor());

        visitorMap.put(base("iterator", "()Ljava/util/Iterator;"), new IteratorVisitor());
        visitorMap.put(stream("iterator", "()Ljava/util/Iterator;"), new IteratorVisitor());

        visitorMap.put(base("spliterator", "()Ljava/util/Spliterator;"), new SpliteratorVisitor());
        visitorMap.put(stream("spliterator", "()Ljava/util/Spliterator;"), new SpliteratorVisitor());

        visitorMap.put(stream("findFirst", "()Ljava/util/Optional;"), new FindFirstVisitor());
        visitorMap.put(stream("findAny", "()Ljava/util/Optional;"), new FindAnyVisitor());
    }
}
