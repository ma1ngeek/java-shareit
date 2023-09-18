package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoPost;
import ru.practicum.shareit.booking.dto.State;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StatusException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository repository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final Sort sortDesc = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(BookingDtoPost bookingDto, long bookerId) {

        if (!bookingDto.getEnd().isAfter(bookingDto.getStart())) {
            throw new ValidationException("End must be after Start");
        }

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item #" + bookingDto.getItemId() + " not found"));
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User #" + bookerId + " not found"));

        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Owner can't book item");
        }

        if (!item.isAvailable()) {
            throw new ValidationException("Item is unavailable");
        }

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);

        booking = repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getListByBooker(long bookerId, String stateName, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from должно быть положительным, size больше 0");
        }

        Pageable pageable = PageRequest.of(from / size, size, sortDesc);
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User #" + bookerId + " not found"));
        List<Booking> list;

        State state = getState(stateName);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case FUTURE:
                list = repository.findByBookerIdAndStartAfter(bookerId, now, pageable).toList();
                break;
            case PAST:
                list = repository.findByBookerIdAndEndBefore(bookerId, now, pageable).toList();
                break;
            case WAITING:
                list = repository.findByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable).toList();
                break;
            case REJECTED:
                list = repository.findByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable).toList();
                break;
            case CURRENT:
                list = repository.findByBookerIdAndStartBeforeAndEndAfter(bookerId, now, now, pageable).toList();
                break;
            default:
                list = repository.findByBooker_Id(bookerId, pageable).toList();
        }

        List<BookingDto> res = list.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return res;
    }

    @Override
    public List<BookingDto> getListByOwner(long ownerId, String stateName, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("from должно быть положительным, size больше 0");
        }

        Pageable pageable = PageRequest.of(from / size, size, sortDesc);
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User #" + ownerId + " not found"));
        List<Booking> list;

        State state = getState(stateName);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case FUTURE:
                list = repository.findByItemOwnerIdAndStartAfter(ownerId, now, pageable).toList();
                break;
            case PAST:
                list = repository.findByItemOwnerIdAndEndBefore(ownerId, now, pageable).toList();
                break;
            case WAITING:
                list = repository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable).toList();
                break;
            case REJECTED:
                list = repository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable).toList();
                break;
            case CURRENT:
                list = repository.findByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, pageable).toList();
                break;
            default:
                list = repository.findByItemOwnerId(ownerId, pageable).toList();
        }

        List<BookingDto> res = list.stream().map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
        return res;
    }

    @Override
    @Transactional
    public BookingDto approve(long bookingId, long userId, boolean approved) {
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking #" + bookingId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("User #" + userId + " can't edit booking #" + bookingId);
        }
        BookingStatus status = (approved) ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getStatus() == status) {
            throw new ValidationException("Already have status " + status);
        }
        booking.setStatus(status);
        booking = repository.save(booking);
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto getById(long id, long userId) {
        Booking booking = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking #" + id + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User #" + userId + " not found"));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Not found booking #" + id + " for User #" + userId);
        }
        return BookingMapper.toBookingDto(repository.getReferenceById(id));
    }

    private static State getState(String stateName) {
        State state;
        try {
            state = State.valueOf(stateName);
        } catch (IllegalArgumentException e) {
            throw new StatusException("Unknown state: " + stateName);
        }
        return state;
    }
}
