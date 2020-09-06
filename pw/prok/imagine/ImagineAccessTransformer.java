package pw.prok.imagine;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class ImagineAccessTransformer extends AccessTransformer {
    public ImagineAccessTransformer() throws IOException {
        super("imagine_at.cfg");
    }
}
