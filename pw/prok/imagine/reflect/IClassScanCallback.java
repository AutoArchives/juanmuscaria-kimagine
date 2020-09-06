package pw.prok.imagine.reflect;

public interface IClassScanCallback<S> {
    void scanClass(Class<S> mainClass, Class<? super S> superClass);
}
