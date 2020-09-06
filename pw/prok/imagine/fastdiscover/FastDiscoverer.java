package pw.prok.imagine.fastdiscover;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.launchwrapper.LaunchClassLoader;
import pw.prok.imagine.fan.Fan;
import pw.prok.imagine.fastdiscover.dd.DataScanner;
import pw.prok.imagine.fastdiscover.dd.DiscoverData;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class FastDiscoverer implements DataScanner.DataScannerCallback {
    public static final DiscoverData DISCOVER_DATA;
    public static final DataScanner DATA_SCANNER;

    static {
        DISCOVER_DATA = new DiscoverData();
        DATA_SCANNER = new DataScanner(DISCOVER_DATA, new FastDiscoverer());
    }

    private static final FileFilter JAR_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return isFileValid(file);
        }
    };
    private static final FileFilter DIR_FILTER = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return isDirValid(file);
        }
    };

    private static boolean isDirValid(File file) {
        if (!file.isDirectory()) return false;
        File metaInf = new File(file, "META-INF");
        return metaInf.exists() && metaInf.isDirectory();
    }

    private static boolean isFileValid(File file) {
        return file.isFile() && (file.getName().endsWith(".jar") || file.getName().endsWith(".zip"));
    }

    public static void discoverClassloader(LaunchClassLoader loader, DataScanner scanner) {
        List<DiscoverCandidate> candidates = new LinkedList<>();
        for (URL url : loader.getSources()) {
            try {
                File file = new File(url.toURI());
                if (!file.exists()) continue;
                if (file.isDirectory() && isDirValid(file)) {
                    candidates.add(new DirCandidate(file, false));
                } else if (file.isFile() && isFileValid(file)) {
                    candidates.add(new JarCandidate(file, false));
                }
            } catch (Exception ignored) {
            }
        }
        scan(loader, candidates, scanner);
    }

    public static void discover(LaunchClassLoader loader, File mcHome, String mcVersion, DataScanner scanner) {
        List<DiscoverCandidate> candidates = new LinkedList<>();
        File modsDir = new File(mcHome, "mods");
        if (modsDir.exists() && modsDir.isDirectory())
            scanDir(modsDir, candidates);
        File versionModsDir = new File(modsDir, mcVersion);
        if (versionModsDir.exists() && versionModsDir.isDirectory())
            scanDir(versionModsDir, candidates);
        scan(loader, candidates, scanner);
    }

    private static void scanDir(File dir, List<DiscoverCandidate> candidates) {
        File[] files = dir.listFiles(JAR_FILTER);
        if (files != null && files.length > 0) {
            for (File file : files) {
                candidates.add(new JarCandidate(file, true));
            }
        }
        files = dir.listFiles(DIR_FILTER);
        if (files != null && files.length > 0) {
            for (File file : files) {
                candidates.add(new DirCandidate(file, true));
            }
        }
    }

    private static boolean sNeedInjection;

    private static void scan(LaunchClassLoader loader, List<DiscoverCandidate> candidates, DataScanner scanner) {
        for (DiscoverCandidate candidate : candidates) {
            sNeedInjection = false;
            for (InputStream is : candidate) {
                scanner.scanClass(is);
            }
            if (sNeedInjection) {
                FMLLog.info("Found Fan in " + candidate + ", injecting into classloader...");
                candidate.injectClassLoader(loader);
            }
        }
    }

    @Override
    public void annotationResult(String className, Set<String> annotations) {
        sNeedInjection = annotations.contains(Fan.class.getName());
    }
}
