package cz.cvut.fit.honysdan.service;

import cz.cvut.fit.honysdan.dto.PokemonCreateDTO;
import cz.cvut.fit.honysdan.dto.PokemonDTO;
import cz.cvut.fit.honysdan.entity.Move;
import cz.cvut.fit.honysdan.entity.Pokemon;
import cz.cvut.fit.honysdan.entity.Trainer;
import cz.cvut.fit.honysdan.exception.*;
import cz.cvut.fit.honysdan.repository.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PokemonService {

    private final PokemonRepository pokemonRepository;
    private final TrainerService trainerService;
    private final MoveService moveService;

    @Autowired
    public PokemonService(PokemonRepository pokemonRepository, TrainerService trainerService, MoveService moveService) {
        this.pokemonRepository = pokemonRepository;
        this.trainerService = trainerService;
        this.moveService = moveService;
    }

//----------------------------------------------------------------------------------------------------------------------
// TO DTO
//----------------------------------------------------------------------------------------------------------------------

    private PokemonDTO toDTO(Pokemon pokemon) {
        return new PokemonDTO(pokemon.getId(),
                              pokemon.getName(),
                              pokemon.getType(),
                              pokemon.getMoves().stream().map(Move::getId).collect(Collectors.toList()),
                              pokemon.getTrainer() == null ? null : pokemon.getTrainer().getId());
    }

//----------------------------------------------------------------------------------------------------------------------
// CRUD
//----------------------------------------------------------------------------------------------------------------------

    // CREATE
    public PokemonDTO create(PokemonCreateDTO pokemonCreateDTO) {
        if (pokemonCreateDTO.getName() == null) {
            throw new NullValueException("Value 'name' can't be null");
        } else if (pokemonCreateDTO.getType() == null) {
            throw new NullValueException("Value 'type' can't be null");
        }
        // check if trainer exists
        Trainer trainer = pokemonCreateDTO.getTrainerId() == null ? null :
                trainerService.getById(pokemonCreateDTO.getTrainerId());

        // check if all moves exist
        List<Move> moves = moveService.getAllByIds(pokemonCreateDTO.getMovesIds());
        if (moves.size() != pokemonCreateDTO.getMovesIds().size()) {
            throw new EntityNotFoundException("Not all provided moves were found");
        }

        // creation
        Pokemon pokemon = new Pokemon(pokemonCreateDTO.getName(), pokemonCreateDTO.getType(), moves, trainer);
        return toDTO(pokemonRepository.save(pokemon));
    }

    // GET BY ID
    public Pokemon getById(int id) {
        return pokemonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such pokemon with id " + id));
    }

    public PokemonDTO getByIdAsDTO(int id) {
        return toDTO(getById(id));
    }

    // GET ALL
    public List<PokemonDTO> getAll() {
        return pokemonRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // UPDATE
    public PokemonDTO update(int id, PokemonCreateDTO pokemonDTO) {
        if (pokemonDTO.getName() == null) {
            throw new NullValueException("Value 'name' can't be null");
        } else if (pokemonDTO.getType() == null) {
            throw new NullValueException("Value 'type' can't be null");
        }

        // look if it even exists
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such pokemon with id " + id));

        // check trainer
        Trainer trainer = pokemonDTO.getTrainerId() == null ? null :
                trainerService.getById(pokemonDTO.getTrainerId());

        // check moves
        List<Move> moves = moveService.getAllByIds(pokemonDTO.getMovesIds());
        if (moves.size() != pokemonDTO.getMovesIds().size()) {
            throw new EntityNotFoundException("Not all provided moves were found");
        }

        // update
        pokemon.setName(pokemonDTO.getName());
        pokemon.setType(pokemonDTO.getType());
        pokemon.setMoves(moves);
        pokemon.setTrainer(trainer);
        return toDTO(pokemon);
    }

    // DELETE
    public void deleteById(int id) {
        pokemonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No such pokemon with id " + id));
        pokemonRepository.deleteById(id);
    }

//----------------------------------------------------------------------------------------------------------------------
// EXTRA
//----------------------------------------------------------------------------------------------------------------------

    // GET LIST BY NAME
    public List<PokemonDTO> getByName(String name) {
        return pokemonRepository.findByName(name).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ADD MOVE
    public PokemonDTO addMove(int pokemonId, int moveId) {
        // check if pokemon with id exists
        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                        .orElseThrow(() -> new EntityNotFoundException("No such pokemon with id " + pokemonId));

        // check if move with id exist
        Move move = moveService.getById(moveId);

        // check if already have
        if ( pokemon.getMoves().contains(move) ) {
            throw new MoveKnowledgeException("Pokemon already knows move with id " + moveId);
        }

        // add
        pokemon.getMoves().add(move);
        return toDTO(pokemon);
    }

    // REMOVE MOVE
    public PokemonDTO removeMove(int pokemonId, int moveId) {
        // check if pokemon with id exists
        Pokemon pokemon = pokemonRepository.findById(pokemonId)
                .orElseThrow(() -> new EntityNotFoundException("No such pokemon with id " + pokemonId));

        // check if move with id exist
        Move move = moveService.getById(moveId);

        // check if it has that move
        if ( !pokemon.getMoves().contains(move) ) {
            throw new MoveKnowledgeException("Pokemon doesn't know move with id " + moveId);
        }

        // remove
        pokemon.getMoves().remove(move);
        return toDTO(pokemon);
    }
}
