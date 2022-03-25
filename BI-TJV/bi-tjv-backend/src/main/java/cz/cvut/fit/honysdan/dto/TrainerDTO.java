package cz.cvut.fit.honysdan.dto;

import java.util.Objects;

public class TrainerDTO {

    private final int id;
    private final String name;
    private final int number;
    private final String address;

    public TrainerDTO(int id, String name, int number, String address) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int hashCode() {return Objects.hash(id, name, number, address); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        TrainerDTO trainerDTO = (TrainerDTO) obj;
        return id == trainerDTO.id && Objects.equals(name, trainerDTO.name)
                                   && Objects.equals(number, trainerDTO.number)
                                   && Objects.equals(address, trainerDTO.address);
    }
}
