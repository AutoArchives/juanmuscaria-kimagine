package pw.prok.imagine.asm;

import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class MethodFilter implements Iterable<ImagineMethod> {
    private final ImagineASM mAsm;
    private final Filter mFilter;

    public MethodFilter(ImagineASM asm, Filter filter) {
        mAsm = asm;
        mFilter = filter;
    }

    @Override
    public Iterator<ImagineMethod> iterator() {
        mAsm.readClass();
        return new MethodIterator(this, mAsm.mClassNode.methods.iterator());
    }

    private static class MethodIterator implements Iterator<ImagineMethod> {
        private final MethodFilter mFilter;
        private final Iterator<MethodNode> mIterator;
        private ImagineMethod mMethod;

        public MethodIterator(MethodFilter filter, Iterator<MethodNode> iterator) {
            mFilter = filter;
            mIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            mMethod = null;
            while (mIterator.hasNext()) {
                MethodNode node = mIterator.next();
                ImagineMethod method = new ImagineMethod(mFilter.mAsm, node);
                if (mFilter.mFilter.matching(method)) {
                    mMethod = method;
                }
            }
            return mIterator.hasNext();
        }

        @Override
        public ImagineMethod next() {
            if (mMethod == null) hasNext();
            ImagineMethod method = mMethod;
            mMethod = null;
            return method;
        }

        @Override
        public void remove() {
            mIterator.remove();
        }
    }
}
