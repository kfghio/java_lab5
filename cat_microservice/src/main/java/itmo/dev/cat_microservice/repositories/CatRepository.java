package itmo.dev.cat_microservice.repositories;

import itmo.dev.common.models.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatRepository extends JpaRepository<Cat, Integer> {

    List<Cat> findAllByOwnerId(Integer OwnerId);
}
