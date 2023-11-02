package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("A")
@Getter
@NoArgsConstructor
public class Album extends Item {
    private String artist;
    private String ext;

    public Album(String artist, String ext) {
        this.artist = artist;
        this.ext = ext;
    }
}

