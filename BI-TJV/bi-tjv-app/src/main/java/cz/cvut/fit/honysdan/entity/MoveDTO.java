package cz.cvut.fit.honysdan.entity;

import java.util.Objects;

public class MoveDTO {

    private Integer id;
    private String name;
    private String description;

    public MoveDTO() {}

    public MoveDTO(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
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
