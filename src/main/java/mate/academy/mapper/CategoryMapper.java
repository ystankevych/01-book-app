package mate.academy.mapper;

import mate.academy.dto.category.CategoryDto;
import mate.academy.dto.category.CategoryRequestDto;
import mate.academy.model.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        implementationPackage = "<PACKAGE_NAME>.impl")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toCategory(CategoryRequestDto category);

    void updateCategoryFromDto(CategoryRequestDto categoryDto, @MappingTarget Category category);
}
