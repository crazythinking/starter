package net.engining.datasource.autoconfigure.support;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 事务监听事件
 *
 * @author : Eric Lu
 * @version :
 * @date : 2021-08-31 13:49
 * @since :
 **/
public class TransactionalEvent<E> extends ApplicationEvent {

    private final @Nullable E entity;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param entity the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public TransactionalEvent(@Nullable E entity) {
        super(Objects.requireNonNull(entity));
        this.entity = entity;
    }

    @Nullable
    public E getEntity() {
        return entity;
    }
}
