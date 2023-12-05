package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import mate.academy.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    private Category unsavedCategory;
    private Category savedCategory;
    @Mock
    private CategoryRepository repository;
    
    @Spy
    private CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);
    
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        unsavedCategory = new Category();
        unsavedCategory.setName("Investment");
        unsavedCategory.setDescription("Investment books");
        savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(unsavedCategory.getName());
        savedCategory.setDescription(unsavedCategory.getDescription());
    }

    @Test
    @DisplayName("Find all categories return not empty list of categories dto")
    void findAll_Categories_ShouldReturnNotEmptyList() {
        Category category = savedCategory;
        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = Collections.singletonList(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(repository.findAll(pageable)).thenReturn(categoryPage);

        List<CategoryDto> actual = categoryService.findAll(pageable);

        assertAll("actual",
                () -> assertEquals(1, actual.size()),
                () -> assertTrue(actual.contains(dtoFromCategory(category))));
    }

    @Test
    @DisplayName("Getting a category by an existing id returns correct CategoryDto")
    void getById_WithValidId_ShouldReturnCorrectDto() {
        Category category = savedCategory;

        when(repository.findById(anyLong())).thenReturn(Optional.of(category));

        CategoryDto expected = dtoFromCategory(category);
        CategoryDto actual = categoryService.getById(1L);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Retrieving a category by an invalid ID resulted in an exception")
    void getById_WithInvalidId_ShouldThrowException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.getById(10L),
                "Expected that an EntityNotFoundException would be thrown");
    }

    @Test
    @DisplayName("""
            Verify correct CategoryDto was returned when the category was saved""")
    void save_WithValidRequest_ReturnCorrectDto() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Investment",
                "Investment books");
        when(repository.save(unsavedCategory)).thenReturn(savedCategory);

        CategoryDto expected = dtoFromCategory(savedCategory);
        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Update existing category with new description is Ok""")
    void update_ExistingCategory_ShouldReturnCorrectDto() {
        Category category = savedCategory;
        CategoryRequestDto requestDto = new CategoryRequestDto("Investment",
                "Books on investments and finance");

        when(repository.findById(anyLong())).thenReturn(Optional.of(category));
        category.setDescription(requestDto.description());
        when(repository.save(category)).thenReturn(category);

        CategoryDto expected = dtoFromCategory(category);
        CategoryDto actual = categoryService.update(1L, requestDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Updating a category with an invalid id will throw an exception""")
    void updateCategory_WithInvalidId_ShouldThrowException() {
        CategoryRequestDto requestDto = new CategoryRequestDto("Investment",
                "Investment books");

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> categoryService.update(10L, requestDto),
                "Expected that an EntityNotFoundException would be thrown");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, -1L})
    void deleteById_WithAnyId_Ok(Long id) {
        categoryService.deleteById(id);

        verify(repository).deleteById(id);
    }

    private CategoryDto dtoFromCategory(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }
}
