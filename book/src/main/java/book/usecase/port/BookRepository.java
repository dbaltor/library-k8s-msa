package book.usecase.port;

import java.util.List;
import java.util.Optional;

import book.dto.Book;

public interface BookRepository {
    public Optional<Book> findById(long id);
    public List<Book> findByName(String name);
    public List<Book> findByIds(List<Long> ids);
    public List<Book> findByReaderId(long Id);
    public List<Book> findAll();
    public List<Book> findAll(int page, int size);
    public Book save(Book book);
    public List<Book> saveAll(List<Book> books);
    public void deleteAll();
}