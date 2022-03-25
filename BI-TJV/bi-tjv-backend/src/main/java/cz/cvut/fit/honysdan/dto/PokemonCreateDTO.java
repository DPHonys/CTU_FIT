package cz.cvut.fit.honysdan.dto;

import java.util.List;

public class PokemonCreateDTO {

    private final String name;
    private final String type;
    private final List<Integer> moves;
    private final Integer trainer;

    public PokemonCreateDTO(String name, String type, List<Integer> moves, Integer trainer) {
        this.name = name;
        this.type = type;
        this.moves = moves;
        this.trainer = trainer;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getTrainerId() {
        return trainer;
    }

    public List<Integer> getMovesIds() {
        return moves;
    }
}
