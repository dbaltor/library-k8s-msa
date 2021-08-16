package reader.adapter.repository.sql;

import reader.dto.Reader;
import reader.usecase.port.ReaderRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import static java.util.stream.Collectors.*;
import java.util.Date;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Repository
@RequiredArgsConstructor
public class ReaderRepositoryImpl implements ReaderRepository {

    private final @NonNull SqlReaderRepository readerRepository;

    public Optional<Reader> findById(long id){
        val readerDb = readerRepository.findById(id);
        if(readerDb.isPresent()){
            return Optional.of(readerDb.get().reader());
        } else {
            return Optional.empty();
        }
    }
    
    public List<Reader> findByLastName(String lastName){
        return readerRepository.findByLastName(lastName)
        .stream()
        .map(ReaderDb::reader)
        .collect(toList());
    }

    public List<Reader> findByFirstName(String firstName){
        return readerRepository.findByFirstName(firstName)
        .stream()
        .map(ReaderDb::reader)
        .collect(toList());
    }

    public List<Reader> findByDob(Date dob){
        return readerRepository.findByDob(dob)
        .stream()
        .map(ReaderDb::reader)
        .collect(toList());

    } 

    public List<Reader> findAll(){
        return StreamSupport.stream(readerRepository.findAll().spliterator(), false)
        .map(ReaderDb::reader)
        .collect(toList());
    }

    public List<Reader> findAll(int page, int size){
        return readerRepository.findAll(PageRequest.of(page, size))
        .stream()
        .map(ReaderDb::reader)
        .collect(toList());
    }

    public List<Reader> saveAll(List<Reader> readers){
        val readersDb = readers.stream()
        .map(ReaderDb::of)
        .collect(toList());
        return StreamSupport.stream(readerRepository.saveAll(readersDb).spliterator(), false)
        .map(ReaderDb::reader)
        .collect(toList()); 
    }

    public void deleteAll(){
        readerRepository.deleteAll();
    }
}