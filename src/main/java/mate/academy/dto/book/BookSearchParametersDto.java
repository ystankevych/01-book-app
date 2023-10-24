package mate.academy.dto.book;

import java.util.List;

public record BookSearchParametersDto(
        List<String> titles,
        List<String> authors
) {}
