package book.usecase.exception;

import java.util.Set;

import book.domain.port.ReaderEntity.ReturningErrors;

public class ReturningException extends Exception {
    public Set<ReturningErrors> errors;

    public ReturningException(Set<ReturningErrors> errors) {
        super();
        this.errors = errors;
    }

    private static final long serialVersionUID = 1L;
}