package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.service.BookService;
import mate.academy.service.CategoryService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Category management", description = "Endpoints for mapping categories")
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {
    private final BookService bookService;
    private final CategoryService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new category")
    public CategoryDto createCategory(@RequestBody @Valid CategoryRequestDto category) {
        return service.save(category);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update existing category",
            description = """
                    Update existing category by its id and specifying new parameters"""
    )
    public CategoryDto updateCategory(@PathVariable @Positive Long id,
                                      @RequestBody @Valid CategoryRequestDto category) {
        return service.update(id, category);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all categories",
            description = """
                    Getting all categories with default pagination
                    (10 categories per page) and sorting"""
    )
    public List<CategoryDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a category by its id")
    public CategoryDto getCategoryById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a category by its id")
    public void deleteCategory(@PathVariable @Positive Long id) {
        service.deleteById(id);
    }

    @GetMapping("/{id}/books")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get a list of books",
            description = """
                    Get a list of books from a specific category.
                    Search parameters: category id"""
    )
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(
            @PathVariable @Positive Long id) {
        return bookService.findAllByCategoryId(id);
    }
}
