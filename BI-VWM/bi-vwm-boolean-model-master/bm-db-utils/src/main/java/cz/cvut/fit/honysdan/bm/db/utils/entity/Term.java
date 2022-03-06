package cz.cvut.fit.honysdan.bm.db.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name = "term")
@Table(name = "term",
    indexes = { @Index(name = "IDX_TERM", columnList = "term")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="term", nullable = false, unique = true)
    private String term;
    @Column(name="count", nullable = false)
    private int count;

    @ManyToMany(mappedBy = "terms")
    List<Article> articles;

    @Override
    public int hashCode() {return Objects.hash(id, term, count, articles); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        Term termObj = (Term) obj;
        return Objects.equals(id, termObj.id)
                && Objects.equals(term, termObj.term)
                && Objects.equals(count, termObj.count)
                && Objects.equals(articles, termObj.articles);
    }
}
