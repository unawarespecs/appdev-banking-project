package io.github.unawarespecs.bankapp.repo;


import io.github.unawarespecs.bankapp.entity.NotificationData;
import io.github.unawarespecs.bankapp.model.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDataRepository extends CrudRepository<NotificationData, Integer> {
    List<NotificationData> findByCustomerId(int id);
}
