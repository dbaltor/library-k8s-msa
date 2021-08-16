package reader.usecase.port;

import reader.dto.Book;

import java.util.List;

public interface BookService {

   /**
    * Find books borrowed by a reader.
    * @param id    reader id
    * @return      list of books borrowed by the reader
    */
   public List<Book> findBooksByReaderId(long id);
}
