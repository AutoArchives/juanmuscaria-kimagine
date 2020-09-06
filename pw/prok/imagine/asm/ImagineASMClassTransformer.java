package pw.prok.imagine.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import pw.prok.imagine.pool.Pool;
import pw.prok.imagine.pool.Pools;
import pw.prok.imagine.util.Array;

public class ImagineASMClassTransformer implements IClassTransformer {
    private static Transformer[] sTransformers = new Transformer[0];
    private static Pool<ImagineASM> sAsmPool = Pools.create(ImagineASM.class);

    public static void addTransformer(Transformer transformer) {
        sTransformers = Array.appendToArray(sTransformers, transformer);
    }

    public ImagineASMClassTransformer() {

    }

    @Override
    public byte[] transform(String s, String s1, byte[] bytes) {
        ImagineASM asm = sAsmPool.obtain();
        asm.loadClass(s, s1, bytes);
        for (Transformer transformer : sTransformers) {
            transformer.transform(asm);
        }
        bytes = asm.build();
        asm.clear();
        sAsmPool.release(asm);
        return bytes;
    }
}
