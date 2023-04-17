package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.dao;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice.CatalogItemSearchResult;
import reactor.core.publisher.Mono;

@Repository
public class FurnitureCatalogDao
{

    @Value("${ssa1.furniture.catalog.getCatalogItemWithStockUrl}")
    private String getCatalogItemWithStockUrl;
    
    public Mono<CatalogItemSearchResult> getCatalogItemWithStock(UUID catalogItemId)
    {
        return
                WebClient.builder()
                .build()
                .get()
                .uri(getCatalogItemWithStockUrl, catalogItemId.toString())
                .retrieve()
                .bodyToMono(CatalogItemSearchResult.class);        
    }
}
