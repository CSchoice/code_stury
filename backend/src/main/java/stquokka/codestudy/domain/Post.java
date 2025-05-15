package stquokka.codestudy.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne
    private StudyGroup studyGroup;
    @ManyToOne
    private User author;
    private LocalDateTime createdAt;
    // Getters and Setters
}
