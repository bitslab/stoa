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
package org.decon.stoa.test.generator;

import java.util.Random;
import java.util.function.Supplier;

import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.internal.generator.ServiceLoaderGeneratorSource;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class Utils {
    public static final Supplier<GeneratorRepository> genRepo = () -> {
        final SourceOfRandomness random = new SourceOfRandomness(new Random());
        GeneratorRepository repo = new GeneratorRepository(random).register(new ServiceLoaderGeneratorSource());
        repo.register(new Dummy1Gen());
        repo.register(new Dummy2Gen());
        repo.register(new Dummy22Gen());
        repo.register(new Dummy3Gen());
        repo.register(new Dummy4Gen());
        return repo;
    };
}
