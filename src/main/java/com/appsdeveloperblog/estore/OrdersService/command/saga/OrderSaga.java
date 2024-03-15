package com.appsdeveloperblog.estore.OrdersService.command.saga;

import com.appsdeveloperblog.estore.OrdersService.command.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.OrdersService.core.events.OrderCreatedEvent;
import com.core.events.ProductReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

import javax.annotation.Nonnull;

@Saga
@Slf4j
@RequiredArgsConstructor
public class OrderSaga {
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handler(OrderCreatedEvent orderCreatedEvent) {
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .orderId(orderCreatedEvent.getOrderId())
                .userId(orderCreatedEvent.getUserId())
                .quantity(orderCreatedEvent.getQuantity()).build();
        log.info("OrderCreatedEvent handled for orderId ",orderCreatedEvent.getOrderId() +
                " and productId "+orderCreatedEvent.getProductId());

        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    //Start a compenstaging transaction
                }
            }
        });
    }
    @SagaEventHandler(associationProperty = "orderId")
    public void handler(ProductReservedEvent productReservedEvent){
    //process user payment
        log.info("ProductReservedEvent this is productId : "+productReservedEvent.getProductId()+" and orderId "+productReservedEvent.getOrderId());
    }
    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void endHandler(OrderCreatedEvent productReservedEvent){
        //process user payment
        log.info("ProductReservedEvent this is productId : "+productReservedEvent.getProductId()+" and orderId "+productReservedEvent.getOrderId());
    }
}
