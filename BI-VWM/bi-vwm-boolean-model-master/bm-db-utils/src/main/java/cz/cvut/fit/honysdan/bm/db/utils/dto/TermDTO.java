package cz.cvut.fit.honysdan.bm.db.utils.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class TermDTO {

    private final int id;
    private final String term;
    private final int count;

    private final List<Integer> articles;

}