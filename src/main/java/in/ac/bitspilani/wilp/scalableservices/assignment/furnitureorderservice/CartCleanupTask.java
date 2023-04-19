package in.ac.bitspilani.wilp.scalableservices.assignment.furnitureorderservice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CartCleanupTask
{
    @Autowired
    private ReactiveMongoOperations reactiveMongoOperations;
    
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void cleanupOldCarts() {
        
        LocalDateTime utc24hrsAgo = LocalDateTime.now(ZoneOffset.UTC).minusHours(24);

        Query query = Query.query(Criteria.where("lastUpdateTimestampUtc").lt(utc24hrsAgo));
        
        reactiveMongoOperations.findAndRemove(query, Cart.class).block();
        log.info("Cleanup completed");
    }
}
