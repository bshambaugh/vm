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

package org.mmadt.machine.console;

import org.mmadt.language.compiler.Instructions;
import org.mmadt.language.compiler.OperatorHelper;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@BuildParseTree
public class SimpleParser extends BaseParser<Object> {

    final Rule COLON = Terminal(Tokens.COLON);
    final Rule COMMA = Terminal(Tokens.COMMA);
    final Rule PERIOD = Terminal(Tokens.PERIOD);
    final Rule AND = Terminal(Tokens.AMPERSAND);
    final Rule OR = Terminal(Tokens.BAR);
    final Rule STAR = Terminal(Tokens.ASTERIX);
    final Rule PLUS = Terminal(Tokens.CROSS);
    final Rule QMARK = Terminal(Tokens.QUESTION);
    final Rule SUB = Terminal(Tokens.DASH);
    final Rule DIV = Terminal(Tokens.BACKSLASH);
    final Rule MAPSFROM = Terminal(Tokens.MAPSFROM);
    final Rule MAPSTO = Terminal(Tokens.MAPSTO);
    final Rule LBRACKET = Terminal(Tokens.LBRACKET);
    final Rule RBRACKET = Terminal(Tokens.RBRACKET);
    final Rule LCURL = Terminal(Tokens.LCURL);
    final Rule RCURL = Terminal(Tokens.RCURL);
    final Rule TRIPLE_QUOTE = Terminal(Tokens.DQUOTE + Tokens.DQUOTE + Tokens.DQUOTE);
    final Rule DOUBLE_QUOTE = Terminal(Tokens.DQUOTE);
    final Rule SINGLE_QUOTE = Terminal(Tokens.SQUOTE);
    final Rule TILDE = Terminal(Tokens.TILDE);
    final Rule LPAREN = Terminal(Tokens.LPAREN);
    final Rule RPAREN = Terminal(Tokens.RPAREN);
    final Rule EQUALS = Terminal(Tokens.EQUALS);
    final Rule GT = Terminal(Tokens.GT);
    final Rule LT = Terminal(Tokens.LT);
    final Rule NEQ = Terminal(Tokens.NEQ);
    final Rule SEMICOLON = Terminal(Tokens.SEMICOLON);
    final Rule TRUE = Terminal(Tokens.TRUE);
    final Rule FALSE = Terminal(Tokens.FALSE);

    /// built-int type symbols
    final Rule INT = Terminal(Tokens.INT);
    final Rule REAL = Terminal(Tokens.REAL);
    final Rule STR = Terminal(Tokens.STR);
    final Rule BOOL = Terminal(Tokens.BOOL);
    final Rule REC = Terminal(Tokens.REC);
    final Rule LST = Terminal(Tokens.LIST);
    final Rule INST = Terminal(Tokens.INST);

    final Rule OP_ID = Terminal(Tokens.ID);
    final Rule OP_DEFINE = Terminal(Tokens.DEFINE);
    final Rule OP_MODEL = Terminal(Tokens.MODEL);

    ///////////////

    Rule Source() {
        return Sequence(Expression(), EOI);
    }

    Rule Expression() {
        return OneOrMore(
                FirstOf(Unary(), Grouping(), Obj()),
                ZeroOrMore(Binary()));
    }

    Rule Unary() {
        return Sequence(UnaryOperator(), Expression(), this.push(((WithMinus) this.pop()).neg())); // currently the only unary operator is -
    }

    Rule Binary() {
        final Var<String> operator = new Var<>();
        return Sequence(
                BinaryOperator(), operator.set((String) this.pop()),
                Expression(), swap(), this.push(OperatorHelper.applyBinary(operator.get(), type(this.pop()), type(this.pop()))));
    }

    Rule Grouping() {
        return Sequence(LPAREN, Expression(), RPAREN);
    }

    Rule Obj() {
        return Sequence(
                FirstOf(Bool(),
                        Real(),
                        Int(),
                        Str(),
                        Rec(),
                        Lst(),
                        Inst()),                                                                              // obj
                Optional(Quantifier(), swap(), this.push((type(this.pop())).q((Q) this.pop()))),              // {quantifier}
                Optional(MAPSFROM, Inst(), swap(), this.push(type(this.pop()).access((Inst) this.pop()))));   // <= inst
    }

    Rule Lst() {
        return FirstOf(
                Sequence(LST, this.push(TLst.some())),
                Sequence(LBRACKET, SEMICOLON, RBRACKET, this.push(TLst.of())),
                Sequence(
                        LBRACKET, this.push(TLst.of()), Expression(), swap(), this.push(((Lst) this.pop()).put(type(this.pop()))),
                        ZeroOrMore(SEMICOLON, Expression(), swap(), this.push(((Lst) this.pop()).put(type(this.pop())))),
                        RBRACKET));
    }

