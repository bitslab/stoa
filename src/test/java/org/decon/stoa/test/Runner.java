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

import java.lang.reflect.Field;

import org.decon.stoa.test.generator.Dummy1Gen;
import org.decon.stoa.test.generator.Dummy22Gen;
import org.decon.stoa.test.generator.Dummy2Gen;
import org.decon.stoa.test.generator.Dummy3Gen;
import org.decon.stoa.test.generator.Dummy4Gen;
import org.decon.stoa.util.ExceptionUtils;
import org.junit.runners.model.InitializationError;

import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;

public class Runner extends JUnitQuickcheck {

    public Runner(Class<?> clazz) throws InitializationError {
        super(clazz);
        try {
            Field field = JUnitQuickcheck.class.getDeclaredField("repo");
            field.setAccessible(true);
            GeneratorRepository repo = (GeneratorRepository) field.get(this);
            repo.register(new Dummy1Gen());
            repo.register(new Dummy2Gen());
            repo.register(new Dummy22Gen());
            repo.register(new Dummy3Gen());
            repo.register(new Dummy4Gen());

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw ExceptionUtils.sneakyThrow(e);
        }
    }

}
