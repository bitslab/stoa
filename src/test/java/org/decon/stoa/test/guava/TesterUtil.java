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
package org.decon.stoa.test.guava;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.decon.stoa.util.ExceptionUtils;

import com.google.common.collect.testing.AbstractTester;

import junit.framework.TestSuite;

public class TesterUtil {

    public static void runTest(AbstractTester<?> test) {
        try {
            test.setUp();
            Method m = test.getClass()
                           .getMethod(test.getTestMethodName());
            m.invoke(test);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                ExceptionUtils.sneakyThrow(e.getCause());
                return;
            }
            ExceptionUtils.sneakyThrow(e);
        }
    }

    public static List<AbstractTester<?>> flatten(junit.framework.Test test) {
        if (test instanceof AbstractTester<?>) {
            return Collections.singletonList((AbstractTester<?>) test);
        } else if (test instanceof TestSuite) {
            TestSuite suite = (TestSuite) test;
            Enumeration<junit.framework.Test> tests = suite.tests();
            List<AbstractTester<?>> ret = new ArrayList<>();
            while (tests.hasMoreElements()) {
                ret.addAll(flatten(tests.nextElement()));
            }
            return ret;
        }
        throw new IllegalArgumentException("test=" + test + "(" + test.getClass()
                                                                      .toGenericString()
                + ")" + " is neither a TestSuite nor a AbstractTester.");
    }

}
