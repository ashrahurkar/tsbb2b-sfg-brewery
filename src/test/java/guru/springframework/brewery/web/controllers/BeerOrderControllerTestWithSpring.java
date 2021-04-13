package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerOrderController.class)
public class BeerOrderControllerTestWithSpring {

    @MockBean
    private BeerOrderService beerOrderService;

    @Autowired
    private MockMvc mockMvc;

    private BeerOrderDto validBeerOrder;
    private BeerDto validBeer;
    private BeerOrderPagedList beerOrderPagedList;

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder().id(UUID.randomUUID())
                .version(1)
                .beerName("Beer1")
                .beerStyle(BeerStyleEnum.PALE_ALE)
                .price(new BigDecimal("12.99"))
                .quantityOnHand(4)
                .upc(123456789012L)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .build();

        validBeerOrder = BeerOrderDto.builder().id(UUID.randomUUID())
                .version(1)
                .orderStatus(OrderStatusEnum.NEW)
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .customerId(UUID.randomUUID())
                .customerRef("1234")
                .orderStatusCallbackUrl("abcd")
                .beerOrderLines(List.of(BeerOrderLineDto.builder()
                        .beerId(validBeer.getId())
                        .build()))
                .build();

        beerOrderPagedList = new BeerOrderPagedList(List.of(validBeerOrder),
                PageRequest.of(1,1), 1L);
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @Test
    void testGetOrder() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        given(beerOrderService.getOrderById(any(),any())).willReturn(validBeerOrder);

        MvcResult result = mockMvc.perform(get("/api/v1/customers/"+ validBeerOrder.getCustomerId() +"/orders/"+ validBeerOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();
    }

    @Test
    void testListOrders() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        given(beerOrderService.listOrders(any(),any())).willReturn(beerOrderPagedList);

        MvcResult result = mockMvc.perform(get("/api/v1/customers/"+ validBeerOrder.getCustomerId() +"/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).andReturn();
    }
}
