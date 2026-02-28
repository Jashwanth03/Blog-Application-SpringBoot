package com.jash.blog.domain.entities;


import com.jash.blog.domain.PostStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID   id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    //OWNING SIDE --> ALWAYS WITH JOINCOLUMN
    @ManyToOne(fetch = FetchType.LAZY) // Each Post belongs to exactly one user
    @JoinColumn(name = "author_id",nullable = false) // Each post Cannot exist without a User (nullable = false)
    //name = "author_id" is a FK in Posts (Post.author.id == User.id)
    private User author;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id",nullable = false)
    private Category category;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="post_tags",
            joinColumns = @JoinColumn(name="post_id"),
            inverseJoinColumns = @JoinColumn(name="tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column(nullable = false)
    private Integer readingTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private LocalDateTime createdTime;

    @PrePersist
    protected void onCreate() { //time is updated on creation o the blog
        LocalDateTime now = LocalDateTime.now();
        this.updateTime = now;
        this.createdTime = now;
    }
    @PreUpdate // Time is Updated only when blog is updated
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id) && Objects.equals(title, post.title) && Objects.equals(content, post.content) && status == post.status && Objects.equals(readingTime, post.readingTime) && Objects.equals(updateTime, post.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, status, readingTime, updateTime);
    }


}
