package cz.cvut.fit.honysdan.bm.db.utils.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class ArticleDTO {

    private final int id;
    private final String name;
    private final String link;
    private final String article;

    private final List<Integer> terms;
}