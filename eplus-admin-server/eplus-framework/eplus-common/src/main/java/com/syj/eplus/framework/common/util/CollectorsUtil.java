package com.syj.eplus.framework.common.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @Description：自定义bigdecimal求和类
 * @Author：du
 * @Date：2024/4/25 9:47
 */
public class CollectorsUtil {
    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    private CollectorsUtil() {
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    /**
         * Simple implementation class for {@code Collector}.
         *
         * @param <T> the type of elements to be collected
         * @param <R> the type of the result
         */
        record CollectorImpl<T, A, R>(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
                                      Function<A, R> finisher,
                                      Set<Characteristics> characteristics) implements Collector<T, A, R> {

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner,
                          Set<Characteristics> characteristics) {
                this(supplier, accumulator, combiner, castingIdentity(), characteristics);
            }
        }

    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(() -> new BigDecimal[1], (a, t) -> {
            if (a[0] == null) {
                a[0] = BigDecimal.ZERO;
            }
            a[0] = a[0].add(mapper.applyAsBigDecimal(t));
        }, (a, b) -> {
            a[0] = a[0].add(b[0]);
            return a;
        }, a -> a[0], CH_NOID);
    }
}
