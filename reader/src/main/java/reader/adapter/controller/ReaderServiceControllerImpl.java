package reader.adapter.controller;

import reader.adapter.controller.port.ReaderServiceController;
import reader.domain.port.ReaderEntity;
import reader.domain.port.ReaderEntity.BorrowingErrors;
import reader.domain.port.ReaderEntity.ReturningErrors;
import reader.dto.Reader;
import reader.usecase.port.ReaderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.web.bind.annotation.RestController;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RestController
@RequiredArgsConstructor
public class ReaderServiceControllerImpl implements ReaderServiceController{
    private final @NonNull ReaderService readerService;
    private final @NonNull ReaderEntity readerEntity;

    @Override
    public List<Reader> getReaders(
        Optional<Integer> pageNum,
        Optional<Integer> pageSize,
        Optional<Long> readerId) {
            if (readerId.isPresent()) {
                //retrieve reader
                val readers = new ArrayList<Reader>();
                val reader = readerService.retrieveReader(readerId.get());
                if (reader.isPresent()) {
                    readers.add(reader.get());
                }
                return readers;
            }
            else {
                // Retrieve readers
                return readerService.retrieveReaders(pageNum, pageSize);
            }
    }

    @Override
    public List<Reader> loadDatabase(Optional<Integer> count) {
        // load database
        return readerService.loadDatabase(count);
    }

    @Override
    public String cleanUp() {
        readerService.cleanUpDatabase();
        return "The data have been removed";
    }

    @Override
    public Set<BorrowingErrors> validateBookBorrowing(
        Long readerId, 
        ValidationRequest validationRequest) {
            return readerEntity.bookBorrowingValidator(
                readerId,
                validationRequest.getBooksToValidate(),
                validationRequest.getBorrowedBooksByReader());
    }

    @Override
    public Set<ReturningErrors> validateBookReturning(
        Long readerId, 
        ValidationRequest validationRequest) {
            return readerEntity.bookReturningValidator(
                readerId, 
                validationRequest.getBooksToValidate(),
                validationRequest.getBorrowedBooksByReader());
    }
}