/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.appsdeveloperblog.estore.OrdersService.command.rest;

import com.appsdeveloperblog.estore.OrdersService.Service.OrderService;
import com.appsdeveloperblog.estore.OrdersService.core.model.OrderStatus;
import com.appsdeveloperblog.estore.OrdersService.command.commands.CreateOrderCommand;
import java.util.UUID;

import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrdersCommandController {

    private final CommandGateway commandGateway;
    @Autowired
    private  OrderService orderService;

    @Autowired
    public OrdersCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createOrder(@Valid @RequestBody OrderCreateRest order) {
        
        String userId = "0b2e6931-dd73-47eb-852b-68c9230f33ee";

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .addressId(order.getAddressId())
                .productId(order.getProductId())
                .userId(userId)
                .quantity(order.getQuantity())
                .orderId(UUID.randomUUID().toString())
                .orderStatus(OrderStatus.CREATED)
                .build();

        return commandGateway.sendAndWait(createOrderCommand);

    }
    @GetMapping("/{empId}")
    ResponseEntity<?> getOrder(@PathVariable("empId") String empId){

        return ResponseEntity.ok(orderService.getOrderDetails(empId));

    }

}
