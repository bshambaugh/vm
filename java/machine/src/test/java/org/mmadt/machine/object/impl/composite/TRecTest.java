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

package org.mmadt.machine.object.impl.composite;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.TestUtilities;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.Bindings;
import org.mmadt.util.ProcessArgs;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.machine.object.impl.__.gt;
import static org.mmadt.machine.object.impl.__.is;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.one;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.plus;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.qmark;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.star;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.zero;
import static org.mmadt.util.ProcessArgs.args;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
final class TRecTest implements TestUtilities {

    private final static ProcessArgs[] PROCESSING = new ProcessArgs[]{
            // instances
            args(List.of(TRec.of(Map.of("a", 1))), TRec.of(Map.of("a", 1))),
            args(List.of(TRec.of(Map.of("name", TStr.of("marko").binding("a"))).binding("c")), TRec.of(Map.of("name", "marko")).as(TRec.of(Map.of("name", TStr.of().binding("a"))).binding("c"))),
            args(List.of(TRec.of(Map.of("name", TStr.of("marko").binding("a"))).binding("c")), TRec.of(Map.of("name", "marko", "age", 29)).as(TRec.of(Map.of("name", TStr.of().binding("a"))).binding("c"))),
            args(List.of(TRec.of(Map.of()).binding("c")), TRec.of(Map.of("name", "marko", "age", 29)).as(TRec.of(Map.of()).binding("c"))),
            args(List.of(), TRec.of(Map.of("name", "marko", "age", 29)).as(TRec.of(Map.of("friend", "bob")).binding("c"))),
    };

