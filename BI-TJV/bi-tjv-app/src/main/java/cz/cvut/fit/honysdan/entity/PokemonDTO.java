package cz.cvut.fit.honysdan.entity;

import java.util.List;
import java.util.Objects;

public class PokemonDTO {

    private Integer id;
    private String name;
    private String type;
    private List<Integer> moves;
    private Integer trainer;

    public PokemonDTO() {}

    public PokemonDTO(int id, String name, String type, List<Integer> moves, Integer trainer) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.moves = moves;
        this.trainer = trainer;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getTrainer() {
        return trainer;
    }

    public List<Integer> getMoves() {
        return moves;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMovesIds(List<Integer> moves) {
        this.moves = moves;
    }

    public void setTrainerId(Integer trainer) {
        this.trainer = trainer;
    }

    @Override
    public int hashCode() {return Objects.hash(id, name, type, moves, trainer); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        PokemonDTO pokemonDTO = (PokemonDTO) obj;
        return id == pokemonDTO.id && Objects.equals(name, pokemonDTO.name)
                                   && Objects.equals(type, pokemonDTO.type)
                                   && Objects.equals(trainer, pokemonDTO.trainer)
                                   && Objects.equals(moves, pokemonDTO.moves);
    }
}
