package com.appsdeveloperblog.estore.OrdersService.saga;

import com.appsdeveloperblog.estore.OrdersService.command.commands.ReserveProductCommand;
import com.appsdeveloperblog.estore.OrdersService.core.events.OrderCreatedEvent;
import com.core.events.ProductReservedEvent;
import com.core.model.User;
import com.core.query.FetchUserPaymentDetailsQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.io.Serializable;

@Saga
@Slf4j
public class SagaOrder implements Serializable {
    @Autowired
    private  transient CommandGateway commandGateway;
    @Autowired
    private  transient QueryGateway queryGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(final OrderCreatedEvent orderCreatedEvent) {
        log.debug("Received {}", orderCreatedEvent);
        ReserveProductCommand reserveProductCommand = ReserveProductCommand.builder()
                .productId(orderCreatedEvent.getProductId())
                .orderId(orderCreatedEvent.getOrderId())
                .quantity(orderCreatedEvent.getQuantity())
                .userId(orderCreatedEvent.getUserId()).build();
        log.info(" OrderCreatedEvent with productId : " + orderCreatedEvent.getProductId()
                + " and orderId " + orderCreatedEvent.getOrderId());
        commandGateway.send(reserveProductCommand, new CommandCallback<ReserveProductCommand, Object>() {

            @Override
            public void onResult(@Nonnull CommandMessage<? extends ReserveProductCommand> commandMessage, @Nonnull CommandResultMessage<?> commandResultMessage) {
                if (commandResultMessage.isExceptional()) {
                    //Start a compensating transaction
                }
            }
        });
    }


    @SagaEventHandler(associationProperty = "orderId")
    public void handler(final ProductReservedEvent productReservedEvent) {
        log.info(" ReserveProductCommand with productId : " + productReservedEvent.getProductId()
                + " and orderId " + productReservedEvent.getOrderId());
        FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery = new FetchUserPaymentDetailsQuery();
        fetchUserPaymentDetailsQuery.setUserId(productReservedEvent.getUserId());

        User user =null;
        try{
            user =   queryGateway.query(fetchUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
        }catch (Exception e){
            log.info(e.getMessage());
            //Start compensating transaction
            return;
        }
        if(user == null){
            // Start compensating transaction
            return ;
        }
        log.info("Successfully fetch user payment details of userId : ",user.getUserId());

    }
}
