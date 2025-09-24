package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 512)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, updatable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime created;
}
