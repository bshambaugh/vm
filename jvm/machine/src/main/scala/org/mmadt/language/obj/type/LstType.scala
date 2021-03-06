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

package org.mmadt.language.obj.`type`
import org.mmadt.language.obj.{Inst, Lst, Obj, withinQ}

trait LstType[A <: Obj] extends PolyType[A, Lst[A]] with Lst[A] {
  override def test(other: Obj): Boolean = other match {
    case _: Obj if !other.alive => !this.alive
    case _: __ if __.isTokenRoot(other) =>
      val temp = Inst.resolveToken(this, other)
      if (temp == other) true else this.test(temp)
    case _: Type[_] => withinQ(this, other.domain) && (other.domain match {
      case alst: Lst[A] => Lst.test(this, alst)
      case x => __.isAnonObj(x)
    })
    case _ => false
  }
  override def equals(other: Any): Boolean = other.isInstanceOf[LstType[_]] && super[Lst].equals(other) && super[PolyType].equals(other)
}



