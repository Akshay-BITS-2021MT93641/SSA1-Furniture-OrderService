package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Document
public class Cart
{
    @Id
    @Builder.Default
    private UUID cartId = UUID.randomUUID();
    private String clientHostAddress;
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>(1);
    private LocalDateTime lastUpdateTimestampUtc;
    
    public BigDecimal getCartTotalCost() 
    {
        return Optional.ofNullable(cartItems).orElse(Collections.emptyList()).stream().map(CartItem::getItemTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
