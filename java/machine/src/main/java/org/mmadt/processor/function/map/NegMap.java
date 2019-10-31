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

package org.mmadt.processor.function.map;

import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.function.MapFunction;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class NegMap<S extends WithMinus<S>> extends AbstractFunction implements MapFunction<S, S> {

    private NegMap(final Q quantifier, final String label) {
        super(quantifier, label);
    }

    @Override
    public S apply(final S obj) {
        return obj.neg();
    }

    public static <S extends WithMinus<S>> NegMap<S> compile(final Inst inst) {
        return new NegMap<S>(inst.q(), inst.label());
    }
}