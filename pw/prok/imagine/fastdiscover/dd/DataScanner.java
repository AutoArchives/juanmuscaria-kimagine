package pw.prok.imagine.fastdiscover.dd;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ASM5;

public class DataScanner extends ClassVisitor {
    private final DiscoverData mData;
    private String mClassName;
    private final Set<String> mAnnotations = new HashSet<>();
    private final DataScannerCallback mCallback;

    public DataScanner(DiscoverData data, DataScannerCallback callback) {
        super(ASM5);
        mData = data;
        mCallback = callback;
    }

    public void scanClass(InputStream is) {
        try {
            ClassReader reader = new ClassReader(is);
            reader.accept(this, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            is.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        mClassName = name.replace('/', '.');
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        mAnnotations.add(desc.substring(1, desc.length() - 1).replace('/', '.'));
        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitEnd() {
        if (mAnnotations.size() > 0) {
            mData.putAnnotations(mClassName, mAnnotations);
        }
        if (mCallback != null) {
            mCallback.annotationResult(mClassName, mAnnotations);
        }

        mClassName = null;
        mAnnotations.clear();
    }

    public interface DataScannerCallback {
        void annotationResult(String className, Set<String> annotations);
    }
}
