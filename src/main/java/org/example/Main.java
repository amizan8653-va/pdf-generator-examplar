package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.example.model.corprecord.CorpRecord;
import org.example.pdf.PdfBoxGenerator;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        CorpRecord corpRecord = loadCorpRecord();
        PdfBoxGenerator generator = new PdfBoxGenerator();
        byte[] bytes = generator.generatePdfForTemplate(corpRecord, "Some footer. Hello World!");
        writeToFile(bytes);

    }

    @SneakyThrows
    private static void writeToFile(byte[] bytes) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URI resource = classLoader.getResource("./").toURI();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("u-M-d k-m-s")
                .withZone(ZoneOffset.UTC);
        FileOutputStream fos = new FileOutputStream(resource.getPath() + "/pdf-example-" + formatter.format(Instant.now()) + ".pdf");
        fos.write(bytes);
    }

    @SneakyThrows
    static CorpRecord loadCorpRecord() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream is = classLoader.getResourceAsStream("corpRecord.json");
        assert is != null;
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        String content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        CorpRecord corpRecord = mapper.readValue(content, CorpRecord.class);
        corpRecord.setDateFormatterInput(new SimpleDateFormat("MMddyyyy", Locale.US));
        corpRecord.setDateFormatterOutput(new SimpleDateFormat("MMMM dd, yyyy", Locale.US));
        return corpRecord;
    }
}