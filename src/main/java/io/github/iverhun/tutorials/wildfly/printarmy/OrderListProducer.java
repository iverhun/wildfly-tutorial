package io.github.iverhun.tutorials.wildfly.printarmy;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Reception;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.Produces;

import java.util.List;

@RequestScoped
public class OrderListProducer {
        @Inject
        private OrderRepository orderRepository;

        private List<Order> orders;

        // @Named provides access the return value via the EL variable name "orders" in the UI (e.g.
        // Facelets or JSP view)
        @Produces
        @Named
        public List<Order> getOrders() {
            return orders;
        }

        /*
            The observer method is notified whenever am order is created, removed, or updated.
            This allows us to refresh the list of orders whenever they are needed.
            This is a good approach as it allows us to cache the list of orders, but keep it up to date at the same time
         */
        public void onOrderListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Order Order) {
            retrieveAllOrdersOrderedByName();
        }

        @PostConstruct
        public void retrieveAllOrdersOrderedByName() {
            orders = orderRepository.findAllOrderedByName();
        }
    }