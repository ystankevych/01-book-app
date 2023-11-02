package mate.academy.mapper;

import mate.academy.dto.cart_item.CartItemDto;
import mate.academy.dto.cart_item.CartItemRequestDto;
import mate.academy.model.CartItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        uses = BookMapper.class,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CartItemMapper {
    void updateFromDto(CartItemRequestDto request, @MappingTarget CartItem cartItem);
    @Mapping(source = "bookId", target = "book", qualifiedByName = "bookFromId")
    CartItem toCartItem(CartItemRequestDto cartItemDto);
    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    CartItemDto toDto(CartItem cartItem);


}
