package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.InfoFromRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {

    private final EntityManager entityManager;
    private final RequestServiceImpl requestService;
    private final RequestRepository requestRepository;
    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setName("name");
        user1.setEmail("mail@email.ru");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("mail2@email.ru");
        entityManager.persist(user2);
    }

    @AfterEach
    void afterEach() {
        entityManager.createNativeQuery("truncate table users");
        entityManager.createNativeQuery("truncate table items");
        entityManager.createNativeQuery("truncate table bookings");
        entityManager.createNativeQuery("truncate table requests");
    }

    @Test
    void addRequestTest() {
        RequestDto request = RequestDto.builder()
                .description("drill")
                .build();

        RequestDto requestDto = requestService.addRequest(user1.getId(), request);

        Request requestFromDb = requestRepository.getReferenceById(requestDto.getId());

        assertEquals(requestDto.getDescription(), requestFromDb.getDescription());
        assertEquals(requestDto.getId(), requestFromDb.getId());
        assertEquals(requestDto.getCreated(), requestFromDb.getCreated());
    }

    @Test
    void getUserRequestsTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("Num1")
                .build();
        entityManager.persist(request);

        Request request1 = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(1))
                .description("Num2")
                .build();
        entityManager.persist(request1);

        List<RequestDto> requests = requestService.getUserRequests(user1.getId());

        List<Request> requestsFromDb = requestRepository.getRequestsByRequesterId(user1.getId());

        assertEquals(requests.size(), 2);
        assertEquals(requests.get(0).getDescription(), requestsFromDb.get(0).getDescription());
        assertEquals(requests.get(0).getId(), requestsFromDb.get(0).getId());
        assertEquals(requests.get(0).getCreated(), requestsFromDb.get(0).getCreated());
        assertEquals(requests.get(1).getDescription(), requestsFromDb.get(1).getDescription());
        assertEquals(requests.get(1).getId(), requestsFromDb.get(1).getId());
        assertEquals(requests.get(1).getCreated(), requestsFromDb.get(1).getCreated());
    }

    @Test
    void getRequestsTest() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("description")
                .build();
        entityManager.persist(request);

        Request request1 = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(1))
                .description("description")
                .build();
        entityManager.persist(request1);

        InfoFromRequest requestInfo = InfoFromRequest.getInfoFromRequest(user2.getId(), 0, 10);

        List<RequestDto> requests = requestService.getRequests(requestInfo);
        TypedQuery<Request> query = entityManager.createQuery("SELECT rt from Request rt", Request.class);

        List<Request> requestsFromDb = query.getResultList();

        assertEquals(requests.size(), 2);
        assertEquals(requests.get(0).getDescription(), requestsFromDb.get(0).getDescription());
        assertEquals(requests.get(0).getId(), requestsFromDb.get(0).getId());
        assertEquals(requests.get(0).getCreated(), requestsFromDb.get(0).getCreated());
        assertEquals(requests.get(1).getDescription(), requestsFromDb.get(1).getDescription());
        assertEquals(requests.get(1).getId(), requestsFromDb.get(1).getId());
        assertEquals(requests.get(1).getCreated(), requestsFromDb.get(1).getCreated());

    }

    @Test
    void getRequestById() {
        Request request = Request.builder()
                .requester(user1)
                .created(LocalDateTime.now().minusHours(2))
                .description("description")
                .build();
        entityManager.persist(request);

        RequestDto requestFromService = requestService.getRequestById(user1.getId(), request.getId());

        Request requestFromDb = requestRepository.getReferenceById(request.getId());

        assertEquals(requestFromService.getDescription(), requestFromDb.getDescription());
        assertEquals(requestFromService.getId(), requestFromDb.getId());
        assertEquals(requestFromService.getCreated(), requestFromDb.getCreated());
    }
}