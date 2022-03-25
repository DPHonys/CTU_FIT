package cz.cvut.fit.honysdan.repository;

import cz.cvut.fit.honysdan.entity.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    // returns list of pokemon with matching name
    @Query("select p from pokemon p where lower(p.name) = lower(:name)")
    List<Pokemon> findByName(String name);
}
