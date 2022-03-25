package cz.cvut.fit.honysdan.entity;

import java.util.Objects;

public class TrainerDTO {

    private Integer id;
    private String name;
    private Integer number;
    private String address;

    public TrainerDTO() {}

    public TrainerDTO(Integer id, String name, Integer number, String address) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(Integer number) {
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
        TrainerDTO trainerDTO = (TrainerDTO) obj;
        return id == trainerDTO.id && Objects.equals(name, trainerDTO.name)
                                   && Objects.equals(number, trainerDTO.number)
                                   && Objects.equals(address, trainerDTO.address);
    }
}
