package itmo.dev.common.models;

import itmo.dev.common.enums.Breed;
import itmo.dev.common.enums.Color;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cats", schema = "public")
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    private Breed breed;
    private Color color;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "cat_friend",
            joinColumns = @JoinColumn(name = "cat_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    private List<Cat> catFriends = new ArrayList<>();
}
