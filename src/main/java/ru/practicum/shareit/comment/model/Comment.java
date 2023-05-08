package ru.practicum.shareit.comment.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "item_id")
    private long item;
    @Column(name = "author_id", nullable = false)
    private long author;
    @Column(name = "created", nullable = false)
    LocalDateTime created;
}
