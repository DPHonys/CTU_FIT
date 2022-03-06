package cz.cvut.fit.honysdan.bm.scraper;

import cz.cvut.fit.honysdan.bm.scraper.utils.Saver;
import cz.cvut.fit.honysdan.bm.scraper.utils.ScrapedData;
import cz.cvut.fit.honysdan.bm.scraper.utils.Scraper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Main {
    private static final String URL = "https://en.wikipedia.org/wiki/Special:Random";
    private static final String PATH = "./out";

    public static void main(String[] args) {
        Scraper scraper = new Scraper(URL);
        Saver saver = null;
        try {
            saver = new Saver(PATH);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            System.exit(1);
        }

        int count = 0;
        ScrapedData data;
        while ( count < 15000 ) {
            try {
                // Scrape
                data = scraper.scrape();

                // Check if there are at least 3 paragraphs
                if ( data.getCount() < 3 ) {
                    continue;
                }

                // Save
                saver.saveFile(data);
            } catch (IOException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                count--;
            }

            count++;
        }
        log.info("Successfully scraped " + count + " pages");
    }
}
