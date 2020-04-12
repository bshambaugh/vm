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

package org.mmadt.language.obj.value

import org.mmadt.language.obj.Int
import org.mmadt.language.obj.`type`.IntType
import org.mmadt.language.obj.op.map._
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait IntValue extends Int
  with ObjValue
  with Value[Int] {
  override val value: Long
  def value(java: Long): this.type = this.clone(value = java)
  override def plus(other: IntType): IntType = this.start[Int]().plus(other)
  override def plus(other: IntValue): this.type = this.clone(value = this.value + other.value, via = (this, PlusOp(other)))
  override def mult(other: IntType): IntType = this.start[Int]().mult(other)
  override def mult(other: IntValue): this.type = this.value(this.value * other.value)
  override def neg(): this.type = this.value(-this.value)
  override def one(): this.type = this.clone(value = 1L, via = (this, OneOp()))
  override def gt(other: IntValue): BoolValue = vbool(value = this.value > other.value, q = this.q, via = (this, GtOp(other)))
  override def gte(other: IntValue): BoolValue = vbool(value = this.value >= other.value, q = this.q, via = (this, GteOp(other)))
  override def lt(other: IntValue): BoolValue = vbool(value = this.value < other.value, q = this.q, via = (this, LtOp(other)))
  override def lte(other: IntValue): BoolValue =  vbool(value = this.value <= other.value, q = this.q, via = (this, LteOp(other)))
  override def zero(): this.type = this.clone(value = 0L, via = (this, ZeroOp()))
}
