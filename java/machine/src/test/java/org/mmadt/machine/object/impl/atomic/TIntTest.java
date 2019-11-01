/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.object.impl.atomic;

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.util.TestHelper;
import org.mmadt.machine.object.model.atomic.Int;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest {

    @Test
    void testInstanceReferenceType() {
        Int instance = TInt.of(23);
        Int reference = TInt.of().access(start(1).plus(2).minus(7));
        Int type = TInt.some();
        TestHelper.validateKinds(instance, reference, type);
        //////
        instance = TInt.of(4).q(2);
        reference = TInt.of(23, 56, 11);
        type = TInt.of().q(45);
        TestHelper.validateKinds(instance, reference, type);
    }

    @Test
    void shouldTest() {
        assertTrue(TInt.some().test(TInt.of(32)));
        assertFalse(TInt.some().test(TReal.of(43.0f)));
        assertTrue(TObj.all().test(TInt.of(-1)));
        assertNotEquals(TInt.some(), TBool.some());
        assertNotEquals(TInt.some(), TStr.some());
    }
}
