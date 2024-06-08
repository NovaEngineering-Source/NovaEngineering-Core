package github.kasuminova.novaeng.common.util;

@FunctionalInterface
public interface BiFunction2Bool<T, U> {

    boolean apply(T t, U u);

}
