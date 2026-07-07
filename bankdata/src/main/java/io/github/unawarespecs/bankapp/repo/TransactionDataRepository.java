package io.github.unawarespecs.bankapp.repo;

import io.github.unawarespecs.bankapp.entity.TransactionData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDataRepository extends CrudRepository<TransactionData, Integer> {
    List<TransactionData> findByCustomerId(int customerId);
}
