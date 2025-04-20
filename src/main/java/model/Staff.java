package model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Staff {
    @Id
    @GeneratedValue
    private Integer id;

    private String username;
    private String password;
    private String fullname;
    private String role; // "kitchen", "manager", "chat"
    @OneToMany(mappedBy = "staff")
    private List<Message> messages;
}

