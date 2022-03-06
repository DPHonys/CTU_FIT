package cz.cvut.fit.honysdan.bm.db.utils.repository;

import cz.cvut.fit.honysdan.bm.db.utils.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query(value = "SELECT count(*) FROM article", nativeQuery = true)
    int getCount();

    @Query(value ="SELECT article_id FROM article_term " +
            "WHERE term_id = (SELECT id FROM term WHERE term = :term)",
            nativeQuery = true)
    List<Integer> getIDsOfArticlesWithIndex(@Param("term") String term);

    @Query(value ="SELECT article_id FROM article_term USE INDEX ()" +
            "WHERE term_id = (SELECT id FROM term USE INDEX () WHERE term = :term)",
            nativeQuery = true)
    List<Integer> getIDsOfArticlesWithNoIndex(@Param("term") String term);

    @Query(value ="SELECT * FROM article WHERE article.id IN :ids",
            countQuery = "SELECT count(*) FROM article",
            nativeQuery = true)
    Page<Article> findAllById(@Param("ids") List<Integer> ids, Pageable page);
}
