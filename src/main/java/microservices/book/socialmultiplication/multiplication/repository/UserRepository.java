package microservices.book.socialmultiplication.multiplication.repository;

import microservices.book.socialmultiplication.multiplication.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{

    Optional<User> findByAlias(final String alias);
}
