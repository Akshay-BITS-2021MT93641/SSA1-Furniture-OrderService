package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.Cart;
import reactor.core.publisher.Mono;

public interface CartRepository extends ReactiveMongoRepository<Cart, UUID>
{
    public Mono<Cart> findByClientHostAddress(String clientHostAddress);
}
