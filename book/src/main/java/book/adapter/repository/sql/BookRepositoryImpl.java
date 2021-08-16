package book.adapter.repository.sql;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import book.dto.Book;
import book.usecase.port.BookRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import static java.util.stream.Collectors.*;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository{

    private final @NonNull SqlBookRepository bookRepository;

    public Optional<Book> findById(long id){
        val bookDb = bookRepository.findById(id);
        if(bookDb.isPresent()){
            return Optional.of(bookDb.get().book());
        } else {
            return Optional.empty();
        }
    }
    
    public List<Book> findByName(String name){
        return bookRepository.findByName(name)
            .stream()
            .map(BookDb::book)
            .collect(toList());
    }

    public List<Book> findByIds(List<Long> ids) {
        return StreamSupport.stream(bookRepository.findAllById(ids).spliterator(), false)
            .map(BookDb::book)
            .collect(toList());
    }

    public List<Book> findByReaderId(long id) {
        return bookRepository.findByReaderId(id)
        .stream()
        .map(BookDb::book)
        .collect(toList());
    }
    public List<Book> findAll(){
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
            .map(BookDb::book)
            .collect(toList());
    }

    public List<Book> findAll(int page, int size) {
        return bookRepository.findAll(PageRequest.of(page, size))
            .stream()
            .map(BookDb::book)
            .collect(toList());
    }

    public Book save(Book book) {
        return bookRepository.save(BookDb.of(book)).book();
    }
    public List<Book> saveAll(List<Book> books){
        val booksDb = books.stream()
            .map(BookDb::of)
            .collect(toList());
        return StreamSupport.stream(bookRepository.saveAll(booksDb).spliterator(), false)
            .map(BookDb::book)
            .collect(toList());  
    }

    public void deleteAll() {
        bookRepository.deleteAll();
    }
}