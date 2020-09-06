package pw.prok.imagine.util;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

import java.io.File;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModContainerWrapper<T extends ModContainer> implements ModContainer {
    protected T mContainer;

    public ModContainerWrapper(T container) {
        mContainer = container;
    }

    protected ModContainerWrapper() {

    }

    @Override
    public String getModId() {
        return mContainer.getModId();
    }

    @Override
    public String getName() {
        return mContainer.getName();
    }

    @Override
    public String getVersion() {
        return mContainer.getVersion();
    }

    @Override
    public File getSource() {
        return mContainer.getSource();
    }

    @Override
    public ModMetadata getMetadata() {
        return mContainer.getMetadata();
    }

    @Override
    public void bindMetadata(MetadataCollection metadataCollection) {
        mContainer.bindMetadata(metadataCollection);
    }

    @Override
    public void setEnabledState(boolean enabled) {
        mContainer.setEnabledState(enabled);
    }

    @Override
    public Set<ArtifactVersion> getRequirements() {
        return mContainer.getRequirements();
    }

    @Override
    public List<ArtifactVersion> getDependencies() {
        return mContainer.getDependencies();
    }

    @Override
    public List<ArtifactVersion> getDependants() {
        return mContainer.getDependants();
    }

    @Override
    public String getSortingRules() {
        return mContainer.getSortingRules();
    }

    @Override
    public boolean registerBus(EventBus eventBus, LoadController loadController) {
        return mContainer.registerBus(eventBus, loadController);
    }

    @Override
    public boolean matches(Object o) {
        return mContainer.matches(o);
    }

    @Override
    public Object getMod() {
        return mContainer.getMod();
    }

    @Override
    public ArtifactVersion getProcessedVersion() {
        return mContainer.getProcessedVersion();
    }

    @Override
    public boolean isImmutable() {
        return mContainer.isImmutable();
    }

    @Override
    public String getDisplayVersion() {
        return mContainer.getDisplayVersion();
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return mContainer.acceptableMinecraftVersionRange();
    }

    @Override
    public Certificate getSigningCertificate() {
        return mContainer.getSigningCertificate();
    }

    @Override
    public Map<String, String> getCustomModProperties() {
        return mContainer.getCustomModProperties();
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        return mContainer.getCustomResourcePackClass();
    }

    @Override
    public Map<String, String> getSharedModDescriptor() {
        return mContainer.getSharedModDescriptor();
    }

    @Override
    public Disableable canBeDisabled() {
        return mContainer.canBeDisabled();
    }

    @Override
    public String getGuiClassName() {
        return mContainer.getGuiClassName();
    }

    @Override
    public List<String> getOwnedPackages() {
        return mContainer.getOwnedPackages();
    }

    @Override
    public String toString() {
        return mContainer.toString();
    }
}
