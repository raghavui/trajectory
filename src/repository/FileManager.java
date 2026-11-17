package repository;

import model.Persistable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all reading and writing of text files.
 *
 * Every record is stored as ONE line, with fields separated by the "|" character:
 *     riya@gmail.com|JAVA-B01|COMPLETED
 *
 * Special characters inside a value are "escaped" so they cannot break the format:
 *     \  is written as  \\
 *     |  is written as  \p
 *     new line is written as \n
 * This way a goal description like "Learn A|B\nthen C" is saved and loaded safely.
 */
public class FileManager {

    private static final char SEPARATOR = '|';

    private final File dataDirectory;

    public FileManager(String dataDirectoryPath) {
        this.dataDirectory = new File(dataDirectoryPath);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            System.err.println("Could not create data folder: " + dataDirectory.getAbsolutePath());
        }
    }

    public File getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Reads every non-empty line of a file and splits it into fields.
     * If the file does not exist yet, an empty list is returned (first run of the app).
     */
    public List<String[]> readRecords(String fileName) throws IOException {
        List<String[]> records = new ArrayList<>();
        File file = new File(dataDirectory, fileName);
        if (!file.exists()) {
            return records;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(splitLine(line));
                }
            }
        }
        return records;
    }

    /**
     * Adds one record at the END of the file without touching existing lines.
     * Used when a new student registers, so old students are never overwritten.
     */
    public void appendRecord(String fileName, Persistable item) throws IOException {
        File file = new File(dataDirectory, fileName);
        // FileWriter(file, charset, true) -> "true" means append mode
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true)))) {
            writer.println(joinFields(item.toRecord()));
        }
    }

    /**
     * Rewrites the whole file with the given objects (used after update/delete).
     */
    public void writeRecords(String fileName, List<? extends Persistable> items) throws IOException {
        List<String[]> rows = new ArrayList<>();
        for (Persistable item : items) {
            rows.add(item.toRecord());
        }
        writeRawRecords(fileName, rows);
    }

    /**
     * Rewrites the whole file with the given rows of fields.
     *
     * Safety: data is first written to a temporary file and then moved over the
     * real file. If the program crashes while writing, the old file is still intact.
     */
    public void writeRawRecords(String fileName, List<String[]> rows) throws IOException {
        File target = new File(dataDirectory, fileName);
        File temp = new File(dataDirectory, fileName + ".tmp");
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(temp, StandardCharsets.UTF_8)))) {
            for (String[] row : rows) {
                writer.println(joinFields(row));
            }
        }
        Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Joins field values into one line, escaping special characters.
     */
    public static String joinFields(String[] fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line.append(SEPARATOR);
            }
            line.append(escape(fields[i]));
        }
        return line.toString();
    }

    /**
     * Splits one line back into fields, turning escape codes back into real characters.
     */
    public static String[] splitLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '\\' && i + 1 < line.length()) {
                char next = line.charAt(++i);
                switch (next) {
                    case 'p':
                        current.append('|');
                        break;
                    case 'n':
                        current.append('\n');
                        break;
                    case 'r':
                        current.append('\r');
                        break;
                    default:
                        current.append(next);   // handles "\\" -> "\"
                }
            } else if (ch == SEPARATOR) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
