package mate.academy.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.CategoryMapper;
import mate.academy.model.Category;
import mate.academy.repository.CategoryRepository;
import mate.academy.service.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No such a category with id: " + id
                ));
    }

    @Override
    public CategoryDto save(CategoryRequestDto categoryDto) {
        Category category = mapper.toCategory(categoryDto);
        return mapper.toDto(repository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryRequestDto categoryDto) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No such a category with id: " + id
                ));
        mapper.updateCategoryFromDto(categoryDto, category);
        return mapper.toDto(repository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
