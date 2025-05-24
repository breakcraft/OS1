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

        // First, rename classes and update their declarations
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
        // Next, apply method and field renaming across all source files
        // Invert method and field mappings: obfName -> originalName
        Map<String, String> methodMapInv = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> e : mapping.methodMap.entrySet()) {
            String key = e.getKey(); // originalFqn.methodName(params)
            String obfName = e.getValue();
            String origWithSig = key.substring(key.lastIndexOf('.') + 1);
            String origName = origWithSig;
            int p = origWithSig.indexOf('(');
            if (p >= 0) origName = origWithSig.substring(0, p);
            methodMapInv.put(obfName, origName);
        }
        Map<String, String> fieldMapInv = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> e : mapping.fieldMap.entrySet()) {
            String key = e.getKey(); // originalFqn.fieldName
            String obfName = e.getValue();
            String origName = key.substring(key.lastIndexOf('.') + 1);
            fieldMapInv.put(obfName, origName);
        }
        // Traverse all Java files under srcRoot
        java.util.List<File> javaFiles = new java.util.ArrayList<File>();
        collectJavaFiles(srcRoot, javaFiles);
        for (File javaFile : javaFiles) {
            String content = readFile(javaFile);
            String updated = content;
            // Replace method invocations and declarations
            for (Map.Entry<String, String> m : methodMapInv.entrySet()) {
                String obf = m.getKey();
                String orig = m.getValue();
                // match word boundary + obf + optional spaces before '('
                updated = updated.replaceAll("\\b" + obf + "\\s*\\(", orig + "(");
            }
            // Replace field references
            for (Map.Entry<String, String> f : fieldMapInv.entrySet()) {
                String obf = f.getKey();
                String orig = f.getValue();
                updated = updated.replaceAll("\\b" + obf + "\\b", orig);
            }
            if (!updated.equals(content)) {
                writeFile(javaFile, updated);
                System.out.println("Updated members in " + javaFile.getPath());
            }
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
    /**
     * Recursively collect all .java files under a directory.
     */
    private static void collectJavaFiles(File dir, java.util.List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                collectJavaFiles(f, list);
            } else if (f.getName().endsWith(".java")) {
                list.add(f);
            }
        }
    }
}