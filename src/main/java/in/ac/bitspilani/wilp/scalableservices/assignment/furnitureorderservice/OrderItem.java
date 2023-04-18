package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = {"itemId", "color"})
public class OrderItem
{
    private UUID itemId;
    private String color;
    private Integer quantity;
    private BigDecimal pricePerUnit;
    
    public BigDecimal getItemTotalCost() 
    {
        return Optional.ofNullable(pricePerUnit).orElse(BigDecimal.ZERO).multiply(Optional.ofNullable(quantity).map(BigDecimal::valueOf).orElse(BigDecimal.ZERO));
    }
}
