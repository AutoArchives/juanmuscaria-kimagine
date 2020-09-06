package pw.prok.imagine.asm;

import pw.prok.imagine.api.Triple;

public class MethodDesc extends Triple<String, String, String> {
    public MethodDesc() {
    }

    public MethodDesc(String first, String second, String third) {
        super(first, second, third);
    }

    public MethodDesc(String desc) {
        this(ImagineRemapper.cutMethodClass(desc), ImagineRemapper.cutMethod(desc), ImagineRemapper.cutMethodDesc(desc));
    }

    @Override
    public String toString() {
        return mFirst + '/' + mSecond + mThird;
    }

    public boolean equals(String first, String second, String third) {
        return mFirst.equals(first) && mSecond.equals(second) && mThird.equals(third);
    }
}
