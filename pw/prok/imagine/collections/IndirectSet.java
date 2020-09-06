package pw.prok.imagine.collections;

import java.util.Set;

public class IndirectSet<T> extends AbstractIndirectList<T> implements Set<T> {
    public IndirectSet() {
    }

    public IndirectSet(int initialSize) {
        super(initialSize);
    }
}
