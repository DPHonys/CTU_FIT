package cz.cvut.fit.honysdan.dto;

public class TrainerCreateDTO {

    private final String name;
    private final int number;
    private final String address;

    public TrainerCreateDTO(String name, int number, String address) {
        this.name = name;
        this.number = number;
        this.address = address;
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
}
