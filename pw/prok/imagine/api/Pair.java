package pw.prok.imagine.api;

public class Pair<X, Y> {
    protected X mFirst;
    protected Y mSecond;

    public Pair() {

    }

    public Pair(X first, Y second) {
        mFirst = first;
        mSecond = second;
    }

    public X first() {
        return mFirst;
    }

    public X first(X first) {
        X old = mFirst;
        mFirst = first;
        return old;
    }

    public Y second() {
        return mSecond;
    }

    public Y second(Y second) {
        Y old = mSecond;
        mSecond = second;
        return old;
    }

    public static <X, Y> Pair<X, Y> create(X first, Y second) {
        return new Pair<X, Y>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || o.getClass() != Pair.class) return false;
        Pair pair = (Pair) o;
        if (mFirst != null ? !mFirst.equals(pair.mFirst) : pair.mFirst != null) return false;
        return !(mSecond != null ? !mSecond.equals(pair.mSecond) : pair.mSecond != null);

    }

    @Override
    public int hashCode() {
        int result = mFirst != null ? mFirst.hashCode() : 0;
        result = 31 * result + (mSecond != null ? mSecond.hashCode() : 0);
        return result;
    }
}
