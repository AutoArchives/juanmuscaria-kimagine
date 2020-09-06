package pw.prok.imagine.object;


public interface IHandler<T> {
    void putData(Object object, T member, Object value) throws Exception;

    Object getData(Object object, T member) throws Exception;
}
