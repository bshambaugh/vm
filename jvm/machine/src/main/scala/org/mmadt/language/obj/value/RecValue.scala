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

package org.mmadt.language.obj.value
import org.mmadt.language.obj.`type`.{Type, __}
import org.mmadt.language.obj.{Inst, Obj, Rec, withinQ}

import scala.util.Try

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait RecValue[A <: Obj, B <: Obj] extends PolyValue[B, Rec[A, B]] with Rec[A, B] {
  override def test(other: Obj): Boolean = other match {
    case _: Obj if !other.alive => !this.alive
    case _: __ if __.isToken(other) => Try[Boolean] {
      this.test(Inst.resolveToken(this, other))
    }.getOrElse(false)
    case arec: RecValue[A, B] => Rec.test(this, arec)
    case _: Type[_] => withinQ(this, other.domain) && (other.domain match {
      case arec: Rec[A, B] => Rec.test(this, arec)
      case x => __.isAnonObj(x)
    }) && this.compute(other).alive
    case _ => false
  }
  override def equals(other: Any): Boolean = other.isInstanceOf[RecValue[_, _]] && super[Rec].equals(other) && super[PolyValue].equals(other)
}
