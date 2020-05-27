package it.gov.pagopa.fa.common.factory;

import java.util.function.Function;

/**
 * Mapper between {@code <T>} class and {@code <U>} class
 *
 * @param <T> mapping source
 * @param <U> mapping target
 */
@FunctionalInterface
public interface ModelFactory<T, U> extends Function<T, U> {

}
