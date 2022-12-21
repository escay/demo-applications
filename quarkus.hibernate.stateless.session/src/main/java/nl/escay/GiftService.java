package nl.escay;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import nl.escay.entity.Gift;

@ApplicationScoped
public class GiftService {
    @Inject
    EntityManager em; 

    @Transactional 
    public void createGift(String giftDescription) {
        Gift gift = new Gift();
        gift.setName(giftDescription);
        em.persist(gift);
    }
}