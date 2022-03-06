package cz.cvut.fit.honysdan.bm.scraper.utils;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Scraper {
    private final String url;

    public Scraper(String url) {
        this.url = url;
    }

    private String cleanParagraph(Element element) {
        element.select("a[href^=#cite_]").remove();
        return element.text().trim();
    }

    private List<String> parseParagraphs(Elements elements) {
        List<String> paragraphs = new ArrayList<>();

        // Go through elements and find "infobox"
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).hasClass("infobox")) {

                // Get paragraphs
                for (int j = i; j < elements.size(); j++) {
                    if (elements.get(j).is("p")) {
                        var paragraph = cleanParagraph(elements.get(j));
                        if (!paragraph.isEmpty()) {
                            paragraphs.add(paragraph);
                        }
                    }
                }

                break;

            }
        }

        return paragraphs;
    }

    public ScrapedData scrape() throws IOException {
        // Getting the content of page
        Connection.Response response = Jsoup.connect(url).followRedirects(true).execute();
        Document doc = Jsoup.connect(response.url().toString()).get();

        // Selecting main elements
        Elements elements = doc.select("h1.firstHeading");

        // Get title and paragraphs
        String title = elements.get(0).text();
        elements = doc.select("div.mw-parser-output > *");
        List<String> paragraphs = parseParagraphs(elements);

        log.info("Scraped - " + title);
        return new ScrapedData(title, paragraphs, paragraphs.size(), response.url().toString());
    }
}
