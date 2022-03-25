package cz.cvut.fit.honysdan;

import cz.cvut.fit.honysdan.dto.*;
import cz.cvut.fit.honysdan.entity.Move;
import cz.cvut.fit.honysdan.entity.Pokemon;
import cz.cvut.fit.honysdan.entity.Trainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestData {
//----------------------------------------------------------------------------------------------------------------------
// Trainers
//----------------------------------------------------------------------------------------------------------------------
    public static Trainer trainer1 = new Trainer("Ash Ketchum", 123, "Pallet Town");
    public static Trainer trainer2 = new Trainer("Misty", 456, "Johto");
    public static Trainer trainer3 = new Trainer("Brock", 789, "Kanto");
    public static Trainer trainer4 = new Trainer("Max", 123, "Cerulean City");
    public static Trainer trainer5 = new Trainer("Chloe", 456, "CherryGrove City");

    public static List<Trainer> allTrainers = List.of(trainer1, trainer2, trainer3, trainer4, trainer5);
    public static List<Trainer> trainerWithName = List.of(trainer1);

//----------------------------------------------------------------------------------------------------------------------
// Moves
//----------------------------------------------------------------------------------------------------------------------
    public static Move move1 = new Move("Bolt Strike", "May paralyze opponent.");
    public static Move move2 = new Move("Slam", "");
    public static Move move3 = new Move("Aqua Tail", "");
    public static Move move4 = new Move("Blast Burn", "User must recharge next turn.");
    public static Move move5 = new Move("Cotton Guard", "Drastically raises user's Defense.");

    public static List<Move> allMoves = List.of(move1, move2, move3, move4, move5);

//----------------------------------------------------------------------------------------------------------------------
// Pokemons
//----------------------------------------------------------------------------------------------------------------------
    public static Pokemon pokemon1 = new Pokemon("Pikachu", "Electric", new ArrayList<>(Arrays.asList(move1, move2)), trainer1);
    public static Pokemon pokemon2 = new Pokemon("Bulbasaur", "Grass", new ArrayList<>(Arrays.asList(move2, move5)), trainer2);
    public static Pokemon pokemon3 = new Pokemon("Charmander", "Fire", List.of(move4), null);
    public static Pokemon pokemon4 = new Pokemon("Rattata", "Normal", List.of(move2), trainer3);
    public static Pokemon pokemon5 = new Pokemon("Squirtle", "Water", new ArrayList<>(Collections.singletonList(move3)), null);

    public static List<Pokemon> allPokemon = List.of(pokemon1, pokemon2, pokemon3, pokemon4, pokemon5);
    public static List<Pokemon> pokemonWithName = List.of(pokemon1);

//----------------------------------------------------------------------------------------------------------------------
// DTO
//----------------------------------------------------------------------------------------------------------------------
    public static PokemonCreateDTO createDTOInstance(Pokemon pokemon) {
        return new PokemonCreateDTO(pokemon.getName(),
                pokemon.getType(),
                pokemon.getMoves().stream().map(Move::getId).collect(Collectors.toList()),
                pokemon.getTrainer() == null ? null : pokemon.getTrainer().getId()
        );
    }

    public static PokemonDTO toDTO(Pokemon pokemon) {
        return new PokemonDTO(pokemon.getId(),
                pokemon.getName(),
                pokemon.getType(),
                pokemon.getMoves().stream().map(Move::getId).collect(Collectors.toList()),
                pokemon.getTrainer() == null ? null : pokemon.getTrainer().getId());
    }

    public static TrainerCreateDTO createDTOInstance(Trainer trainer) {
        return new TrainerCreateDTO(trainer.getName(),
                trainer.getNumber(),
                trainer.getAddress());
    }

    public static TrainerDTO toDTO(Trainer trainer) {
        return new TrainerDTO(trainer.getId(),
                trainer.getName(),
                trainer.getNumber(),
                trainer.getAddress());
    }

    public static MoveCreateDTO createDTOInstance(Move move) {
        return new MoveCreateDTO(move.getName(),
                move.getDescription());
    }

    public static MoveDTO toDTO(Move move) {
        return new MoveDTO(move.getId(),
                move.getName(),
                move.getDescription());
    }
}
