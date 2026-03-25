package com.example.demo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SubmissionStorageService {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path CSV_PATH = DATA_DIR.resolve("submissions.csv");
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss");

    // Basic CSV escaping: wrap in quotes if needed and escape quotes
    private String csv(String s) {
        if (s == null) {
            return "";
        }
        String cleaned = s.replace("\r", " ").replace("\n", " ").trim();
        boolean needsQuotes = cleaned.contains(",") || cleaned.contains("\"");
        if (cleaned.contains("\"")) {
            cleaned = cleaned.replace("\"", "\"\"");
        }
        return needsQuotes ? "\"" + cleaned + "\"" : cleaned;
    }

    public synchronized void save(QuoteRequest quote) {
        try {
            Files.createDirectories(DATA_DIR);

            // Add header if file doesn’t exist yet
            if (Files.notExists(CSV_PATH)) {
                String header = "timestamp,name,email,phone,service,message\n";
                Files.writeString(CSV_PATH, header, StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            }

            String ts = LocalDateTime.now().format(TS_FORMAT);

            String line = String.join(",", this.csv(ts), this.csv(quote.getName()),
                    this.csv(quote.getEmail()), this.csv(quote.getPhone()),
                    this.csv(quote.getService()), this.csv(quote.getMessage())) + "\n";

            Files.writeString(CSV_PATH, line, StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);

        } catch (IOException e) {
            // If saving fails, we want to know immediately
            throw new RuntimeException(
                    "Failed to save submission to CSV: " + CSV_PATH.toAbsolutePath(), e);
        }
    }

    public synchronized List<SubmissionRecord> readAll() {
        try {
            if (Files.notExists(CSV_PATH)) {
                return List.of();
            }

            List<String> lines = Files.readAllLines(CSV_PATH, StandardCharsets.UTF_8);
            if (lines.size() <= 1) {
                return List.of(); // header only
            }

            List<SubmissionRecord> out = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) { // skip header
                String line = lines.get(i);
                List<String> cols = this.parseCsvLine(line);

                // Expect: timestamp,name,email,phone,service,message
                while (cols.size() < 6) {
                    cols.add("");
                }

                out.add(new SubmissionRecord(cols.get(0), cols.get(1), cols.get(2),
                        cols.get(3), cols.get(4), cols.get(5)));
            }
            // newest first (CSV is oldest->newest); reverse it
            java.util.Collections.reverse(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read submissions from CSV: " + CSV_PATH.toAbsolutePath(),
                    e);
        }
    }

    /**
     * Minimal CSV parser for one line: supports commas + quoted fields +
     * escaped quotes ("")
     */
    private List<String> parseCsvLine(String line) {
        List<String> cols = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        cur.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    cols.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        cols.add(cur.toString());
        return cols;
    }

}
