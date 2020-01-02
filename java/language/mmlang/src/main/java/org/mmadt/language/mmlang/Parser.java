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

package org.mmadt.language.mmlang;

import org.mmadt.language.compiler.Instructions;
import org.mmadt.language.compiler.OperatorHelper;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.branch.BranchInst;
import org.mmadt.machine.object.impl.composite.inst.branch.ChooseInst;
import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.impl.ext.composite.TPair;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.ext.algebra.WithOrderedRing;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import static org.mmadt.machine.object.impl.__.id;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@BuildParseTree
public class Parser extends BaseParser<Object> {

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
    final Rule TILDE = Terminal(Tokens.TILDE);
    final Rule LPAREN = Terminal(Tokens.LPAREN);
    final Rule RPAREN = Terminal(Tokens.RPAREN);
    final Rule SEMICOLON = Terminal(Tokens.SEMICOLON);
    final Rule TRUE = Terminal(Tokens.TRUE);
    final Rule FALSE = Terminal(Tokens.FALSE);
    final Rule EQUALS = Terminal(Tokens.EQUALS);
    final Rule DEQUALS = Terminal(Tokens.DEQUALS);
    final Rule LTE = Terminal(Tokens.LEQUALS);
    final Rule GTE = Terminal(Tokens.REQUALS);
    final Rule LT = Terminal(Tokens.LANGLE);
    final Rule GT = Terminal(Tokens.RANGLE);
    final Rule RPACK = Terminal(Tokens.RPACK);
    final Rule LPACK = Terminal(Tokens.LPACK);

    /// built-int type symbols
    final Rule OBJ = Terminal(Tokens.OBJ);
    final Rule INT = Terminal(Tokens.INT);
    final Rule REAL = Terminal(Tokens.REAL);
    final Rule STR = Terminal(Tokens.STR);
    final Rule BOOL = Terminal(Tokens.BOOL);
    final Rule REC = Terminal(Tokens.REC);
    final Rule LST = Terminal(Tokens.LST);
    final Rule INST = Terminal(Tokens.INST);

    ///////////////

    public Rule Source() {
        return Sequence(Expression(), EOI);
    }

    Rule Singles() {
        return FirstOf(Unary(), Grouping(), Obj());
    }

    Rule Expression() {
        return OneOrMore(
                Singles(),
                ZeroOrMore(BinaryOperator()));
    }

    Rule Unary() {
        return Sequence(UnaryOperator(), Singles(), swap(), this.push(OperatorHelper.applyUnary((String) this.pop(), type(this.pop())))); // always left associative
    }

    Rule Grouping() {
        return Sequence(LPAREN, Expression(), RPAREN);
    }

    Rule UnaryOperator() {
        return Sequence(TestNot(RPACK), FirstOf(LPACK, STAR, PLUS, DIV, SUB, AND, OR, GTE, LTE, GT, LT, DEQUALS), this.push(this.match().trim()));
    }

    Rule BinaryOperator() {
        return
                FirstOf(
                        ModelOperator(),
                        Sequence(FirstOf(Sequence(RPACK, this.push(this.match().trim()), Expression()),
                                Sequence(TestNot(RPACK), FirstOf(MAPSFROM, MAPSTO, LPACK, STAR, PLUS, DIV, SUB, AND, OR, GTE, LTE, GT, LT, DEQUALS), this.push(this.match().trim()), Singles())),
                                swap3(), swap(), this.push(OperatorHelper.applyBinary((String) this.pop(), type(this.pop()), type(this.pop())))));
    }

    @SuppressNode
    Rule ModelOperator() {
        return Sequence(EQUALS, Inst(), swap(), this.push(type(this.pop()).mapTo(inst(this.pop()))), MAPSTO, Singles(), swap(), this.push(type(this.pop()).mapTo(type(this.pop()))));
    }

    Rule Obj() {
        final Var<String> symbol = new Var<>();
        return Sequence(
                Optional(Type(symbol)),
                FirstOf(Sequence(OBJ, this.push(TObj.single())),
                        Bool(),
                        Inst(),
                        Real(),
                        Int(),
                        Str(),
                        Lst(),
                        Rec(),
                        Sym()),
                Optional(symbol.isSet(), this.push(type(this.pop()).symbol(symbol.get()))),
                Obj_Metadata());
    }

    Rule Obj_Metadata() {
        return Sequence(Optional(Quantifier(), swap(), this.push(type(this.pop()).q(this.pop()))),    // {quantifier}
                Optional(TILDE, Sequence(Word(), this.push(type(this.pop()).label(this.match().trim())))));     // ~label
    }

    Rule Sym() {
        return Sequence(Word(), this.push(TObj.sym(match().trim())));
    }

