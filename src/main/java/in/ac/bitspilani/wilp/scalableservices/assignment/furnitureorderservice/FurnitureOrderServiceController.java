package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao.CartRepository;
import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao.FurnitureCatalogDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FurnitureOrderServiceController
{
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private FurnitureCatalogDao furnitureCatalogDao;
    
    @PutMapping("/upsertCart")
    public Mono<Cart> upsertCart(@RequestBody CartItem[] cartItemsUpsert, ServerHttpRequest request) 
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
    
    
}
