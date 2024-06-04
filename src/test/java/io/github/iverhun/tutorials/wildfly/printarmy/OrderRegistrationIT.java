package io.github.iverhun.tutorials.wildfly.printarmy;

import io.github.iverhun.tutorials.wildfly.Resources;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.logging.Logger;

@ExtendWith(ArquillianExtension.class)
public class OrderRegistrationIT {

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "io.github.iverhun.tutorials.wildfly")
                .addClasses(Order.class, OrderRegistration.class, Resources.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                // Deploy our test datasource
                .addAsWebInfResource("test-ds.xml");
    }

    @Inject
    OrderRegistration orderRegistration;

    @Inject
    Logger log;

    @Test
    public void testRegister() throws Exception {
        Order newOrder = new Order();
        newOrder.setName("FPV Cam Holder");
        newOrder.setPhoneNumber("jane@printservice.org");
        newOrder.setPhoneNumber("2125551234");
        newOrder.setCustomer("Ben Adiga");
        orderRegistration.register(newOrder);
        Assertions.assertNotNull(newOrder.getId());
        log.info(newOrder.getName() + " was persisted with id " + newOrder.getId());
    }

}
