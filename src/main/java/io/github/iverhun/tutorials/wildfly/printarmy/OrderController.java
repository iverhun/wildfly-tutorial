package io.github.iverhun.tutorials.wildfly.printarmy;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.logging.Logger;

@Path("/orders")
@RequestScoped
public class OrderController {

    @Inject
    private Logger log;

    @Inject
    private Validator validator;

    @Inject
    private OrderRepository repository;

    @Inject
    private OrderRegistration registration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Order> listAllOrders() {
        return repository.findAllOrderedByName();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Order lookupOrderById(@PathParam("id") long id) {
        Order order = repository.findById(id);
        if (order == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
        return order;
    }

    /**
     * Creates a new order from the values provided.  Performs
     * validation, and will return a JAX-RS response with either
     * 200 ok, or with a map of fields, and related errors.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(Order order) {
        Response.ResponseBuilder builder;

        try {
            // Validates order using bean validation
            validateOrder(order);

            registration.register(order);

            //Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            //Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            //Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Name taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }


    /**
     * <p>
     * Validates the given Order variable and throws validation
     * exceptions based on the type of error. If the error is
     * standard bean validation errors then it will throw a
     * ConstraintValidationException with the set of the
     * constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing order with the
     * same name is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param order Order to be validated
     * @throws ConstraintViolationException
     *     If Bean Validation errors exist
     * @throws ValidationException
     *     If order with the same email already exists
     */
    private void validateOrder(Order order) throws ConstraintViolationException, ValidationException {
        //Create a bean validator and check for issues.
        Set<ConstraintViolation<Order>> violations = validator.validate(order);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
        }

        //Check the uniqueness of the email address
        if (nameAlreadyExists(order.getName())) {
            throw new ValidationException("Unique Name Violation");
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of
     * all violation fields, and their message. This can then be
     * used by clients to show violations.
     *
     * @param violations A set of violations that needs to be
     *                   reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.fine("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    /**
     * Checks if a order with the same email address is already
     * registered.  This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from
     * the Order class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false
    otherwise
     */
    public boolean nameAlreadyExists(String email) {
        Order order = null;
        try {
            order = repository.findByName(email);
        } catch (NoResultException e) {
            // ignore
        }
        return order != null;
    }
}
