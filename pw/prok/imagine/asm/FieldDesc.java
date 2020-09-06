package pw.prok.imagine.asm;

import pw.prok.imagine.api.Pair;

public class FieldDesc extends Pair<String, String> {
    public FieldDesc() {
    }

    public FieldDesc(String first, String second) {
        super(first, second);
    }

    public FieldDesc(String desc) {
        this(ImagineRemapper.cutFieldClass(desc), ImagineRemapper.cutField(desc));
    }

    @Override
    public String toString() {
        return mFirst + '/' + mSecond;
    }

    public boolean equals(String first, String second) {
        return mFirst.equals(first) && mSecond.equals(second);
    }
}
