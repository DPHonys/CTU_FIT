package cz.cvut.fit.honysdan.repository;

import cz.cvut.fit.honysdan.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    // returns list of trainers with matching name
    @Query("select t from trainer t where lower(t.name) = lower(:name)")
    List<Trainer> findByName(String name);
}
