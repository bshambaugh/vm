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

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{Type, TypeChecker}
import org.mmadt.language.obj.value.{IntValue, StrValue, Value}
import org.mmadt.storage.StorageFactory._

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class VInst(java:InstTuple,quantifier:IntQ,function:(Obj,List[Obj]) => Obj) extends AbstractVObj(Tokens.inst,java,quantifier) with Inst {
  override def as[O <: Obj](name:String):O = this.asInstanceOf[O] //
  def this(java:InstTuple) = this(java,qOne,null) //
  override def value():InstTuple = java //
  override def q(quantifier:IntQ):this.type = new VInst(java,quantifier,function).asInstanceOf[this.type] //
  override def id():this.type = this //
  override def apply(obj:Obj,args:List[Obj]):Obj = function.apply(obj,args) //
  override def count():IntValue = this.q()._2
  override def quant():IntValue = this.q()._2
  // pattern matching methods TODO: GUT WHEN VINST JOINS HEIRARCHY
  def test(other:Obj):Boolean = this match {
    case startValue:Value[Obj] => other match {
      case argValue:Value[Obj] => TypeChecker.matchesVV(startValue,argValue)
      case argType:Type[Obj] => TypeChecker.matchesVT(startValue,argType)
    }
    case startType:Type[Obj] => other match {
      case argValue:Value[Obj] => TypeChecker.matchesTV(startType,argValue)
      case argType:Type[Obj] => TypeChecker.matchesTT(startType,argType)
    }
  }
  override def mmkv(file:StrValue):Rec[Str,Obj] = null
}
