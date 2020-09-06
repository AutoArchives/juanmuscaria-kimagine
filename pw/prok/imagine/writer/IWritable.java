package pw.prok.imagine.writer;

public interface IWritable<T extends IWritable<T>> {
    void write(WritableBuf buffer);
}
