package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao.CartRepository;
import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao.FurnitureCatalogDao;
import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao.OrderRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FurnitureOrderServiceController
{
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private FurnitureCatalogDao furnitureCatalogDao;
    
    @PutMapping("/upsertCart")
    public Mono<Cart> upsertCart(@RequestBody OrderItem[] cartItemsUpsert, ServerHttpRequest request) 
    {
        Objects.requireNonNull(cartItemsUpsert);
        final String clientHostAddress = request.getRemoteAddress().getAddress().getHostAddress();
        return
                cartRepository.findByClientHostAddress(clientHostAddress)
                .switchIfEmpty(Mono.just(Cart.builder().clientHostAddress(clientHostAddress).build()))
                .flatMap(cart->{
                    return
                        Flux.fromArray(cartItemsUpsert)
                        .filterWhen(cartItemToAdd->furnitureCatalogDao.getCatalogItemWithStock(cartItemToAdd.getItemId())
                                                                .doOnSuccess(sr->cartItemToAdd.setPricePerUnit(sr.getCatalogItem().getPrice()))
                                                                .filter(sr->{
                                                                    Integer stock = Optional.ofNullable(sr.getColorWiseStock()).orElse(Collections.emptyMap()).get(cartItemToAdd.getColor());
                                                                    return Objects.nonNull(stock) && stock>cartItemToAdd.getQuantity();
                                                                })
                                                                .hasElement())
                        .filter(cartItemToAdd->cartItemToAdd.getQuantity()>0)
                        .collectList()
                        .flatMap(cartItems->{
                            
                            if(CollectionUtils.isEmpty(cartItems)) {
                                return Mono.error(new IllegalArgumentException("No valid items or stock not sufficient for some items")); 
                            }
                            
                            cart.setCartItems(cartItems);
                            cart.setLastUpdateTimestampUtc(LocalDateTime.now(ZoneOffset.UTC));
                            return cartRepository.save(cart);
                        });
                });
    }
    
    @PutMapping("/createOrder")
    public Mono<Order> createOrder(@RequestBody Order order)
    {
        return
                orderRepository.findById(order.getOrderId())
                .hasElement()
                .flatMap(exist->{
                    
                    if(exist) {
                        return Mono.error(new IllegalArgumentException("Cannot create new order with same id"));
                    }
                    
                    return
                            Flux.fromIterable(order.getOrderItems())
                            .filterWhen(cartItemToAdd->furnitureCatalogDao.getCatalogItemWithStock(cartItemToAdd.getItemId())
                                                                    .doOnSuccess(sr->cartItemToAdd.setPricePerUnit(sr.getCatalogItem().getPrice()))
                                                                    .filter(sr->{
                                                                        Integer stock = Optional.ofNullable(sr.getColorWiseStock()).orElse(Collections.emptyMap()).get(cartItemToAdd.getColor());
                                                                        return Objects.nonNull(stock) && stock>cartItemToAdd.getQuantity();
                                                                    })
                                                                    .hasElement())
                            .filter(cartItemToAdd->cartItemToAdd.getQuantity()>0)
                            .collectList()
                            .flatMap(cartItems->{
                                
                                if(CollectionUtils.isEmpty(cartItems)) {
                                    return Mono.error(new IllegalArgumentException("No valid items or stock not sufficient for some items")); 
                                }
                                
                                order.setOrderItems(cartItems);
                                order.setLastUpdateTimestampUtc(LocalDateTime.now(ZoneOffset.UTC));
                                return orderRepository.save(order);
                            });
                });
    }
    
    @PostMapping("/updateOrder")
    public Mono<Order> updateOrder(@RequestBody Order order)
    {
        return
                orderRepository.findById(order.getOrderId())
                .hasElement()
                .flatMap(exist->{
                    
                    if(!exist) {
                        return Mono.error(new IllegalArgumentException("Cannot update as no order with this id exists"));
                    }
                    
                    return
                            Flux.fromIterable(order.getOrderItems())
                            .filterWhen(cartItemToAdd->furnitureCatalogDao.getCatalogItemWithStock(cartItemToAdd.getItemId())
                                                                    .doOnSuccess(sr->cartItemToAdd.setPricePerUnit(sr.getCatalogItem().getPrice()))
                                                                    .filter(sr->{
                                                                        Integer stock = Optional.ofNullable(sr.getColorWiseStock()).orElse(Collections.emptyMap()).get(cartItemToAdd.getColor());
                                                                        return Objects.nonNull(stock) && stock>cartItemToAdd.getQuantity();
                                                                    })
                                                                    .hasElement())
                            .filter(cartItemToAdd->cartItemToAdd.getQuantity()>0)
                            .collectList()
                            .flatMap(cartItems->{
                                
                                if(CollectionUtils.isEmpty(cartItems)) {
                                    return Mono.error(new IllegalArgumentException("No valid items or stock not sufficient for some items")); 
                                }
                                
                                order.setOrderItems(cartItems);
                                order.setLastUpdateTimestampUtc(LocalDateTime.now(ZoneOffset.UTC));
                                return orderRepository.save(order);
                            });
                });
    }
    
    @PostMapping("/updateOrderPaymentStatus/{orderId}")
    public Mono<Order> updateOrderPaymentStatus(@PathVariable UUID orderId, @RequestParam PaymentStatus paymentStatus)
    {
        return
                orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cannot update as no order with this id exists")))
                .flatMap(order->{
                    order.setPaymentStatus(paymentStatus);
                    if(order.getPaymentMethod() == PaymentMethod.COD || paymentStatus == PaymentStatus.AUTHORIZED || paymentStatus== PaymentStatus.RECIEVED)
                    {
                        order.setDeliveryStatus(DeliveryStatus.PACKAGING);
                        orderRepository.save(order);
                    }
                    return orderRepository.save(order);
                });
    }
    
    @PostMapping("/updateOrderDeliveryStatus/{orderId}")
    public Mono<Order> updateOrderDeliveryStatus(@PathVariable UUID orderId, @RequestParam DeliveryStatus deliveryStatus)
    {
        return
                orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cannot update as no order with this id exists")))
                .flatMap(order->{
                    order.setDeliveryStatus(deliveryStatus);
                    return orderRepository.save(order);
                });
    }
    
}
