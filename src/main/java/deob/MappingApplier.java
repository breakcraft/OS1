package deob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility to apply ProGuard-style mappings to Java source files.
 * Renames class files and updates references based on the mapping.
 */
public class MappingApplier {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: MappingApplier <mapping_file> <source_root>");
            System.exit(1);
        }
        File mapFile = new File(args[0]);
        File srcRoot = new File(args[1]);
        MappingLoader.Mapping mapping = MappingLoader.load(mapFile);

        // Invert class mapping: obfuscated simple name -> original fully-qualified name
        Map<String, String> classMapInv = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : mapping.classMap.entrySet()) {
            String origFqn = entry.getKey();
            String destName = entry.getValue();
            classMapInv.put(destName, origFqn);
        }

        for (Map.Entry<String, String> entry : classMapInv.entrySet()) {
            String obfSimple = entry.getKey();
            String origFqn = entry.getValue();
            String origSimple = origFqn.substring(origFqn.lastIndexOf('.') + 1);
            String parentPkg = origFqn.substring(0, origFqn.lastIndexOf('.'));

            // Determine obfuscated file location: assume same package as original
            String obfPkg = parentPkg;
            String obfFqn = obfPkg + "." + obfSimple;
            File obfFile = new File(srcRoot, obfFqn.replace('.', File.separatorChar) + ".java");
            if (!obfFile.exists()) {
                System.err.println("[WARN] File not found for obfuscated class: " + obfFile.getPath());
                continue;
            }
            // Prepare target file path
            File origFile = new File(srcRoot, origFqn.replace('.', File.separatorChar) + ".java");
            origFile.getParentFile().mkdirs();

            // Read contents
            String content = readFile(obfFile);
            // Update package declaration if needed
            String oldPkgDecl = "package " + obfPkg + ";";
            String newPkgDecl = "package " + parentPkg + ";";
            if (!oldPkgDecl.equals(newPkgDecl)) {
                content = content.replace(oldPkgDecl, newPkgDecl);
            }
            // Replace class and constructor names (word boundaries)
            content = content.replaceAll("\\b" + obfSimple + "\\b", origSimple);

            // Write to new file
            writeFile(origFile, content);
            // Delete old file
            if (!obfFile.delete()) {
                System.err.println("[WARN] Could not delete old file: " + obfFile.getPath());
            }
            System.out.println("Renamed " + obfFile.getPath() + " -> " + origFile.getPath());
        }
    }

    private static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return sb.toString();
    }

    private static void writeFile(File file, String content) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}