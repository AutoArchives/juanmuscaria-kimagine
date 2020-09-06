package pw.prok.imagine.fastdiscover;

import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class DirCandidate extends DiscoverCandidate {
    private final File mDir;
    private final boolean mNeedInjection;

    public DirCandidate(File dir, boolean needInjection) {
        mDir = dir;
        mNeedInjection = needInjection;
    }

    @Override
    public Iterator<InputStream> iterator() {
        return new Iter(mDir);
    }

    @Override
    public void injectClassLoader(LaunchClassLoader classLoader) {
        if (mNeedInjection) {
            try {
                classLoader.addURL(mDir.toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(mDir);
    }

    private static final class Iter implements Iterator<InputStream> {
        private final Set<File> mClasses = new LinkedHashSet<>();
        private final Iterator<File> mIterator;

        public Iter(File dir) {
            scanDir(dir);
            mIterator = mClasses.iterator();
        }

        private void scanDir(File dir) {
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) return;
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDir(file);
                } else if (file.isFile() && file.getName().endsWith(".class")) {
                    mClasses.add(file);
                }
            }
        }

        @Override
        public boolean hasNext() {
            return mIterator.hasNext();
        }

        @Override
        public InputStream next() {
            try {
                return new FileInputStream(mIterator.next());
            } catch (FileNotFoundException ignored) {
                return null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
