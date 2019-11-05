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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.processor.compiler.Argument;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class OrInst<S extends Obj> extends TInst implements MapInstruction<S, Bool> {

    private OrInst(final Object... arguments) {
        super(PList.of(arguments));
        this.<PList<Obj>>get().add(0, TStr.of(Tokens.OR));
    }

    public Bool apply(final S s) {
        return Stream.of(Argument.<S, Bool>args(args())).map(a -> a.mapArg(s)).reduce(Bool::and).orElse(TBool.of(true));
    }

    public static <S extends Obj> Bool create(final Supplier<Bool> result, final S source, final Object... arguments) {
        return ObjectHelper.allInstances(source) ?
                result.get() :
                TBool.of().q(source.q()).access(source.access().mult(new OrInst<>(arguments)));
    }
}