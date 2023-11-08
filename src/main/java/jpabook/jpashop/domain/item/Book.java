package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("B")
@Getter
@NoArgsConstructor
@Setter
public class Book extends Item {
    private String author;
    private String isbn;

    public Book(String name, int price, int stock, String author, String isbn) {
        super(name, price, stock);
        this.author = author;
        this.isbn = isbn;
    }

}
