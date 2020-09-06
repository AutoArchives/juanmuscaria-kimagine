package pw.prok.imagine.fastdiscover;

import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarCandidate extends DiscoverCandidate {
    private final File mJarFile;
    private final boolean mNeedInjection;

    public JarCandidate(File jarFile, boolean needInjection) {
        mJarFile = jarFile;
        mNeedInjection = needInjection;
    }

    @Override
    public Iterator<InputStream> iterator() {
        return new Iter(mJarFile);
    }

    @Override
    public void injectClassLoader(LaunchClassLoader classLoader) {
        if (mNeedInjection) {
            try {
                classLoader.addURL(mJarFile.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(mJarFile);
    }

    private static final class Iter implements Iterator<InputStream> {
        private ZipFile mZipFile;
        private Enumeration<? extends ZipEntry> mEntries;
        private InputStream mNextStream;

        public Iter(File jarFile) {
            try {
                mZipFile = new ZipFile(jarFile);
                mEntries = mZipFile.entries();
            } catch (IOException ignored) {
            }
        }

        @Override
        public boolean hasNext() {
            acquire();
            return mNextStream != null;
        }

        private void acquire() {
            if (mNextStream != null || mEntries == null) return;
            while (mEntries.hasMoreElements()) {
                ZipEntry entry = mEntries.nextElement();
                if (!entry.getName().endsWith(".class")) continue;
                try {
                    mNextStream = mZipFile.getInputStream(entry);
                } catch (IOException ignored) {
                }
                break;
            }
        }

        @Override
        public InputStream next() {
            acquire();
            InputStream is = mNextStream;
            mNextStream = null;
            return is;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
