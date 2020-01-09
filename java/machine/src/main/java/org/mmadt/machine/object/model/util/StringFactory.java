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

package org.mmadt.machine.object.model.util;

import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.composite.util.PMap;
import org.mmadt.machine.object.model.ext.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.ext.composite.Pair;

import java.util.Map;

import static org.mmadt.language.compiler.Tokens.ASTERIX;
import static org.mmadt.language.compiler.Tokens.COLON;
import static org.mmadt.language.compiler.Tokens.COMMA;
import static org.mmadt.language.compiler.Tokens.CROSS;
import static org.mmadt.language.compiler.Tokens.EMPTY;
import static org.mmadt.language.compiler.Tokens.LBRACKET;
import static org.mmadt.language.compiler.Tokens.LCURL;
import static org.mmadt.language.compiler.Tokens.LPAREN;
import static org.mmadt.language.compiler.Tokens.MAPSFROM;
import static org.mmadt.language.compiler.Tokens.QUESTION;
import static org.mmadt.language.compiler.Tokens.RBRACKET;
import static org.mmadt.language.compiler.Tokens.RCURL;
import static org.mmadt.language.compiler.Tokens.RPAREN;
import static org.mmadt.language.compiler.Tokens.SEMICOLON;
import static org.mmadt.language.compiler.Tokens.SPACE;
import static org.mmadt.language.compiler.Tokens.TILDE;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StringFactory {

    private StringFactory() {
        // for static method use
    }

    private static void objMetadata(final Obj obj, final StringBuilder builder) {
        if (!obj.isSome())
            builder.append(quantifier(obj.q()));
        if (obj.isBound())
            builder.append(TILDE).append(obj.binding());
        if (!obj.ref().isOne()) {
            builder.append(SPACE)
                    .append(MAPSFROM)
                    .append(SPACE);
            /*if (!obj.access().domain().q().isZero())    // no obj{0} displays
                builder.append(LPAREN)
                        .append(obj.access().domain())
                        .append(RPAREN)
                        .append(MAPSTO); */ // TODO: the domain of the inst is represented as (obj)=> (cheesy)
            builder.append(obj.ref());
        }
    }

    private static String nestedObj(final Obj obj) {
        final StringBuilder builder = new StringBuilder();
        if (obj.constant() || !obj.isNamed())
            builder.append(obj);
        else {
            builder.append(obj.symbol());
            StringFactory.objMetadata(obj, builder);
        }
        return builder.toString();
    }

    public static String record(final Rec<? extends Obj, ? extends Obj> rec) {
        final StringBuilder builder = new StringBuilder();
        if (null == rec.get())
            builder.append(rec.symbol());
        else {
            if (rec.isNamed())
                builder.append(rec.symbol()).append(TILDE);
            if (rec.isInstance() || rec.get() != null)
                builder.append(rec.<PMap>get());
        }
        StringFactory.objMetadata(rec, builder);
        return builder.toString();
    }

    public static String map(final Map<? extends Obj, ? extends Obj> map) {
        final StringBuilder builder = new StringBuilder();
        builder.append(LBRACKET);
        if (map.isEmpty())
            builder.append(COLON);
        else {
            for (final Map.Entry<? extends Obj, ? extends Obj> entry : map.entrySet()) {
                builder.append(nestedObj(entry.getKey())).append(COLON).append(nestedObj(entry.getValue())).append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(RBRACKET);
        return builder.toString();
    }

    public static String list(final Lst<? extends Obj> lst) {
        final StringBuilder builder = new StringBuilder();
        if (null == lst.get())
            builder.append(lst.symbol());
        else {
            if (lst.isNamed())
                builder.append(lst.symbol()).append(TILDE);
            if (lst.get() instanceof PList) {
                builder.append(LBRACKET);
                if (lst.<PList>get().isEmpty())
                    builder.append(SEMICOLON);
                else {
                    for (Obj object : lst.<PList<Obj>>get()) {
                        builder.append(nestedObj(object)).append(SEMICOLON);
                    }
                    builder.deleteCharAt(builder.length() - 1);
                }
                builder.append(RBRACKET);
            } else if (null != lst.get())
                builder.append(lst.get().toString());
        }
        StringFactory.objMetadata(lst, builder);
        return builder.toString();
    }

    public static String obj(final Obj obj) {
        final StringBuilder builder = new StringBuilder();
        final Object o = obj.get();
        if (null == o)
            builder.append(obj.symbol());
        else {
            if (obj.isNamed())
                builder.append(obj.symbol()).append(TILDE);
            final boolean parentheses = o instanceof Inst && (null != obj.binding() || !obj.q().isOne());
            if (parentheses) builder.append(LPAREN);
            if (o instanceof Inst)
                builder.append(obj.symbol());
            final String oString = o instanceof String ?
                    String.format("'%s'", ((String) o).replaceAll("[\"\\\\]", "\\\\$0")) :
                    o.toString();
            builder.append(oString);
            if (parentheses) builder.append(RPAREN);
        }
        StringFactory.objMetadata(obj, builder);
        return builder.toString();
    }

    public static String inst(final Inst inst) {
        final StringBuilder builder = new StringBuilder();
        if (!TInst.some().get().equals(inst.get())) {
            for (final Inst single : inst.iterable()) {
                builder.append(LBRACKET);
                boolean opcode = true;
                for (Obj object : single.<Iterable<Obj>>get()) {
                    if (opcode) {
                        builder.append(object.get().toString()).append(COMMA);
                        opcode = false;
                    } else
                        builder.append(object).append(COMMA);
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append(RBRACKET);
                builder.append(quantifier(single.q()));
                if (null != single.binding())
                    builder.append(TILDE).append(single.binding());
            }
            if (null != inst.binding()) // TODO: this shouldn't happen over the entire stream
                builder.append(TILDE).append(inst.binding());
        } else
            builder.append(inst.symbol());
        return builder.toString();
    }

    private static String quantifier(final WithOrderedRing quantifier) {
        if (quantifier.isOne())
            return EMPTY;
        else if (QuantifierHelper.isStar(quantifier))
            return LCURL + ASTERIX + RCURL;
        else if (QuantifierHelper.isQMark(quantifier))
            return LCURL + QUESTION + RCURL;
        else if (QuantifierHelper.isPlus(quantifier))
            return LCURL + CROSS + RCURL;
        else if (QuantifierHelper.isSingle(quantifier))
            return LCURL + (quantifier instanceof Pair ? ((Pair) quantifier).first().toString() : quantifier.toString()) + RCURL;
        else
            return LCURL + ((Pair) quantifier).first() + COMMA + ((Pair) quantifier).second() + RCURL;
    }
}
