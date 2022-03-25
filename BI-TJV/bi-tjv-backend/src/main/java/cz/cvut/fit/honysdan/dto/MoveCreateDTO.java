package cz.cvut.fit.honysdan.dto;

public class MoveCreateDTO {

    private final String name;
    private final String description;

    public MoveCreateDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
