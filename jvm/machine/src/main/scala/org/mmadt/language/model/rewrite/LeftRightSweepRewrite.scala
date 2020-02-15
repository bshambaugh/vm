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

package org.mmadt.language.model.rewrite

import org.mmadt.language.model.Model
import org.mmadt.language.obj.Obj
import org.mmadt.language.obj.`type`.Type
import org.mmadt.processor.Traverser
import org.mmadt.processor.obj.`type`.C1Traverser

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object LeftRightSweepRewrite {
  private type EType = Obj with Type[_]

  def rewrite[T <: Type[_]](model: Model, startType: Type[_], endType: T): Traverser[T] = {
    var mutatingTraverser: Traverser[T] = new C1Traverser[T](startType.asInstanceOf[T])
    var previousTraverser: Traverser[T] = new C1Traverser[T](endType)
    while (previousTraverser != mutatingTraverser) {
      mutatingTraverser = previousTraverser
      previousTraverser = rewrite2(model, mutatingTraverser.obj().asInstanceOf[EType], startType, new C1Traverser(startType.asInstanceOf[T]))._2
    }
    mutatingTraverser
  }

  @scala.annotation.tailrec
  private def rewrite2[T <: Obj](model: Model, atype: EType, btype: EType, traverser: Traverser[T]): (EType, Traverser[T]) = {
    if (atype.insts().nonEmpty) {
      model.get(atype) match {
        case Some(right: EType) => rewrite2(model, right, btype, traverser)
        case None => rewrite2(model,
          atype.rinvert(),
          atype.insts().last._2.apply(atype.range(), atype.insts().last._2.args()).asInstanceOf[EType].compose(btype),
          traverser)
      }
    } else if (btype.insts().nonEmpty) rewrite2(model, btype.linvert(), btype.linvert().domain(), traverser.apply(btype).asInstanceOf[Traverser[T]])
    else (atype, traverser)
  }
}