package cz.cvut.fit.honysdan.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity(name = "pokemon")
@Table(name = "pokemon")
public class Pokemon {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;
    @Column(name = "type", length = 30, nullable = false)
    private String type;

    // Pokemon's moves
    @ManyToMany
    @JoinTable( name = "pokemon_move",
                joinColumns = @JoinColumn(name = "pokemon_id"),
                inverseJoinColumns = @JoinColumn(name = "move_id")
                )
    private List<Move> moves;

    // Pokemon's trainer
    @ManyToOne
    @JoinColumn(name="trainer_id")
    private Trainer trainer;

    // Constructors
    public Pokemon() {
    }

    public Pokemon(String name, String type, List<Move> moves, Trainer trainer) {
        this.name = name;
        this.type = type;
        this.moves = moves;
        this.trainer = trainer;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public List<Move> getMoves() {
        return moves;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    @Override
    public int hashCode() {return Objects.hash(id, name, type, moves, trainer); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        Pokemon pokemon = (Pokemon) obj;
        return id == pokemon.id && Objects.equals(name, pokemon.name)
                && Objects.equals(type, pokemon.type)
                && Objects.equals(trainer, pokemon.trainer)
                && Objects.equals(moves, pokemon.moves);
    }
}
