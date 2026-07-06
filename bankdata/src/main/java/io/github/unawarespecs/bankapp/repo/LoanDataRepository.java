package io.github.unawarespecs.bankapp.repo;

import io.github.unawarespecs.bankapp.entity.LoanData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDataRepository extends CrudRepository<LoanData, Integer> {
}
