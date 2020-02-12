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

package org.mmadt.storage.obj.value

import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{Inst, JInst, Obj, TQ}
import org.mmadt.language.{Stringer, Tokens}
import org.mmadt.storage.obj._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VInst(java: JInst, quantifier: TQ, function: (Obj, List[Obj]) => Obj) extends VObj(Tokens.inst, java, quantifier) with Inst {

  override def as[O <:Obj](name: String): O = this.asInstanceOf[O] //

  def this(java: JInst) = this(java, qOne, null) //
  override def value(): JInst = java //
  override def toString: String = Stringer.instString(this) //
  override def q(quantifier: TQ): this.type = new VInst(java, quantifier, function).asInstanceOf[this.type] //
  override def apply(obj: Obj, args: List[Obj]): Obj = function.apply(obj, args) //

  // pattern matching methods TODO: GUT WHEN VINST JOINS HEIRARCHY
  def test(other: Obj): Boolean = this match {
    case startValue: Value[_] => other match {
      case argValue: Value[_] => TypeChecker.matchesVV(startValue, argValue)
      case argType: Type[_] => TypeChecker.matchesVT(startValue, argType)
    }
    case startType: Type[_] => other match {
      case argValue: Value[_] => TypeChecker.matchesTV(startType, argValue)
      case argType: Type[_] => TypeChecker.matchesTT(startType, argType)
    }
  }

}