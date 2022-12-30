package com.uroria.kebab.events;

public enum EventOrder {
    FIRST(0),
    EARLY(1),
    NORMAL(2),
    LATE(3),
    LAST(4);

    private final int order;
    EventOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }

    public static EventOrder getByOrder(int order) {
        for (EventOrder eventOrder: EventOrder.values()) {
            if (eventOrder.getOrder() == order) {
                return eventOrder;
            }
        }
        return null;
    }

    public static EventOrder[] getOrdersInOrder() {
        EventOrder[] eventOrders = new EventOrder[EventOrder.values().length];
        for (int i = 0; i < eventOrders.length; i++) {
            eventOrders[i] = EventOrder.getByOrder(i);
        }
        return eventOrders;
    }
}
