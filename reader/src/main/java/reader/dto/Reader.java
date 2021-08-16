package reader.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@ToString @Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reader {
    private @Setter long id;
    private @NonNull String firstName;
    private @NonNull String lastName;
    private @NonNull Date dob;
    private @NonNull String address;
    private @NonNull String phone;
    // DDD aggregate
    private @Builder.Default List<Book> books = new ArrayList<>();
}
