package cz.cvut.fit.honysdan.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "move")
@Table(name = "move")
public class Move {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name", length = 30, nullable = false, unique = true)
    private String name;
    @Column(name = "description", length = 200)
    private String description;

    // Constructors
    public Move() {
    }

    public Move(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {return Objects.hash(id, name, description); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        Move move = (Move) obj;
        return id == move.id && Objects.equals(name, move.name)
                             && Objects.equals(description, move.description);
    }
}
