package cz.cvut.fit.honysdan.bm.scraper.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class Saver {
    private final File dir;

    public Saver(String path) throws IOException {
        // Check or create an output folder
        dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Cannot create output directory");
            } else {
                log.info("Output directory was created");
            }
        } else {
            log.info("Output directory exists");
        }
    }

    public void saveFile(ScrapedData data) throws IOException {
        // Check if text file exists
        File output = new File(dir.getPath(), data.getTitle() + ".txt");
        if (!output.createNewFile()) {
            throw new IOException("File with name " + data.getTitle() + ".txt already exists");
        }

        // Write parsed data into a text file
        try (FileWriter myWriter = new FileWriter(output.getPath()))  {
            myWriter.write(data.getTitle() + '\n');
            myWriter.write(data.getUrl() + '\n');
            for ( String i : data.getParagraphs()) {
                myWriter.write(i + '\n');
            }
        }

        log.info("Saved - " + data.getTitle());
    }
}
