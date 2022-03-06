package cz.cvut.fit.honysdan.bm.db.utils.repository;

import cz.cvut.fit.honysdan.bm.db.utils.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TermRepository extends JpaRepository<Term, Integer> {

    @Query(value = "select count(*) from term", nativeQuery = true)
    int getCount();
}