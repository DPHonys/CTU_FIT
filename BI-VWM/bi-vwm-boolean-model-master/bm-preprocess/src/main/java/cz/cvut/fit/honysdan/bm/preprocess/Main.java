package cz.cvut.fit.honysdan.bm.preprocess;

import cz.cvut.fit.honysdan.bm.preprocess.utils.Processor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class Main {
    public static final String FOLDER_PATH = "../bm-scraper/out";
    public static final String STOPWORDS_PATH = "./src/main/resources/stopwords.txt";

    public static void main(String[] args) {
        try {
            // Load list of file names and list of stopwords
            File dir = new File(FOLDER_PATH);
            List<String> files = Arrays.stream(Objects.requireNonNull(dir.list()))
                    .filter(name -> name.contains(".txt")).collect(Collectors.toList());
            log.info("Total number of " + files.size() + " is going to be processed.");

            List<String> stopWords = Files.readAllLines(Paths.get(STOPWORDS_PATH));

            // Process the articles
            Processor processor = new Processor(files, stopWords);
            processor.run();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            System.exit(1);
        }
    }
}
