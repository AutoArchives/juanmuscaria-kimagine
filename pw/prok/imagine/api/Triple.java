package pw.prok.imagine.api;

public class Triple<X, Y, Z> extends Pair<X, Y> {
    protected Z mThird;

    public Triple() {

    }

    public Triple(X first, Y second, Z third) {
        super(first, second);
        mThird = third;
    }

    public Z third() {
        return mThird;
    }

    public Z third(Z third) {
        Z old = mThird;
        mThird = third;
        return old;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != Triple.class) return false;
        Triple triple = (Triple) o;
        if (mFirst != null ? !mFirst.equals(triple.mFirst) : triple.mFirst != null) return false;
        if (mSecond != null ? !mSecond.equals(triple.mSecond) : triple.mSecond != null) return false;
        return !(mThird != null ? !mThird.equals(triple.mThird) : triple.mThird != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mThird != null ? mThird.hashCode() : 0);
        return result;
    }

    public static <X, Y, Z> Triple<X, Y, Z> create(X first, Y second, Z third) {
        return new Triple<>(first, second, third);
    }
}
