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

package org.mmadt.language.obj.op.trace

import org.mmadt.language.Tokens
import org.mmadt.language.obj.Inst.Func
import org.mmadt.language.obj.op.TraceInstruction
import org.mmadt.language.obj.{Inst, Obj, _}
import org.mmadt.storage.obj.value.VInst

trait JuxtaOp {
  this: Obj =>
  def juxta[A <: Obj](right: A): A = JuxtaOp(right).exec(this)
  def `=>`[A <: Obj](right: A): A = this.juxta(right)
}
object JuxtaOp extends Func[Obj, Obj] {
  def apply[A <: Obj](right: A): Inst[Obj, A] = new VInst[Obj, A](g = (Tokens.juxt, List(right)), func = this) with TraceInstruction
  override def apply(start: Obj, inst: Inst[Obj, Obj]): Obj = start.compute(inst.arg0[Obj]).hardQ(multQ(start.q, inst.arg0[Obj].q))
}