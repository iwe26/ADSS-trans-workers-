package hrmanagement.dal.dto;

import java.util.Objects;

public class RoleDTO {
    private int id;      // PK
    private String name;       // Unique role name

    public RoleDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Constructor for new roles (ID assigned by DB)
     */
    public RoleDTO(String name) {
        this.id = 0;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoleDTO)) return false;
        RoleDTO roleDTO = (RoleDTO) o;
        return id == roleDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
