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
import org.mmadt.language.__;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TStream;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.CountInst;
import org.mmadt.machine.object.impl.composite.inst.reduce.SumInst;
import org.mmadt.machine.object.impl.util.TestHelper;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Inst;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.language.__.eq;
import static org.mmadt.language.__.gt;
import static org.mmadt.language.__.lt;
import static org.mmadt.language.__.mult;
import static org.mmadt.language.__.plus;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TIntTest {

    @Test
    void testInstanceReferenceType() {
        Int instance = TInt.of(23);
        Int reference = TInt.of(1, 2).plus(TInt.of(2)).minus(TInt.of(7));
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

    @Test
    void shouldMonoid() {
        /*final Obj obj = TInt.of(1,2,3).mult(TInt.of(5)).minus(TInt.of(3)).<Int>is(TInt.of().gt(TInt.of(2))).zero();
        System.out.println(obj);
        obj.iterable().forEach(System.out::println);

        final Model model= TModel.of("ex");
        model.define("int",TInt.of().inst(TInst.of(Tokens.PLUS,TInt.some().label("a")), __.plus(model.sym("a")).bytecode()));*/
        final Obj x = __.start(1,2,3).is(gt(2)).mult(plus(34)).is(gt(1).or(gt(110)).or(lt(10))).obj();
        System.out.println(x);
        x.iterable().forEach(System.out::println);
    }

    @Test
    void shouldMonoid2() {
        /*final Obj obj = TInt.of(1,2,3).mult(TInt.of(5)).minus(TInt.of(3)).<Int>is(TInt.of().gt(TInt.of(2))).zero();
        System.out.println(obj);
        obj.iterable().forEach(System.out::println);

        final Model model= TModel.of("ex");
        model.define("int",TInt.of().inst(TInst.of(Tokens.PLUS,TInt.some().label("a")), __.plus(model.sym("a")).bytecode()));*/
        Int t = TInt.of(1,2,3).inst(TInst.of(Tokens.IS), new SumInst<>()).inst(TInst.of(Tokens.MULT,TInt.some()),new PlusInst<>(TInt.of(23)));
        final Obj x = __.start(t).is(gt(2)).mult(plus(34)).is(gt(1).or(gt(110)).or(lt(10000))).obj();
        System.out.println(x);
        x.iterable().forEach(System.out::println);
    }
}
