package mate.academy.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookSearchParametersDto {
    private List<String> titles;
    private List<String> authors;
}
