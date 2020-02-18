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

package org.mmadt.language.obj.op

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{BoolType, Type}
import org.mmadt.language.obj.value.Value
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait EqOp[O <: Obj with EqOp[O,V,T],V <: Value[V],T <: Type[T]] {
  this:O =>

  def eqs(other:T):BoolType //
  def eqs(other:V):Bool //
  // final def ===(other: T): BoolType = this.eq(other) //
  // final def ===(other: V): Bool = this.eq(other) //
}

object EqOp {
  def apply[O <: Obj with EqOp[O,V,T],V <: Value[V],T <: Type[T]](other:V):Inst = new VInst((Tokens.eq,List(other)),qOne,((a:O,b:List[Obj]) => a.eqs(other)).asInstanceOf[(Obj,List[Obj]) => Obj]) //
  def apply[O <: Obj with EqOp[O,V,T],V <: Value[V],T <: Type[T]](other:T):Inst = new VInst((Tokens.eq,List(other)),qOne,((a:O,b:List[Obj]) => b.head match {
    case v:OValue with V => a.eqs(v)
    case t:OType with T => a.eqs(t)
  }).asInstanceOf[(Obj,List[Obj]) => Obj])
}