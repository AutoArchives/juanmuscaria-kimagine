package pw.prok.imagine.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

public class ConfigReader {
    private static final Charset UTF_8 = Charset.forName("utf-8");

    public static Map<String, String> read(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, UTF_8));
            String line;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
