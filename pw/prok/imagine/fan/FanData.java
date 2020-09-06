package pw.prok.imagine.fan;

public class FanData implements Fan {
    public FanData() {
    }

    public FanData(Fan fan) {
        mName = fan.name();
        mId = fan.id();
        mVersion = fan.version();
        mServerRequired = fan.serverRequired();
        mClientRequired = fan.clientRequired();
    }

    private String mName;

    @Override
    public String name() {
        return mName;
    }

    public FanData name(String name) {
        mName = name;
        return this;
    }

    private String mId;

    @Override
    public String id() {
        return mId;
    }

    public FanData id(String id) {
        mId = id;
        return this;
    }

    private String mVersion;

    @Override
    public String version() {
        return mVersion;
    }

    public FanData version(String version) {
        mVersion = version;
        return this;
    }

    private boolean mServerRequired;

    @Override
    public boolean serverRequired() {
        return mServerRequired;
    }

    public FanData serverRequired(boolean serverRequired) {
        mServerRequired = serverRequired;
        return this;
    }

    private boolean mClientRequired;

    @Override
    public boolean clientRequired() {
        return mClientRequired;
    }

    public FanData clientRequired(boolean clientRequired) {
        mClientRequired = clientRequired;
        return this;
    }

    @Override
    public Class<? extends Fan> annotationType() {
        return Fan.class;
    }
}