    @TestFactory
    Stream<DynamicTest> testProcessing() {
        return Stream.of(PROCESSING).map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> assertEquals(tp.expected, submit(tp.input))));
    }

    @Test
    void testType() {
        validateTypes(TRec.some());
    }

    @Test
    void testIsA() {
        validateIsA(TRec.some());
    }

    /*@Test
    void testBytecodeTestingMatchingAndBinding() {
        final Bindings bindings = new Bindings();
        final Rec<Str, ?> type = TRec.of(
                "name", is(eq("marko")).as("x"),
                "age", (is(a(TInt.of())).is(gt(23))).as("y"));
        assertEquals("y", type.get(TStr.of("age")).label());
        final Rec<Str, Obj> person = TRec.of("name", "marko", "age", 29);
        System.out.println(type + ":::" + person);
        assertTrue(type.test(person));
        assertFalse(person.test(type));
        assertTrue(type.match(bindings, person));
        assertEquals(2, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("x"));
        assertEquals(TInt.of(29), bindings.get("y"));
        // TODO final Rec<Str,?> bound = type.bind(bindings);
        // System.out.println(bound);
        //  assertEquals(person, bound);
        final Lst list = TLst.of(TStr.of().label("x"), TInt.of().label("y"));
        assertEquals(TLst.of("marko", 29), list.bind(bindings));
    }*/

    @Test
    void shouldAndCorrectly() {
        Rec rec1 = TRec.of("name", "marko");
        Rec rec2 = TRec.of("age", 29);
        Rec rec3 = TRec.of("name", "marko", "age", 29);
        assertTrue(rec1.constant());
        assertTrue(rec2.constant());
        assertTrue(rec3.constant());
        assertEquals(rec3, rec1.and(rec2));
        assertEquals(rec3, rec3.and(rec3));
        ///
        rec1 = TRec.of("name", "marko");
        assertTrue(rec1.constant());
        assertEquals(TRec.of("name", "marko", "age", 29), rec1.and(rec2));
        //
        rec1 = TRec.of("name", TStr.of());
        assertFalse(rec1.constant());
        assertEquals(TRec.of("name", TStr.of(), "age", 29), rec1.and(rec2));
    }

    @Test
    void shouldOrCorrectly() {
        Rec rec1 = TRec.of("name", "marko");
        Rec rec2 = TRec.of("age", 29);
        Rec rec3 = (TRec) rec1.or(rec2);
        assertTrue(rec1.constant());
        assertTrue(rec2.constant());
        //assertFalse(rec3.constant());
        assertEquals(rec3, rec1.or(rec2));
        // assertEquals(rec3, rec3.or(rec3));
        ///
        rec1 = TRec.of("name", "marko");
        assertTrue(rec1.constant());
        assertEquals(rec3, rec1.or(rec2));
        //
        Rec rec4 = (TRec) TRec.of("name", TStr.of()).or(TRec.of("age", 29));
        assertFalse(rec4.constant());
        assertEquals(rec4, TRec.of("name", TStr.of()).or(rec2));
    }

    @Test
    void shouldSupportQuantifiersInTest() {
        Rec person = TRec.of("name", TStr.of(), "age", TInt.of().q(qmark)).symbol("person");
        final Rec marko = TRec.of("name", "marko", "age", TInt.of(29));
        final Rec kuppitz = TRec.of("name", "kuppitz");
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().q(zero)).symbol("person");
        assertFalse(person.test(marko));
        assertTrue(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().q(star)).symbol("person");
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().q(plus)).symbol("person");
        assertTrue(person.test(marko));
        assertFalse(person.test(kuppitz));
        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().q(one)).symbol("person");
        assertTrue(person.test(marko));
        assertFalse(person.test(kuppitz));
    }

    @Test
    void shouldSupportQuantifiersInMatch() {
        Bindings bindings = new Bindings();
        Rec<Obj, Obj> person = TRec.of("name", TStr.of(), "age", TInt.of().q(qmark).binding("a")).symbol("person");
        final Rec marko = TRec.of("name", "marko", "age", TInt.of(29));
        final Rec kuppitz = TRec.of("name", "kuppitz");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertTrue(person.match(bindings, kuppitz));
        bindings.clear();
        //
        person = TRec.of("name", TStr.of(), "age", TInt.none().binding("a")).symbol("person");
        assertFalse(person.match(bindings, marko));
        assertEquals(0, bindings.size());
        assertTrue(person.match(bindings, kuppitz));
        System.out.println(bindings);
        assertEquals(0, bindings.size());
        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().q(star).binding("a")).symbol("person");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertTrue(person.match(bindings, kuppitz));
        assertEquals(0, bindings.size());

        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().q(plus).binding("a")).symbol("person");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertFalse(person.match(bindings, kuppitz));
        assertEquals(0, bindings.size());
        //
        person = TRec.of("name", TStr.of(), "age", TInt.of().binding("a")).symbol("person");
        assertTrue(person.match(bindings, marko));
        assertEquals(1, bindings.size());
        assertEquals(TInt.of(29), bindings.get("a"));
        bindings = new Bindings();
        assertFalse(person.test(kuppitz));
        assertEquals(0, bindings.size());
    }

    @Test
    void shouldSupportComplexAndPatterns() {
        final Random random = new Random();
        Obj recordType = null;
        for (int i = 0; i < 100; i++) {
            recordType = TRec.of("name", TStr.of(), "age", TInt.of(is(gt(28))));
            if (random.nextBoolean())
                recordType = recordType.and(TRec.of("name", TStr.of()).and(recordType));
            if (random.nextBoolean())
                recordType = TRec.some().and(TRec.of("name", TStr.of())).and(TRec.of("age", TInt.of()));
        }
        recordType = TRec.of("name", TStr.of(), "age", TInt.of(is(gt(28)))).and(recordType);
        assertEquals(recordType, TRec.of("name", TStr.of(), "age", TInt.of(is(gt(28)))));
        assertTrue(recordType.isType());
        assertFalse(recordType.isReference());
        assertFalse(recordType.isInstance());
    }

    @Test
    void shouldMatchNestedRecords1() {
        final Rec person = TRec.of("name", TStr.of().binding("n1"), "age", TInt.of(),
                "phones", TRec.of(
                        "home", TInt.of().binding("h1").or(TStr.of().binding("h2")),
                        "work", TInt.of(is(gt(0))).binding("w1").or(TStr.of()))).binding("x");

        final Rec marko = TRec.of("name", "marko", "age", 29, "phones", TRec.of("home", 123, "work", 34));
        assertTrue(person.test(marko));
        assertFalse(marko.test(person));
        assertTrue(marko.test(marko));
        // TODO: assertFalse(person.test(person));
        final Bindings bindings = new Bindings();
        assertTrue(person.match(bindings, marko));
        System.out.println(person);
/*        assertEquals(4, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("n1"));
        assertEquals(TInt.of(123), bindings.get("h1"));
        assertEquals(TInt.of(34), bindings.get("w1"));
        assertEquals(marko, bindings.get("x"));
*/
    }

    @Test
    void shouldMatchNestedRecords2() {
        Rec type1 = TRec.of("name", TStr.of().binding("n"), "address", TRec.of("state", TStr.of().binding("s"), "zipcode", TInt.of().binding("z")));
        Rec rec1 = TRec.of("name", "marko", "address", TRec.of("state", "NM", "zipcode", 87506));
        assertTrue(rec1.constant());
        assertFalse(type1.constant());
        Bindings bindings = new Bindings();
        assertTrue(type1.test(rec1));
        assertEquals(0, bindings.size());
        assertTrue(type1.match(bindings, rec1));
        assertEquals(3, bindings.size());
        assertEquals(TStr.of("marko"), bindings.get("n"));
        assertEquals(TStr.of("NM"), bindings.get("s"));
        assertEquals(TInt.of(87506), bindings.get("z"));
    }

    @Test
    void shouldAndTypesAndInstances() {
        final Rec<?, ?> named = TRec.of("name", TStr.of()).symbol("named");
        final Rec<?, ?> aged = TRec.of("age", TInt.of());
        final Rec<?, ?> human = TRec.some().symbol("human");
        final Rec<?, ?> person = named.and(aged).and(human).symbol("person");
        Rec<Obj, Obj> marko = TRec.of("name", "marko");
        //
        assertNotNull(named.get());
        assertEquals("named", named.symbol());
        assertTrue(named.isType());
        assertFalse(named.isReference());
        assertFalse(named.isInstance());
        //
        assertNotNull(aged.get());
        assertEquals(Tokens.REC, aged.symbol());
        assertTrue(aged.isType());
        assertFalse(aged.isReference());
        assertFalse(aged.isInstance());
        //
        assertNull(human.get());
        assertEquals("human", human.symbol());
        assertTrue(human.isType());
        assertFalse(human.isReference());
        assertFalse(human.isInstance());
        //
        assertNotNull(person.get());
        assertEquals("person", person.symbol());
        assertTrue(person.isType());
        assertFalse(person.isReference());
        assertFalse(person.isInstance());
        //
        assertTrue(marko.isInstance());
        assertFalse(marko.isType());
        assertEquals(Tokens.REC, marko.symbol());
        assertFalse(marko.isType());
        assertFalse(marko.isReference());
        assertTrue(marko.isInstance());
        //
        assertTrue(named.test(marko));
        assertFalse(aged.test(marko));
        assertTrue(human.test(marko));
        assertFalse(person.test(marko));
        assertTrue(marko.test(marko));
        //
        marko = marko.symbol(named.symbol());
        //assertEquals(named, marko.type());
        //assertThrows(RuntimeException.class, () -> marko.type(aged));
        marko = marko.symbol(human.symbol());
        //assertEquals(human, marko.type());
        //assertThrows(RuntimeException.class, () -> marko.type(person));
        //assertThrows(RuntimeException.class, () -> marko.type(marko));
        //
        marko.put(TStr.of("age"), TInt.of(29));
        person.test(marko);
        marko = marko.symbol(person.symbol());
        assertEquals("person", marko.symbol());
        //
        Rec<Obj, Obj> marko2 = marko.plus(TRec.of("state", "ca")); // TODO!!
        assertNotEquals(marko, marko2);
        // assertEquals(person, marko2.type());
        assertTrue(person.test(marko2));
        Rec dweller = (Rec) person.and(TRec.of("state", TStr.of("nm").or(TStr.of("az"))));
        // assertEquals(person, marko2.type());
        marko2.put(TStr.of("state"), TStr.of("nm"));
        marko2 = marko2.symbol(dweller.symbol());
        assertNotEquals(marko, marko2);
        //
        /*System.out.println(named);
        System.out.println(aged);
        System.out.println(human);
        System.out.println(person);
        System.out.println(dweller);
        System.out.println(marko);
        System.out.println(marko2);*/

    }

    @Test
    void shouldSupportRecursiveTypeTesting() {
        final Rec<Obj, Obj> person = TRec.of("name", TStr.of(), "friend", TRec.some().symbol("person")).symbol("person");
        person.put(TStr.of("friend"), person);
        assertDoesNotThrow(person::toString); // check for stack overflow
        final Rec<Obj, Obj> marko = TRec.of("name", "marko");
        final Rec<Obj, Obj> kuppitz = TRec.of("name", "kuppitz", "friend", marko);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertFalse(person.test(marko));
        marko.put(TStr.of("friend"), kuppitz);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertEquals(kuppitz, marko.get(TStr.of("friend")));
        person.test(marko);
        person.test(kuppitz); // stackoverflow without type breaker
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
    }

    @Test
    void shouldSupportRecursiveTypeMatching() {
        Rec<Obj, Obj> person = TRec.of("name", TStr.of().binding("a"), "friend", TRec.some().symbol("person")).symbol("person");
        person.put(TStr.of("friend"), person.binding("b").q(qmark));
        assertDoesNotThrow(person::toString); // check for stack overflow
        final Rec<Obj, Obj> marko = TRec.of("name", "marko").symbol(person.symbol());
        final Rec<Obj, Obj> kuppitz = TRec.of("name", "kuppitz", "friend", marko).symbol(person.symbol());
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        marko.put(TStr.of("friend"), kuppitz);
        assertEquals(marko, kuppitz.get(TStr.of("friend")));
        assertEquals(kuppitz, marko.get(TStr.of("friend")));
        assertTrue(person.test(marko));
        assertTrue(person.test(kuppitz));
        ///
        Bindings bindings = new Bindings();
        assertTrue(person.match(bindings, marko));
        assertEquals(2, bindings.size());
        assertEquals("marko", bindings.get("a").get());
        assertEquals(kuppitz, bindings.get("b"));
        ///
        bindings = new Bindings();
        person.match(bindings, kuppitz);
        assertEquals(2, bindings.size());
        assertEquals("kuppitz", bindings.get("a").get());
        assertEquals(marko, bindings.get("b"));
    }

    @Test
    void shouldCarrySymbols() {
        final Rec<?, ?> person = TRec.of("name", TStr.of()).symbol("person");
        final Rec<?, ?> aged = TRec.of("age", TInt.of());
        assertEquals("person", person.symbol());
        assertEquals(Tokens.REC, aged.symbol());
        final Rec<?, ?> personAndAged = (Rec) person.and(aged);
        assertEquals(Tokens.REC, personAndAged.symbol());
        /*final PAnd and = personAndAged.get();
        assertEquals(2, and.predicates().size());
        assertEquals("person", and.<TRec>get(0).label());
        assertEquals(Tokens.REC, and.<TRec>get(1).label());*/

    }
}
