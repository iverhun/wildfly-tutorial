package io.github.iverhun.tutorials.wildfly.printarmy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.logging.Logger;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(Transactional.TxType.REQUIRED)
public class OrderRegistration {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

    @Inject
    private Event<Order> orderEventSrc;

    public void register(Order order) {
        log.info("Registering " + order.getName());
        em.persist(order);
        
        // An event is sent every time an order is updated.
        // This allows other pieces of code (in this tutorial the order list is refreshed)
        // to react to changes in the order list without any coupling to this class.
        orderEventSrc.fire(order);
    }
}