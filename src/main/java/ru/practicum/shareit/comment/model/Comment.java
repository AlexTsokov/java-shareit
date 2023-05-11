package ru.practicum.shareit.comment.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments", schema = "public")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "item_id")
    private Long item;
    @Column(name = "author_id", nullable = false)
    private Long author;
    @Column(name = "created", nullable = false)
    LocalDateTime created;
}
