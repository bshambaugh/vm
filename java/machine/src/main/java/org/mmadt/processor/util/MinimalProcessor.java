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

package org.mmadt.processor.util;

import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.processor.Processor;
import org.mmadt.processor.ProcessorFactory;
import org.mmadt.processor.compiler.FunctionTable;
import org.mmadt.processor.function.FilterFunction;
import org.mmadt.processor.function.InitialFunction;
import org.mmadt.processor.function.MapFunction;
import org.mmadt.processor.function.QFunction;
import org.mmadt.processor.function.ReduceFunction;
import org.mmadt.util.EmptyIterator;
import org.mmadt.util.FunctionUtils;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class MinimalProcessor<S extends Obj, E extends Obj> implements Processor<S, E>, ProcessorFactory {

    protected final Inst bytecode;

    public MinimalProcessor(final Inst inst) {
        this.bytecode = inst;
    }

    private static <E extends Obj> Iterator<E> processTraverser(final E start, final Inst inst) {

        final QFunction function = FunctionTable.function(TModel.of("ex"), inst);
        if (function instanceof FilterFunction)
            return FunctionUtils.test((FilterFunction<Obj>) function, start).isPresent() ? IteratorUtils.of(start) : EmptyIterator.instance();
        else if (function instanceof MapFunction)
            return IteratorUtils.of((E) FunctionUtils.map((MapFunction) function, start));
        else if (function instanceof InitialFunction)
            return ((InitialFunction<E>) function).get();
        else if (function instanceof ReduceFunction)
            return IteratorUtils.of(IteratorUtils.stream(((List<E>) start.get()).iterator()).reduce((E) TInt.zeroInt(), ((ReduceFunction<E, E>) function)::apply));
        else
            throw new UnsupportedOperationException("This is not implemented yet: " + function);

    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public void stop() {
    }

    @Override
    public Iterator<E> iterator(final Iterator<S> starts) {
        Stream<E> stream = (Stream<E>) IteratorUtils.stream(starts);
        for (final Inst inst : this.bytecode.iterable()) {
            if (FunctionTable.function(TModel.of("ex"), inst) instanceof ReduceFunction)
                stream = IteratorUtils.stream(MinimalProcessor.processTraverser((E) TLst.of(stream.collect(Collectors.toList())), inst));
            else
                stream = stream.flatMap(s -> IteratorUtils.stream(MinimalProcessor.processTraverser(s, inst)));
        }
        return stream.map(s -> (E) s.label(this.bytecode.label())).iterator();
    }

    @Override
    public void subscribe(final Iterator<S> starts, final Consumer<E> consumer) {
        this.iterator(starts).forEachRemaining(consumer);
    }
}