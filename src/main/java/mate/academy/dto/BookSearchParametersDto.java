package mate.academy.dto;

import java.util.List;

public record BookSearchParametersDto(
        List<String> titles,
        List<String> authors
) {}
