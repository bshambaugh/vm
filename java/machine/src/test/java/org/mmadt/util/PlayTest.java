/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.util;

import org.junit.jupiter.api.Test;
import org.mmadt.language.compiler.Instructions;
import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.impl.___;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.model.atomic.Int;

import static org.mmadt.machine.object.impl.___.gt;
import static org.mmadt.machine.object.impl.___.is;
import static org.mmadt.machine.object.impl.___.mult;
import static org.mmadt.machine.object.impl.___.plus;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PlayTest {

    @Test
    void xxx() {
        System.out.println(TInt.of().<Int>write(TSym.of("x", 43)).<Int>write(plus(TInt.of(is(gt(50)))).mapFrom(mult(10))).state().toString());
        System.out.println(TInt.of().<Int>write(TSym.of("x", 43)).<Int>write(plus(TInt.of(is(gt(50)))).mapFrom(mult(10))).plus(TSym.of("x")).plus(800).toString());
    }

    @Test
    void yyy() {
        System.out.println(Instructions.compile(___.plus(2).mapTo(___.put(0, "minus").mult(___.drop(1)))));
    }

}