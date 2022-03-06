package cz.cvut.fit.honysdan.bm.db.utils.service;

import cz.cvut.fit.honysdan.bm.db.utils.dto.TermDTO;
import cz.cvut.fit.honysdan.bm.db.utils.entity.Article;
import cz.cvut.fit.honysdan.bm.db.utils.entity.Term;
import cz.cvut.fit.honysdan.bm.db.utils.repository.TermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TermService {

    private final TermRepository termRepository;

    @Autowired
    public TermService(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

//----------------------------------------------------------------------------------------------------------------------
// TO DTO
//----------------------------------------------------------------------------------------------------------------------

    private TermDTO toDTO(Term term) {
        return new TermDTO(
                term.getId(),
                term.getTerm(),
                term.getCount(),
                term.getArticles().stream().map(Article::getId).collect(Collectors.toList()));
    }

//----------------------------------------------------------------------------------------------------------------------
// Service logic
//----------------------------------------------------------------------------------------------------------------------

    // GET ALL TERMS
    public List<TermDTO> getAll(int vaadinOffset, int limit, int pageSize) {
        List<TermDTO> ret = new ArrayList<>();
        int realOffset = vaadinOffset/pageSize;

        for (int i = 0; i < (int)Math.ceil((float)limit/pageSize); i++) {
            Pageable page = PageRequest.of(realOffset, pageSize);
            ret.addAll(termRepository.findAll(page).stream().map(this::toDTO).collect(Collectors.toList()));
            realOffset = realOffset + 1;
        }

        return ret;
    }

    // GET COUNT OF TERMS
    public int getArticleCount() {
        return termRepository.getCount();
    }
}
