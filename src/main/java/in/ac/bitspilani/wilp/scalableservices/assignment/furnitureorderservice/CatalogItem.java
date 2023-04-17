package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CatalogItem
{
    private UUID itemId;
    private String itemName;
    private UUID itemTypeId;
    private List<String> colors;
    private URL photoS3Url;
    private Map<String, Object> itemDetails;
    private BigDecimal price;
}
