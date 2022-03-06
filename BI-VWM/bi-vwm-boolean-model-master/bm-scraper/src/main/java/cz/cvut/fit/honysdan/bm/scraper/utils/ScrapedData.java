package cz.cvut.fit.honysdan.bm.scraper.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ScrapedData {
    private final String title;
    private final List<String> paragraphs;
    private final Integer count;
    private final String url;
}
