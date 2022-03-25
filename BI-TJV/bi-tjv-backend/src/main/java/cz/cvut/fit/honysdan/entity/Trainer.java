package cz.cvut.fit.honysdan.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "trainer")
@Table(name = "trainer")
public class Trainer {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "number")
    private int number;
    @Column(name = "address", length = 100)
    private String address;

    // Constructors
    public Trainer() {
    }

    public Trainer(String name, int number, String address) {
        this.name = name;
        this.number = number;
        this.address = address;
    }

    // Getters
    public int    getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int    getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int hashCode() {return Objects.hash(id, name, number, address); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        Trainer trainer = (Trainer) obj;
        return id == trainer.id && Objects.equals(name, trainer.name)
                && Objects.equals(number, trainer.number)
                && Objects.equals(address, trainer.address);
    }
}
