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
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.{Inst, Obj, Rec}
import org.mmadt.storage.obj.qOne
import org.mmadt.storage.obj.value.VInst

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GetOp[A <: Obj, B <: Obj] {
  this: Rec[A, B] =>

  def get(key: A): B //
  def get[BT <: Type[BT]](key: A, btype: BT): BT //
}

object GetOp {
  def apply[A <: Obj, B <: Obj](key: A): Inst = new VInst((Tokens.get, List(key)), qOne, ((a: Rec[A, B], b: List[Obj]) => a.get(key)).asInstanceOf[(Obj, List[Obj]) => Obj]) //
}