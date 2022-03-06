package cz.cvut.fit.honysdan.bm.db.utils.service;

import cz.cvut.fit.honysdan.bm.db.utils.dto.ArticleDTO;
import cz.cvut.fit.honysdan.bm.db.utils.entity.Article;
import cz.cvut.fit.honysdan.bm.db.utils.entity.Term;
import cz.cvut.fit.honysdan.bm.db.utils.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

//----------------------------------------------------------------------------------------------------------------------
// TO DTO
//----------------------------------------------------------------------------------------------------------------------

    private ArticleDTO toDTO(Article article) {
        return new ArticleDTO(
                article.getId(),
                article.getName(),
                article.getLink(),
                article.getArticle(),
                article.getTerms().stream().map(Term::getId).collect(Collectors.toList()));
    }

    private Optional<ArticleDTO> toDTO(Optional<Article> article){
        if (article.isEmpty())
            return Optional.empty();
        return Optional.of(toDTO(article.get()));
    }

//----------------------------------------------------------------------------------------------------------------------
// Service logic
//----------------------------------------------------------------------------------------------------------------------

    // GET ALL ARTICLES
    public List<ArticleDTO> getAll(int vaadinOffset, int limit, int pageSize) {
        List<ArticleDTO> ret = new ArrayList<>();
        int realOffset = vaadinOffset/pageSize;

        for (int i = 0; i < (int)Math.ceil((float)limit/pageSize); i++) {
            Pageable page = PageRequest.of(realOffset, pageSize);
            ret.addAll(articleRepository.findAll(page).stream().map(this::toDTO).collect(Collectors.toList()));
            realOffset = realOffset + 1;
        }

        return ret;
    }

    // GET COUNT OF ARTICLES
    public int getArticleCount() {
        return articleRepository.getCount();
    }

    // GET ARTICLE BY ITS ID
    public Optional<ArticleDTO> getByID(int id) {
        return toDTO(articleRepository.findById(id));
    }

    // GET LIST OF ARTICLE IDS (with index)
    public List<Integer> getIDsOfArticlesWithIndex(String term) {
        return articleRepository.getIDsOfArticlesWithIndex(term);
    }

    // GET LIST OF ARTICLE IDS (without index)
    public List<Integer> getIDsOfArticlesWithNoIndex(String term) {
        return articleRepository.getIDsOfArticlesWithNoIndex(term);
    }

    // GET LIST OF ARTICLES BY IDS
    public List<ArticleDTO> getByIds(int vaadinOffset, int limit, int pageSize, List<Integer> ids) {
        List<ArticleDTO> ret = new ArrayList<>();
        int realOffset = vaadinOffset/pageSize;

        for (int i = 0; i < (int)Math.ceil((float)limit/pageSize); i++) {
            Pageable page = PageRequest.of(realOffset, pageSize);
            ret.addAll(articleRepository.findAllById(ids, page).stream().map(this::toDTO).collect(Collectors.toList()));
            realOffset = realOffset + 1;
        }

        return ret;
    }

}
