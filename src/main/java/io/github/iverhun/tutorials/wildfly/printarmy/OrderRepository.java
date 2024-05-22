package io.github.iverhun.tutorials.wildfly.printarmy;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;


@ApplicationScoped
public class OrderRepository {

    @Inject
    private EntityManager em;

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findByUser(String user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = cb.createQuery(Order.class);
        Root<Order> order = criteria.from(Order.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(order).where(cb.equal(order.get(Order_.email), email));
        criteria.select(order).where(cb.equal(order.get("user"), user));
        return em.createQuery(criteria).getResultList();
    }


    public Order findByName(String name) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = cb.createQuery(Order.class);
        Root<Order> order = criteria.from(Order.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(order).where(cb.equal(order.get(Order_.email), email));
        criteria.select(order).where(cb.equal(order.get("name"), name));
        return em.createQuery(criteria).getSingleResult();
    }

    public List<Order> findAllOrderedByName() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> criteria = cb.createQuery(Order.class);
        Root<Order> Order = criteria.from(Order.class);
        // Swap criteria statements if you would like to try out type-safe criteria queries, a new
        // feature in JPA 2.0
        // criteria.select(Order).orderBy(cb.asc(Order.get(Order_.name)));
        criteria.select(Order).orderBy(cb.asc(Order.get("name")));
        return em.createQuery(criteria).getResultList();
    }
}