    Rule Lst() {
        return FirstOf(
                Sequence(LST, this.push(TLst.some())),
                Sequence(LBRACKET, SEMICOLON, RBRACKET, this.push(TLst.of())),
                Sequence(
                        LBRACKET, this.push(TLst.of()), Expression(), swap(), this.push(((Lst) this.pop()).plus(type(this.peek()).isLst() ? TLst.of(PList.of(this.pop())) : TLst.of(this.pop()))),
                        ZeroOrMore(SEMICOLON, Expression(), swap(), this.push(((Lst) this.pop()).plus(type(this.peek()).isLst() ? TLst.of(PList.of(this.pop())) : TLst.of(this.pop())))),
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

    Rule Field() {
        return Sequence(Expression(), COLON, Expression(), swap(), this.push(TRec.of(this.pop(), this.pop())));
    }

    Rule Real() {
        return FirstOf(
                Sequence(REAL, this.push(TReal.of()), Optional(Inst(), swap(), this.push(type(this.pop()).set(this.pop())))), // type predicate
                Sequence(Sequence(Number(), PERIOD, Number()), this.push(TReal.of(Float.valueOf(match().trim())))));
    }

    Rule Int() {
        return FirstOf(
                Sequence(INT, this.push(TInt.of()), Optional(Inst(), swap(), this.push(type(this.pop()).set(this.pop())))),  // type predicate
                Sequence(Number(), this.push(TInt.of(Integer.valueOf(match().trim())))));
    }

    Rule Str() {
        return FirstOf(
                Sequence(STR, this.push(TStr.of()), Optional(Inst(), swap(), this.push(type(this.pop()).set(this.pop())))),  // type predicate
                Sequence("\"\"\"", ZeroOrMore(Sequence(TestNot("\"\"\""), ANY)), this.push(TStr.of(match())), "\"\"\""),
                Sequence("\'", ZeroOrMore(Sequence(TestNot("\'"), ANY)), this.push(TStr.of(match())), "\'"),
                Sequence("\"", ZeroOrMore(Sequence(TestNot("\""), ANY)), this.push(TStr.of(match())), "\""));
    }

    Rule Bool() {
        return FirstOf(
                Sequence(BOOL, this.push(TBool.of()), Optional(Inst(), swap(), this.push(type(this.pop()).set(this.pop())))),  // type predicate
                Sequence(TRUE, this.push(TBool.of(true))),
                Sequence(FALSE, this.push(TBool.of(false))));
    }

    Rule Inst() {
        return FirstOf(
                Sequence(INST, this.push(TInst.some())),
                Sequence(this.push(id()), OneOrMore(Single_Inst(), swap(), this.push(this.inst(this.pop()).mult(inst(this.pop()))))));
    }

    Rule Branch() {
        return Sequence(Singles(), RPACK, Singles(), swap(), this.push(TRec.of(this.pop(), this.pop())));
    }

    @SuppressSubnodes
    Rule Branch_Inst() {
        final Var<String> operator = new Var<>();
        return Sequence(
                LBRACKET,
                Optional(FirstOf(PLUS, OR)), // for a clean consistent look on multi-line expressions
                FirstOf(Branch(), Singles()),
                FirstOf(PLUS, OR), operator.set(match().trim()),
                FirstOf(Branch(), Singles()),
                this.swap(), this.push(operator.getAndClear().equals(Tokens.BAR) ?
                        ChooseInst.create(this.pop(), this.pop()) :
                        BranchInst.create(this.pop(), this.pop())),
                ZeroOrMore(
                        FirstOf(PLUS, OR), operator.set(match().trim()),
                        FirstOf(Branch(), Singles()),
                        this.swap(), this.push(operator.getAndClear().equals(Tokens.BAR) ?
                                ChooseInst.create(this.pop(), this.pop()) :
                                BranchInst.create(this.pop(), this.pop()))),
                RBRACKET);
    }

    @SuppressSubnodes
    Rule Opcode_Inst() {
        final Var<String> opcode = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(
                LBRACKET,
                Sequence(Opcode(), opcode.set(match().trim()), ZeroOrMore(Optional(COMMA), Expression(), args.get().add(type(this.pop())))),    // arguments
                RBRACKET,
                this.push(Instructions.compile(TInst.of(opcode.get(), args.get()))));
    }

    @SuppressSubnodes
    Rule Single_Inst() {
        return Sequence(
                FirstOf(
                        Branch_Inst(),
                        Opcode_Inst()),// compiler grabs the instruction type
                Optional(Quantifier(), swap(), this.push(castToInst(this.pop()).q(this.pop()))));
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

    @SuppressNode
    Rule Type(final Var<String> symbol) {
        return Sequence(Sequence(Word(), TILDE), ACTION(!Tokens.RESERVED.contains(this.match().trim().replace(Tokens.TILDE, ""))), symbol.set(this.match().trim().replace(Tokens.TILDE, "")));
    }

    @SuppressNode
    Rule Opcode() {
        return FirstOf(EQUALS, Word());
    }

    @SuppressSubnodes
    Rule Quantifier() {
        return Sequence(
                LCURL,  // TODO: the *, +, ? shorthands assume Int ring. (this will need to change)
                FirstOf(Sequence(STAR, this.push(TPair.of(0, Integer.MAX_VALUE))),                                                                // {*}
                        Sequence(PLUS, this.push(TPair.of(1, Integer.MAX_VALUE))),                                                                // {+}
                        Sequence(QMARK, this.push(TPair.of(0, 1))),                                                                               // {?}
                        Sequence(COMMA, Expression(), this.push(TPair.of(this.<WithOrderedRing>type(this.peek()).min(), type(this.pop())))),                    // {,10}
                        Sequence(Expression(),
                                FirstOf(Sequence(COMMA, Expression(), swap(), this.push(TPair.of(type(this.pop()), type(this.pop())))),           // {1,10}
                                        Sequence(COMMA, this.push(TPair.of(type(this.peek()), this.<WithOrderedRing>type(this.pop()).max()))),                  // {10,}
                                        this.push(TPair.of(type(this.peek()), type(this.pop())))))),                                              // {1}
                RCURL);
    }

    <A extends Obj> A type(final Object object) {
        return (A) object;
    }

    Inst castToInst(final Object object) {
        return object instanceof Inst ? (Inst) object : StartInst.create(object); // start or map?
    }

    Inst inst(final Object object) {
        return (Inst) object;
    }
}
