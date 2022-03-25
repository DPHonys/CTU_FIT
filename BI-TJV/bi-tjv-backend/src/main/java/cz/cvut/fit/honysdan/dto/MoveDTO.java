package cz.cvut.fit.honysdan.dto;

import java.util.Objects;

public class MoveDTO {

    private final int id;
    private final String name;
    private final String description;

    public MoveDTO(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {return Objects.hash(id, name, description); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return  false;
        MoveDTO moveDTO = (MoveDTO) obj;
        return id == moveDTO.id && Objects.equals(name, moveDTO.name)
                                && Objects.equals(description, moveDTO.description);
    }
}
