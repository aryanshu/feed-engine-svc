package com.feed.engine.userImage;

import lombok.*;

import javax.persistence.*;

import javax.persistence.Entity;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class UserImage {


    @SequenceGenerator(
            name = "image_sequence",
            sequenceName = "image_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "image_sequence"
    )
    private Long imageId;
    private Long Id;
    private String filename;

    private Integer imageOrderId;


    public UserImage(Long id,  Integer imageOrderId, String filename) {
        Id = id;
        this.filename = filename;
        this.imageOrderId = imageOrderId;
    }
}
