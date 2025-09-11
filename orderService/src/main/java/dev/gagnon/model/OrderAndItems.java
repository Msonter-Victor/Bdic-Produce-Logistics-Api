package dev.gagnon.model;

import java.util.List;

public record OrderAndItems(Order order, List<OrderItem> items) {

}
