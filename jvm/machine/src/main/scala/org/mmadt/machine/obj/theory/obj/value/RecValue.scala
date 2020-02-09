/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.machine.obj.theory.obj.value

import org.mmadt.machine.obj.impl.traverser.RecursiveTraverser
import org.mmadt.machine.obj.theory.obj.`type`.{RecType, Type}
import org.mmadt.machine.obj.theory.obj.{Bool, Obj, Rec}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecValue[A <: Obj, B <: Obj] extends Rec[A, B]
  with Value[RecValue[A, B]] {

  override def value(): Map[A, B] //
  override def start(): RecType[A, B] //

  override def to(label: StrValue): RecType[A, B] = this.start().to(label) //

  override def is(bool: Bool): Rec[A, B] = {
    try if (bool.value()) this else this.q(0)
    catch {
      case _: IllegalAccessException => this.start().is(bool)
    }
  }

  override def plus(other: Rec[A, B]): Rec[A, B] = {
    try other.value() ++ this.value()
    catch {
      case _: IllegalAccessException => this.start().plus(other)
    }
  }

  override def get(key: A): B = this.value().get(key) match {
    case Some(bvalue: Value[_]) => bvalue.asInstanceOf[B]
    case Some(btype: Type[_]) => new RecursiveTraverser(key).apply(btype).obj()
    case None => throw new NoSuchElementException("The rec does not have a value for the key: " + key)
    case _ => throw new RuntimeException()
  }

  override def get[BT <: Type[BT]](key: A, btype: BT): BT = this.get(key).asInstanceOf[BT]
}
