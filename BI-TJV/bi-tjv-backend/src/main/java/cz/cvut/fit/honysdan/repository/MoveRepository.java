package cz.cvut.fit.honysdan.repository;

import cz.cvut.fit.honysdan.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MoveRepository extends JpaRepository<Move, Integer> {
    // returns move with matching name
    @Query("select m from move m where lower(m.name) = lower(:name)")
    Optional<Move> findByName(String name);
}