    Rule Rec() {
        return FirstOf(
                Sequence(REC, this.push(TRec.some())),
                Sequence(LBRACKET, COLON, RBRACKET, this.push(TRec.of())),
                Sequence(LBRACKET,
                        this.push(TRec.of()), Field(), swap(), this.push(((Rec) this.pop()).plus(type(this.pop()))), // using plus (should use put -- but hard swap logic)
                        ZeroOrMore(COMMA, Field(), swap(), this.push(((Rec) this.pop()).plus(type(this.pop())))),
                        RBRACKET));
    }

    @SuppressSubnodes
    Rule Field() {
        return Sequence(Expression(), COLON, Expression(), swap(), this.push(TRec.of(this.pop(), this.pop())));
    }

    @SuppressSubnodes
    Rule Real() {
        return FirstOf(
                Sequence(REAL, this.push(TReal.of())),
                Sequence(Sequence(Number(), PERIOD, Number()), this.push(TReal.of(Float.valueOf(match().trim())))));
    }

    @SuppressSubnodes
    Rule Int() {
        return FirstOf(
                Sequence(INT, this.push(TInt.some())),
                Sequence(Number(), this.push(TInt.of(Integer.valueOf(match().trim())))));
    }

    @SuppressSubnodes
    Rule Str() {
        return FirstOf(
                Sequence(STR, this.push(TStr.some())),
                Sequence(TRIPLE_QUOTE, ZeroOrMore(Sequence(TestNot(TRIPLE_QUOTE), ANY)), this.push(TStr.of(match())), TRIPLE_QUOTE),
                Sequence(SINGLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\\'")), ANY)), this.push(TStr.of(match())), SINGLE_QUOTE),
                Sequence(DOUBLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\"")), ANY)), this.push(TStr.of(match())), DOUBLE_QUOTE));
    }

    @SuppressSubnodes
    Rule Bool() {
        return FirstOf(
                Sequence(BOOL, this.push(TBool.some())),
                Sequence(TRUE, this.push(TBool.of(true))),
                Sequence(FALSE, this.push(TBool.of(false))));
    }

    Rule Inst() {
        return FirstOf(
                Sequence(INST, this.push(TInst.some())),
                Sequence(this.push(TInst.ids()), OneOrMore(Single_Inst(), swap(), this.push(this.<Inst>type(this.pop()).mult(type(this.pop()))))));
    }

    @SuppressSubnodes
    Rule Single_Inst() {
        final Var<String> opcode = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(
                LBRACKET,
                Symbol(), opcode.set(match().trim()),                                           // opcode
                ZeroOrMore(Optional(COMMA), Expression(), args.get().add(type(this.pop()))),    // arguments
                RBRACKET, this.push(Instructions.compile(TInst.of(opcode.get(), args.get())))); // compiler grabs the instruction type
    }

    @SuppressSubnodes
    Rule Symbol() {
        return Word(); //Sequence(Word(), ZeroOrMore(FirstOf(Number(), Word())));
    }

    @SuppressSubnodes
    Rule UnaryOperator() {
        return SUB;
    }

    @SuppressSubnodes
    Rule BinaryOperator() {
        return Sequence(FirstOf(STAR, PLUS, DIV, SUB, AND, OR), this.push(this.match().trim()));
    }

    @SuppressNode
    Rule Terminal(final String string) {
        return Sequence(Spacing(), string, Spacing());
    }

    @SuppressNode
    Rule Spacing() {
        return ZeroOrMore(FirstOf(
                OneOrMore(AnyOf(" \t\r\n\f")),                                                               // whitespace
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),                                        // block comment
                Sequence("//", ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI)))); // line comment
    }

    @SuppressNode
    Rule Number() {
        return Sequence(OneOrMore(CharRange('0', '9')), Spacing());
    }

    @SuppressNode
    Rule Word() {
        return Sequence(OneOrMore(FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'))), Spacing());
    }

    @SuppressSubnodes
    Rule Quantifier() {
        return Sequence(
                LCURL,  // TODO: the *, +, ? shorthands assume Int ring. (this will need to change)
                FirstOf(Sequence(STAR, this.push(new TQ<>(0, Integer.MAX_VALUE))),                                                                      // {*}
                        Sequence(PLUS, this.push(new TQ<>(1, Integer.MAX_VALUE))),                                                                      // {+}
                        Sequence(QMARK, this.push(new TQ<>(0, 1))),                                                                                     // {?}
                        Sequence(COMMA, Expression(), this.push(new TQ<>((this.<WithOrderedRing>type(this.peek())).min(), type(this.pop())))),          // {,10}
                        Sequence(Expression(),
                                FirstOf(Sequence(COMMA, Expression(), swap(), this.push(new TQ<>(type(this.pop()), type(this.pop())))),                 // {1,10}
                                        Sequence(COMMA, this.push(new TQ<>(type(this.peek()), (this.<WithOrderedRing>type(this.peek())).max()))),       // {10,}
                                        this.push(new TQ<>(type(this.peek()), type(this.pop())))))),                                                    // {1}
                RCURL);
    }

    <A extends Obj> A type(final Object object) {
        return (A) object;
    }
}