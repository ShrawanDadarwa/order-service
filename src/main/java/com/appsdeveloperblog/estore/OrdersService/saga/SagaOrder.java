package com.appsdeveloperblog.estore.OrdersService.saga;

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
@RequiredArgsConstructor
@Slf4j
public class SagaOrder {

    private final transient CommandGateway commandGateway;
    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent orderCreatedEvent){
        ReserveProductCommand reserveProductCommand =  ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .orderId(orderCreatedEvent.getOrderId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId()).build();
        log.info(" OrderCreatedEvent with productId : "+orderCreatedEvent.getProductId()
                +" and orderId "+orderCreatedEvent.getOrderId());
        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if(commandResultMessage.isExceptional()){
                    //Start a compensating transaction
                }
            }
        });
    }



    @SagaEventHandler(associationProperty = "orderId")
    public void handler(ProductReservedEvent productReservedEvent){
    log.info(" ReserveProductCommand with productId : "+productReservedEvent.getProductId()
            +" and orderId "+productReservedEvent.getOrderId());
    }
}
