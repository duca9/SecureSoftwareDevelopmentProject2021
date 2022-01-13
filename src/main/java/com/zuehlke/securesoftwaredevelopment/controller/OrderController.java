package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.domain.Food;
import com.zuehlke.securesoftwaredevelopment.domain.NewOrder;
import com.zuehlke.securesoftwaredevelopment.domain.User;
import com.zuehlke.securesoftwaredevelopment.repository.OrderRepository;
import com.zuehlke.securesoftwaredevelopment.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderController(OrderRepository orderRepository, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/order")
    @PreAuthorize("hasAuthority('ORDER_FOOD')")
    public String order(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        model.addAttribute("restaurants", restaurantRepository.getRestaurants());
        model.addAttribute("addresses", orderRepository.getAddresses(user.getId()));
        return "order";
    }


    @GetMapping(value = "/api/menu", produces = "application/json")
    @PreAuthorize("hasAuthority('ORDER_FOOD')")
    @ResponseBody
    public List<Food> getMenu(@RequestParam(name = "id") String id) {
        List<Food> menu;
        try {
            int identification = Integer.parseInt(id);
            menu = orderRepository.getMenu(identification);
        } catch (NumberFormatException e) {
            LOG.error("Error while parsing id");
            menu = new ArrayList<>();
        }
        return menu;
    }

    @PostMapping(value = "/api/new-order", consumes = "application/json")
    @PreAuthorize("hasAuthority('ORDER_FOOD')")
    @ResponseBody
    public String newOrder(@RequestBody NewOrder newOrder) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        orderRepository.insertNewOrder(newOrder, user.getId());
        return "";
    }
}
