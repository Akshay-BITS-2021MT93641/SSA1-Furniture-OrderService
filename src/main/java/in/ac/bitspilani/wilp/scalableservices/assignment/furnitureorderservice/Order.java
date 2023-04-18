package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class Order
{
    @Id
    @Builder.Default
    private UUID orderId = UUID.randomUUID();
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>(1);
    private String customerName;
    private String customerContact;
    private String billingAdress;
    private String shippingAdress;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private DeliveryStatus deliveryStatus;
    private LocalDateTime lastUpdateTimestampUtc;
}
