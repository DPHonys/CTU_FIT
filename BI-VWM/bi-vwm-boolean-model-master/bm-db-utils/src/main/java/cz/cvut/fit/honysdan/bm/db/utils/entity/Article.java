package cz.cvut.fit.honysdan.bm.db.utils.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name = "article")
@Table(name = "article",
        indexes = { @Index(name = "IDX_ARTICLE", columnList = "id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;
    @Column(name="link", nullable = false, unique = true)
    private String link;
    @Column(name="article", nullable = false, columnDefinition = "text")
    private String article;

    @ManyToMany
    @JoinTable( name = "article_term",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "term_id")
    )
    private List<Term> terms;

    @Override
    public int hashCode() {return Objects.hash(id, name, link, article, terms); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        Article articleObj = (Article) obj;
        return Objects.equals(id, articleObj.id)
                && Objects.equals(name, articleObj.name)
                && Objects.equals(link, articleObj.link)
                && Objects.equals(article, articleObj.article)
                && Objects.equals(terms, articleObj.terms);
    }
}
