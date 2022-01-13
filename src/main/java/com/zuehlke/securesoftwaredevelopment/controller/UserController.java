package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.config.AuditLogger;

import com.zuehlke.securesoftwaredevelopment.config.CsrfHttpSessionListener;
import com.zuehlke.securesoftwaredevelopment.domain.Address;
import com.zuehlke.securesoftwaredevelopment.domain.CustomerUpdate;
import com.zuehlke.securesoftwaredevelopment.domain.NewAddress;
import com.zuehlke.securesoftwaredevelopment.domain.RestaurantUpdate;
import com.zuehlke.securesoftwaredevelopment.repository.CustomerRepository;
import com.zuehlke.securesoftwaredevelopment.repository.RestaurantRepository;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static final AuditLogger auditLogger = AuditLogger.getAuditLogger(UserController.class);

    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;

    public UserController(CustomerRepository customerRepository, RestaurantRepository restaurantRepository) {
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('USERS_LIST_VIEW')")
    public String customers(Model model) {
        model.addAttribute("customers", customerRepository.getCustomers());
        return "customers";
    }

    @GetMapping("/restaurants")
    @PreAuthorize("hasAuthority('RESTAURANT_LIST_VIEW')")
    public String restaurants(Model model) {
        model.addAttribute("restaurants", restaurantRepository.getRestaurants());
        return "restaurants";
    }


    @GetMapping("/restaurant")
    @PreAuthorize("hasAuthority('RESTAURANT_DETAILS_VIEW')")
    public String getRestaurant(@RequestParam(name = "id", required = true) String id, Model model) {
        model.addAttribute("restaurant", restaurantRepository.getRestaurant(id));
        return "restaurant";
    }

    @DeleteMapping("/restaurant")
    @PreAuthorize("hasAuthority('RESTAURANT_DELETE')")
    public String deleteRestaurant(@RequestParam(name = "id", required = true) String id) {
        int identificator = Integer.valueOf(id);
        restaurantRepository.deleteRestaurant(identificator);
        return "restaurants";
    }

    @PostMapping("/api/restaurant/update-restaurant")
    @PreAuthorize("hasAuthority('RESTAURANT_EDIT')")
    public String updateRestaurant(RestaurantUpdate restaurantUpdate, Model model) {
        restaurantRepository.updateRestaurant(restaurantUpdate);
        restaurants(model);
        return "restaurants";
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('USERS_DETAILS_VIEW')")
    public String getCustomer(@RequestParam(name = "id", required = true) String id, Model model, HttpSession session) {
        String csrfToken = session.getAttribute(CsrfHttpSessionListener.CSRF_TOKEN).toString();
        model.addAttribute(CsrfHttpSessionListener.CSRF_TOKEN, csrfToken);
        model.addAttribute("customer", customerRepository.getCustomer(id));
        model.addAttribute("addresses", customerRepository.getAddresses(id));
        return "customer";
    }

    @DeleteMapping("/customer")
    @PreAuthorize("hasAnyAuthority('USERS_DELETE')")
    public String deleteCustomer(HttpSession session, @RequestParam(name = "id", required = true) String id, @RequestParam(name = "csrfToken") String csrfTokenInRequest) {
        String csrTokenInSession = session.getAttribute(CsrfHttpSessionListener.CSRF_TOKEN).toString();
        if (!csrTokenInSession.equals(csrfTokenInRequest)) {
            throw new AccessDeniedException("Forbidden");
        }
        customerRepository.deleteCustomer(id);
        return "customers";
    }

    @PostMapping("/api/customer/update-customer")
    @PreAuthorize("hasAuthority('USERS_EDIT')")
    public String updateCustomer(CustomerUpdate customerUpdate, Model model, HttpSession session, @RequestParam(name = "csrfToken") String csrfTokenInRequest) {
        String csrTokenInSession = session.getAttribute(CsrfHttpSessionListener.CSRF_TOKEN).toString();
        if (!csrTokenInSession.equals(csrfTokenInRequest)) {
            throw new AccessDeniedException("Forbidden");
        }
        customerRepository.updateCustomer(customerUpdate);
        customers(model);
        return "customers";
    }

    @DeleteMapping("/customer/address")
    @PreAuthorize("hasAuthority('USERS_EDIT')")
    public String deleteCustomerAddress(HttpSession session, @RequestParam(name = "id", required = true) String id, @RequestParam(name = "csrfToken") String csrfTokenInRequest) {
        String csrTokenInSession = session.getAttribute(CsrfHttpSessionListener.CSRF_TOKEN).toString();
        if (!csrTokenInSession.equals(csrfTokenInRequest)) {
            throw new AccessDeniedException("Forbidden");
        }
        int identificator = Integer.valueOf(id);
        customerRepository.deleteCustomerAddress(identificator);
        return "customers";
    }

    @PostMapping("/api/customer/address/update-address")
    @PreAuthorize("hasAuthority('USERS_EDIT')")
    public String updateCustomerAddress(Address address, Model model, HttpSession session, @RequestParam(name = "csrfToken") String csrfTokenInRequest) {
        String csrTokenInSession = session.getAttribute(CsrfHttpSessionListener.CSRF_TOKEN).toString();
        if (!csrTokenInSession.equals(csrfTokenInRequest)) {
            throw new AccessDeniedException("Forbidden");
        }
        customerRepository.updateCustomerAddress(address);
        customers(model);
        return "customers";
    }

    @PostMapping("/customer/address")
    @PreAuthorize("hasAuthority('USERS_EDIT')")
    public String putCustomerAddress(NewAddress newAddress, Model model, HttpSession session, @RequestParam(name = "csrfToken") String csrfTokenInRequest) {
        String csrTokenInSession = session.getAttribute(CsrfHttpSessionListener.CSRF_TOKEN).toString();
        if (!csrTokenInSession.equals(csrfTokenInRequest)) {
            throw new AccessDeniedException("Forbidden");
        }
        customerRepository.putCustomerAddress(newAddress);
        customers(model);
        return "customers";
    }
}
