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

package org.mmadt.machine.object.impl.atomic;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.inst.map.DivInst;
import org.mmadt.machine.object.impl.composite.inst.map.EqInst;
import org.mmadt.machine.object.impl.composite.inst.map.GtInst;
import org.mmadt.machine.object.impl.composite.inst.map.GteInst;
import org.mmadt.machine.object.impl.composite.inst.map.InvInst;
import org.mmadt.machine.object.impl.composite.inst.map.LtInst;
import org.mmadt.machine.object.impl.composite.inst.map.LteInst;
import org.mmadt.machine.object.impl.composite.inst.map.MinusInst;
import org.mmadt.machine.object.impl.composite.inst.map.MultInst;
import org.mmadt.machine.object.impl.composite.inst.map.NegInst;
import org.mmadt.machine.object.impl.composite.inst.map.OneInst;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.impl.composite.inst.map.ZeroInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TReal extends TObj implements Real {

    private TReal(final Object value) {
        super(value);
    }

    public static Real sym(final String label) {
        return TReal.of().bind(label);
    }

    public static Real all() {
        return new TReal(null).q(0, Integer.MAX_VALUE);
    }

    public static Real none() {
        return new TReal(null).q(0);
    }

    public static Real of(final Object... objects) {
        return ObjectHelper.make(TReal::new, objects);
    }

    @Override
    public Real max() {
        return new TReal(Float.MAX_VALUE);
    }

    @Override
    public Real min() {
        return new TReal(Float.MIN_VALUE);
    }

    @Override
    public Real one() {
        return !this.isReference() ? super.set(1.0f) : OneInst.<Real>create().attach(this); // no need to check -0.0
    }

    @Override
    public Real zero() {
        return !this.isReference() ? super.set(0.0f) : ZeroInst.<Real>create().attach(this); // no need to check -0.0
    }

    @Override
    public Real neg() {
        return this.isInstance() ? this.set(-this.java()) : NegInst.<Real>create().attach(this);
    }

    @Override
    public Real minus(final Real real) {
        return (this.isInstance() && real.isInstance()) ?
                this.set(this.java() - real.java()) :
                MinusInst.<Real>create(real).attach(this);
    }

    @Override
    public Real plus(final Real real) {
        return PlusInst.compute(this, real);
    }

    @Override
    public Real mult(final Real real) {
        return (this.isInstance() && real.isInstance()) ?
                this.set(this.java() * real.java()) :
                MultInst.<Real>create(real).attach(this);
    }

    @Override
    public Real div(final Real real) {
        return (this.isInstance() && real.isInstance()) ?
                this.set(this.java() / real.java()) :
                DivInst.<Real>create(real).attach(this);
    }

    @Override
    public Bool gt(final Real real) {
        return GtInst.compute(this, real, TBool.via(this));
    }

    @Override
    public Bool gte(final Real real) {
        return (this.isInstance() && real.isInstance()) ?
                TBool.via(this).set(this.java() >= real.java()) :
                GteInst.<Real>create(real).attach(this, TBool.via(this));
    }

    @Override
    public Bool eq(final Obj obj) {
        return this.isInstance() ?
                TBool.via(this).set(obj instanceof Real && this.java().equals(((Real) obj).java())) :
                EqInst.<Real>create(obj).attach(this, TBool.via(this));
    }

    @Override
    public Bool lt(final Real real) {
        return (this.isInstance() && real.isInstance()) ?
                TBool.via(this).set(this.java() < real.java()) :
                LtInst.<Real>create(real).attach(this, TBool.via(this));
    }

    @Override
    public Bool lte(final Real real) {
        return (this.isInstance() && real.isInstance()) ?
                TBool.via(this).set(this.java() <= real.java()) :
                LteInst.<Real>create(real).attach(this, TBool.via(this));
    }

    @Override
    public Real inv() {
        return this.isInstance() ? super.set(1.0f / this.java()) : InvInst.<Real>create().attach(this); // no need to check -0.0
    }

    @Override
    public Real set(final Object value) {
        return super.set(Float.valueOf(-0.0f).equals(value) ? 0.0f : value);
    }
}