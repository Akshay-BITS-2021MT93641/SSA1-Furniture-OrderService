package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.Order;

public interface OrderRepository extends ReactiveMongoRepository<Order, UUID>
{

}
