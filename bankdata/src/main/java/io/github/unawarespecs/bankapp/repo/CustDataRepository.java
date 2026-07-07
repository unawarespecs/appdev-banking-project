package io.github.unawarespecs.bankapp.repo;

import io.github.unawarespecs.bankapp.entity.CustomerData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustDataRepository extends CrudRepository<CustomerData, Integer> {
    List<CustomerData> findAll();
    Optional<CustomerData> findByUuid(UUID uuid);
    @Query("SELECT cd.id FROM CustomerData cd WHERE cd.username=:user AND cd.password=:pass")
    Integer getCustomerId(@Param("user") String user, @Param("pass") String pass);

    void deleteById(Integer id);
}
