package pw.prok.imagine.api;

public interface ICopyable<T extends ICopyable<T>> {
    T copy();
}
