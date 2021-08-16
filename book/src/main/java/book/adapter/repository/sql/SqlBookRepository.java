package book.adapter.repository.sql;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface SqlBookRepository extends PagingAndSortingRepository<BookDb, Long> {
    public List<BookDb> findByName(String name);

    public List<BookDb> findByReaderId(@Param("readerId") long readerId);
}
