package reader.adapter.repository.sql;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface SqlReaderRepository extends PagingAndSortingRepository<ReaderDb, Long> {
    public List<ReaderDb> findByLastName(String lastName);
    public List<ReaderDb> findByFirstName(String firstName);
    public List<ReaderDb> findByDob(Date dob);    
}