package com.uroria.kebab.events;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    EventOrder order() default EventOrder.NORMAL;
}
