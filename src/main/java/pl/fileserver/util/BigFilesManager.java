package pl.fileserver.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//TODO change offset place from table to temp file
public class BigFilesManager {

    private static final int MAX_LINE_LIMIT = 50_000_000;
    private final Logger log = LoggerFactory.getLogger(getClass());

    private int linesCount;
    private long[] fileLineOffset = new long[MAX_LINE_LIMIT];
    private String fileName;

    @Autowired
    public BigFilesManager(String fileName) {
        this.fileName = fileName;
    }

    @PostConstruct
    public void init() throws IOException {
        if (fileName == null) {
            return;
        }

        log.info("Processing file {}", fileName);

        int currentLine = 0;
        fileLineOffset[currentLine] = 0;

        for(Long nextLineOffset = getNextLineOffset(0); nextLineOffset != null;
            nextLineOffset = getNextLineOffset(fileLineOffset[currentLine])) {

            currentLine++;
            fileLineOffset[currentLine] = nextLineOffset;
        }

        linesCount = currentLine;
    }

    private Long getNextLineOffset(long currentOffset) throws IOException {

        try (RandomAccessFile file = getFile()) {
            file.seek(currentOffset);
            return file.readLine() == null ? null : file.getFilePointer();
        }

    }

    public String getLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= linesCount) {
            return null;
        }

        try (RandomAccessFile file = getFile()) {

            file.seek(fileLineOffset[lineNumber]);
            return file.readLine();

        } catch (IOException e) {
            log.error("error occurred", e);
            return null;
        }

    }

    private RandomAccessFile getFile() throws FileNotFoundException {
        return new RandomAccessFile(fileName, "r");
    }

}
