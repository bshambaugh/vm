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

package org.mmadt.language.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.value.{RecValue, Value}
import org.mmadt.storage.StorageFactory._

import scala.collection.mutable

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object TypeChecker {
  def matchesVT[O <: Obj](obj: Value[O], pattern: Type[O]): Boolean = {
    (pattern.name.equals(Tokens.obj) || pattern.name.equals(Tokens.empty) || // all objects are obj
      (!obj.name.equals(Tokens.rec) && (obj.name.equals(pattern.name) || pattern.domain().name.equals(obj.name)) && ((pattern.q == qZero && obj.q == qZero) || obj.compute(pattern).alive)) || // nominal type checking (prevent infinite recursion on recursive types) w/ structural on atomics
      obj.isInstanceOf[Strm[Obj]] || // TODO: testing a stream requires accessing its values (we need strm type descriptors associated with the strm -- or strms are only checked nominally)
      ((obj.isInstanceOf[Poly[_]] && pattern.isInstanceOf[Poly[_]] &&
        testList(obj.asInstanceOf[Poly[Obj]], pattern.asInstanceOf[Poly[Obj]]) && obj.compute(pattern).alive) || // structural type checking on records
        (obj.isInstanceOf[RecValue[_, _]] && pattern.isInstanceOf[RecType[_, _]] &&
          testRecord(obj.ground.asInstanceOf[collection.Map[Obj, Obj]], pattern.asInstanceOf[ORecType].ground) && obj.compute(pattern).alive))) && // structural type checking on records
      withinQ(obj, pattern) // must be within the type's quantified window
  }

  def matchesVV[O <: Obj](obj: Value[O], pattern: Value[O]): Boolean =
    obj.ground.equals(pattern.ground) &&
      withinQ(obj, pattern)

  def matchesTT[O <: Obj](obj: Type[O], pattern: Type[O]): Boolean = {
    ((obj.name.equals(Tokens.obj) || pattern.name.equals(Tokens.obj) || obj.name.equals(Tokens.empty) || pattern.name.equals(Tokens.empty)) || // all objects are obj
      (!obj.name.equals(Tokens.rec) && obj.name.equals(pattern.name)) ||
      (obj match {
        case recType: ORecType if pattern.isInstanceOf[RecType[_, _]] => testRecord(recType.ground, pattern.asInstanceOf[ORecType].ground)
        case lstType: Poly[Obj] => testList(lstType, pattern.asInstanceOf[Poly[Obj]])
        case _ => false
      })) &&
      obj.trace
        .map(_._2)
        .zip(pattern.trace.map(_._2))
        .map(insts => insts._1.op().equals(insts._2.op()) &&
          insts._1.args().zip(insts._2.args()).
            map(a => a._1.test(a._2)).
            fold(insts._1.args().length == insts._2.args().length)(_ && _))
        .fold(obj.trace.length == pattern.trace.length)(_ && _) &&
      withinQ(obj, pattern)
  }

  def matchesTV[O <: Obj](obj: Type[O], pattern: Value[O]): Boolean = false

  ////////////////////////////////////////////////////////

  private def testRecord(leftMap: collection.Map[Obj, Obj], rightMap: collection.Map[Obj, Obj]): Boolean = {
    if (leftMap.equals(rightMap)) return true

    val typeMap: mutable.Map[Obj, Obj] = mutable.Map() ++ rightMap

    leftMap.map(a => typeMap.find(k =>
      a._1.test(k._1) && a._2.test(k._2)).map(z => typeMap.remove(z._1))).toList

    typeMap.isEmpty || !typeMap.values.exists(x => x.q._1.ground != 0)
  }

  private def testList(leftList: Poly[Obj], rightList: Poly[Obj]): Boolean = {
    if (rightList.groundList.isEmpty || leftList.groundList.equals(rightList.groundList)) return true
    leftList.groundList.zip(rightList.groundList).foldRight(true)((a, b) => a._1.test(a._2) && b)
  }
}
