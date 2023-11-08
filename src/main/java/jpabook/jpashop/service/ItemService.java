package jpabook.jpashop.service;

import jpabook.jpashop.controller.BookForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public Long saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public void updateBook(Long itemId, BookForm book) {
        Book one = (Book)itemRepository.findOne(itemId);
        // 실무에서는 setter 가 아닌 메서드를 사용하여 변경해야한다
        one.setName(book.getName());
        one.setPrice(book.getPrice());
        one.setStockQuantity(book.getStockQuantity());
        one.setAuthor(book.getAuthor());
        one.setIsbn(book.getIsbn());

    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
