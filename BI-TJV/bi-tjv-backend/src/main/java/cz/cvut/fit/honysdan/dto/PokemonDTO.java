package cz.cvut.fit.honysdan.dto;

import java.util.List;
import java.util.Objects;

public class PokemonDTO {

    private final int id;
    private final String name;
    private final String type;
    private final List<Integer> movesIds;
    private final Integer trainerId;

    public PokemonDTO(int id, String name, String type, List<Integer> moves, Integer trainer) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.movesIds = moves;
        this.trainerId = trainer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getTrainerId() {
        return trainerId;
    }

    public List<Integer> getMovesIds() {
        return movesIds;
    }
    
    @Override
    public int hashCode() {return Objects.hash(id, name, type, movesIds, trainerId); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        PokemonDTO pokemonDTO = (PokemonDTO) obj;
        return id == pokemonDTO.id && Objects.equals(name, pokemonDTO.name)
                                   && Objects.equals(type, pokemonDTO.type)
                                   && Objects.equals(trainerId, pokemonDTO.trainerId)
                                   && Objects.equals(movesIds, pokemonDTO.movesIds);
    }
}
