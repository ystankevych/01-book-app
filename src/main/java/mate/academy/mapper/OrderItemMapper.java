package mate.academy.mapper;

import java.util.List;
import mate.academy.dto.orderitem.OrderItemDto;
import mate.academy.model.CartItem;
import mate.academy.model.OrderItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", source = "book.price")
    OrderItem cartItemToOrderItem(CartItem cartItem);

    @Mapping(target = "bookId", source = "book.id")
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    List<OrderItemDto> toOrderItemDtoList(List<OrderItem> orderItems);
}
