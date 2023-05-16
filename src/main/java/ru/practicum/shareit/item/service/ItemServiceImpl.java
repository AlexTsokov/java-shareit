package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item createItem(Long userId, Item item) {
        User user = userService.findUserById(userId);
        item.setOwner(user);
        log.info("Вещь {} добавлена пользователю {} c id {}", item.getName(), user.getName(), userId);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item itemForUpdate = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        if (userService.checkUserExist(userId) && checkIfItemOwner(userId, itemId)) {
            item.setOwner(userService.findUserById(userId));
            if (item.getName() != null && !item.getName().isBlank()) {
                itemForUpdate.setName(item.getName());
                log.info("Вещь {} обновлена", item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                itemForUpdate.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                itemForUpdate.setAvailable(item.getAvailable());
            }
            return itemRepository.save(itemForUpdate);
        } else throw new EntityNotFoundException("Пользователь данной вещи не найден");
    }

    @Override
    public Item findItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public ItemDto findItemDtoById(Long userId, Long id) {
        Item item = itemRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if ((long) item.getOwner().getId() == userId) {
            Booking lastBooking = bookingRepository.findLastBookingByItemId(itemDto.getId());
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
            }
            Booking nextBooking = bookingRepository.findNextBookingByItemId(itemDto.getId());
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
            }
        }
        List<Comment> commentList = commentRepository.findByItemId(id);
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            commentDto.setAuthorName(comment.getAuthor().getName());
            commentDtoList.add(commentDto);
        }
        itemDto.setComments(commentDtoList);
        return itemDto;
    }

    @Override
    public List<ItemDto> findItemsByUser(Long userId) {
        List<Item> itemList = itemRepository.findItemsByUser(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            Booking lastBooking = bookingRepository.findLastBookingByItemId(itemDto.getId());
            if (lastBooking != null) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(lastBooking));
            }
            Booking nextBooking = bookingRepository.findNextBookingByItemId(itemDto.getId());
            if (nextBooking != null) {
                itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking));
            }
            List<Comment> commentList = commentRepository.findByItemId(item.getId());
            List<CommentDto> commentDtoList = CommentMapper.toCommentDtoList(commentList);
            itemDto.setComments(commentDtoList);
            itemDtoList.add(itemDto);
        }
        return itemDtoList;
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.search(text.toUpperCase());
    }

    public boolean checkIfItemOwner(Long userId, Long itemId) {
        Item item = findItemById(itemId);
        return (long) item.getOwner().getId() == userId;
    }

    @Override
    public void checkItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        if (!item.getAvailable()) {
            throw new BookingNotFoundException("Предмет не доступен для бронирования");
        }
    }
}
