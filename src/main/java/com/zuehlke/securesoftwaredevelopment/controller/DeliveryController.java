package com.zuehlke.securesoftwaredevelopment.controller;

import com.zuehlke.securesoftwaredevelopment.domain.DeliveryDetail;
import com.zuehlke.securesoftwaredevelopment.domain.ViewableDelivery;
import com.zuehlke.securesoftwaredevelopment.repository.DeliveryRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.List;

@Controller
public class DeliveryController {
    private final DeliveryRepository deliveryRepository;

    public DeliveryController(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        return "home";
    }


    @GetMapping("/deliveries")
    @PreAuthorize("hasAuthority('DELIVERY_LIST_VIEW')")
    public String showDeliveries(Model model) {
        model.addAttribute("deliveries", deliveryRepository.getAllDeliveries());
        return "deliveries";
    }

    @GetMapping("/delivery")
    @PreAuthorize("hasAuthority('DELIVERY_DETAILS_VIEW')")
    public String showDelivery(@RequestParam(name ="id", required = true) String id, Model model) {
        model.addAttribute("delivery", deliveryRepository.getDelivery(id));
        List<DeliveryDetail> details = deliveryRepository.getDeliveryDetails(id);
        model.addAttribute("details", details);
        model.addAttribute("sum", deliveryRepository.calculateSum(details));
        return "delivery";
    }

    @GetMapping(value = "/api/deliveries/search", produces = "application/json")
    @ResponseBody
    @PreAuthorize("hasAuthority('DELIVERY_LIST_VIEW')")
    public List<ViewableDelivery> search(@RequestParam("query") String query) throws SQLException {
        return deliveryRepository.search(query);
    }


}
