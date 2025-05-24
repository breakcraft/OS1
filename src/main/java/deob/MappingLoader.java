package deob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MappingLoader {
    public static class Mapping {
        public final Map<String, String> classMap = new HashMap<String, String>();
        public final Map<String, String> fieldMap = new LinkedHashMap<String, String>();
        public final Map<String, String> methodMap = new LinkedHashMap<String, String>();
    }

    /**
     * Loads mappings from a ProGuard-style mapping file.
     * Supports class, field, and method mappings.
     * @param file the mapping file (e.g., proguard.map)
     * @return Mapping data
     * @throws IOException if file read fails
     */
    public static Mapping load(File file) throws IOException {
        Mapping mapping = new Mapping();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            String currentClass = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Class mapping: original.Class -> obfName:
                if (!line.startsWith("    ") && line.contains("->") && line.trim().endsWith(":")) {
                    String trimmed = line.trim();
                    String entry = trimmed.substring(0, trimmed.length() - 1);
                    String[] parts = entry.split(" -> ", 2);
                    if (parts.length == 2) {
                        String orig = parts[0].trim();
                        String dest = parts[1].trim();
                        mapping.classMap.put(orig, dest);
                        currentClass = orig;
                    }
                }
                // Member mapping lines start with indentation
                else if (line.startsWith("    ") && currentClass != null) {
                    String trimmed = line.trim();
                    String[] parts = trimmed.split(" -> ", 2);
                    if (parts.length == 2) {
                        String left = parts[0].trim();
                        String dest = parts[1].trim();
                        int idx = left.lastIndexOf(' ');
                        String origName = idx >= 0 ? left.substring(idx + 1) : left;
                        if (origName.indexOf('(') >= 0) {
                            mapping.methodMap.put(currentClass + "." + origName, dest);
                        } else {
                            mapping.fieldMap.put(currentClass + "." + origName, dest);
                        }
                    }
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return mapping;
    }

    /**
     * Command-line entry point: prints summary counts.
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: MappingLoader <mapping_file>");
            System.exit(1);
        }
        File mapFile = new File(args[0]);
        System.out.println("Loading mappings from: " + mapFile.getPath());
        Mapping mapping = load(mapFile);
        System.out.println("Class mappings: " + mapping.classMap.size());
        System.out.println("Field mappings: " + mapping.fieldMap.size());
        System.out.println("Method mappings: " + mapping.methodMap.size());
    }
}