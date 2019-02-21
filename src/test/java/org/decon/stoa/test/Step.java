/*
 * Copyright (C) 2016 Adarsh Soodan
 * 
 * Stoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3.0 as 
 * published by the Free Software Foundation.
 *
 * Stoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3.0 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License version 3.0
 * along with Stoa.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.decon.stoa.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Step<T> implements InvocationHandler {
    private final List<T>                   targets;
    private final List<Optional<Object>>    returns;
    private final List<Optional<Throwable>> exceptions;

    @SuppressWarnings("unchecked")
    public static <T> T proxy(Class<T> cls, T... targets) {
        Step<T> step = new Step<>(targets);
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[] { cls }, step);
    }

    @SafeVarargs
    public Step(T... targets) {
        this.targets = Arrays.asList(targets);
        this.returns = new ArrayList<>();
        this.exceptions = new ArrayList<>();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        returns.clear();
        exceptions.clear();
        for (T target : targets) {
            returns.add(Optional.empty());
            exceptions.add(Optional.empty());
            try {
                Object r = method.invoke(target, args);
                returns.set(returns.size() - 1, Optional.of(r));
            } catch (Throwable t) {
                exceptions.set(exceptions.size() - 1, Optional.of(t));
            }
        }
        return returns.get(0)
                      .orElse(null);
    }

    public void assertOn(TriConsumer<T, Optional<Object>, Optional<Throwable>> assertion) {
        for (int i = 0; i < targets.size(); i++) {
            assertion.accept(targets.get(i), returns.get(i), exceptions.get(i));
        }
    }

    @FunctionalInterface
    public static interface TriConsumer<T1, T2, T3> {
        void accept(T1 t1, T2 t2, T3 t3);
    }
}
