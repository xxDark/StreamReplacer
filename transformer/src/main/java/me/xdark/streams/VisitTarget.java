package me.xdark.streams;

import java.util.Objects;

public final class VisitTarget {
    private final String owner, name, desc;

    public VisitTarget(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitTarget that = (VisitTarget) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(name, that.name) &&
                Objects.equals(desc, that.desc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner, name, desc);
    }
}
