package ru.practicum.shareit.request.dal;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorId(long requestorId, Sort sort);

    List<ItemRequest> findByRequestorIdNot(long requestorId, Sort sort);
}
