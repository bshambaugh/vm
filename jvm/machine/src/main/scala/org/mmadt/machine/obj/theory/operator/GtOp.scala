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

package org.mmadt.machine.obj.theory.operator

import org.mmadt.machine.obj.theory.obj.{Bool, Obj}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait GtOp[J, O <: Obj with GtOp[J, O]] {
  this: Obj with GtOp[J, O] =>

  def gt(other: J): Bool = this.gt(this.value[J, O](other)) //
  def gt(other: O): Bool //

  final def >(other: J): Bool = this.gt(other) //
  final def >(other: O): Bool = this.gt(other) //
}
