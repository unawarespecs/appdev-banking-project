package io.github.unawarespecs.bankapp.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class Administrator extends User {

    public Administrator() {
        // Default Here
        super();
        this.setRole("Admin");
        this.setId(0); // ID CANNOT BE 0 THIS WILL BE CHANGED LATER ON IF NOT EXPLICITEDLY IMPLEMENTED
    }

    public Administrator(UUID uuid, String role, boolean isAdmin, String username, String password, int id) {
        // this is used to call the User() constructor directly
        super(uuid, username,  role, isAdmin);
        this.setPassword(password);
        this.setRole(role);
        this.setId(id); // ID CANNOT BE 0 THIS WILL BE CHANGED LATER ON IF NOT EXPLICITEDLY IMPLEMENTED
    }

    @Override
    public String toString() {
        return String.format("Administrator[username=%s, id=%d, uuid=%s]", super.getUsername(), super.getId(),
                super.getUuid().toString());
    }
}
