package pl.ticket.event.admin.image.model;

import jakarta.persistence.*;
import lombok.*;
import pl.ticket.event.admin.event.model.AdminEvent;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
public class AdminImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "desctiption")
    private String desc;

    @Column(name = "thumbImage")
    private String thumbImage;

    @OneToMany(mappedBy = "image", fetch = FetchType.LAZY)
    private List<AdminEvent> events = new ArrayList<>();
}

