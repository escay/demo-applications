package nl.escay;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class GiftServiceTest {

    @Inject 
    GiftService service;
    
    @Test
    public void testCreateGift() {
        service.createGift("testGift1");
    }

}
