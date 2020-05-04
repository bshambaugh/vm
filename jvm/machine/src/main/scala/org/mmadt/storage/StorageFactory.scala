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

package org.mmadt.storage

import java.util.ServiceLoader

import org.mmadt.language.Tokens
import org.mmadt.language.obj.`type`.{BoolType, _}
import org.mmadt.language.obj.value._
import org.mmadt.language.obj.value.strm._
import org.mmadt.language.obj.{ViaTuple, _}
import org.mmadt.storage.StorageFactory.{qOne, qZero}
import org.mmadt.storage.obj.OPoly
import org.mmadt.storage.obj.`type`._
import org.mmadt.storage.obj.value._
import org.mmadt.storage.obj.value.strm._
import org.mmadt.storage.obj.value.strm.util.MultiSet


/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
trait StorageFactory {
  /////////TYPES/////////
  lazy val zeroObj: ObjType = tobj().q(qZero)
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj]: RecType[A, B] = trec(value = Map.empty[A, B])
  def lst[A <: Obj]: LstType[A] = tlst()
  def `|`[A <: Obj](values: A*): Poly[A] = new OPoly[A](ground = ("|", values.toList))
  def `;`[A <: Obj](values: A*): Poly[A] = new OPoly[A](ground = (";", values.toList))
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base()): ObjType
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base()): BoolType
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base()): IntType
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base()): RealType
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base()): StrType
  def trec[A <: Obj, B <: Obj](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base()): RecType[A, B]
  def trec[A <: Obj, B <: Obj](value: (A, B), values: (A, B)*): RecType[A, B] = trec[A, B](value = (value +: values).toMap)
  def tlst[A <: Obj](name: String = Tokens.lst, value: List[A] = List.empty[A], q: IntQ = qOne, via: ViaTuple = base()): LstType[A]
  def tlst[A <: Obj](value: A, values: A*): LstType[A] = tlst[A](value = (value +: values).toList)
  /////////VALUES/////////
  def obj(value: Any): ObjValue
  def bool(value: Boolean): BoolValue = vbool(value = value)
  def int(value: Long): IntValue = vint(value = value)
  def real(value: Double): RealValue = vreal(value = value)
  def str(value: String): StrValue = vstr(value = value)
  def vrec[A <: Value[Obj], B <: Value[Obj]](_value: collection.Map[A, B]): RecValue[A, B] = vrec(value = _value)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: (A, B), values: (A, B)*): RecValue[A, B] = vrec(value = (value +: values).toMap)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value1: RecValue[A, B], value2: RecValue[A, B], valuesN: RecValue[A, B]*): RecStrm[A, B] = vrec((List(value1, value2) ++ valuesN).iterator)
  def vlst[A <: Value[Obj]](): LstValue[A] = vlst(value = List.empty[A])
  def vlst[A <: Value[Obj]](value: A, values: A*): LstValue[A] = vlst(value = (value +: values).toList)
  //
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm
  def vrec[A <: Value[Obj], B <: Value[Obj]](value: Iterator[RecValue[A, B]]): RecStrm[A, B]
  def vbool(name: String = Tokens.bool, value: Boolean, q: IntQ = qOne, via: ViaTuple = base()): BoolValue
  def vint(name: String = Tokens.int, value: Long, q: IntQ = qOne, via: ViaTuple = base()): IntValue
  def vreal(name: String = Tokens.real, value: Double, q: IntQ = qOne, via: ViaTuple = base()): RealValue
  def vstr(name: String = Tokens.str, value: String, q: IntQ = qOne, via: ViaTuple = base()): StrValue
  def vlst[A <: Value[Obj]](name: String = Tokens.lst, value: List[A] = List.empty[A], q: IntQ = qOne, via: ViaTuple = base()): LstValue[A]
  def vrec[A <: Value[Obj], B <: Value[Obj]](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base()): RecValue[A, B]
  //
  def strm[O <: Obj](itty: Seq[O]): OStrm[O]
  def strm[O <: Obj]: OStrm[O]
}

object StorageFactory {
  ///////PROVIDERS///////
  val providers: ServiceLoader[StorageProvider] = ServiceLoader.load(classOf[StorageProvider])
  /////////TYPES/////////
  lazy val zeroObj: ObjType = tobj().q(qZero)
  lazy val obj: ObjType = tobj()
  lazy val bool: BoolType = tbool()
  lazy val int: IntType = tint()
  lazy val real: RealType = treal()
  lazy val str: StrType = tstr()
  def rec[A <: Obj, B <: Obj]: RecType[A, B] = trec(ground = Map.empty[A, B])
  def lst[A <: Obj]: LstType[A] = tlst(ground = List.empty[A])
  def `|`[A <: Obj](values: A*)(implicit f: StorageFactory): Poly[A] = f.`|`[A](values: _*)
  def `;`[A <: Obj](values: A*)(implicit f: StorageFactory): Poly[A] = f.`;`[A](values: _*)
  //
  def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): ObjType = f.tobj(name, q, via)
  def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): BoolType = f.tbool(name, q, via)
  def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): IntType = f.tint(name, q, via)
  def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RealType = f.treal(name, q, via)
  def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): StrType = f.tstr(name, q, via)
  def trec[A <: Obj, B <: Obj](name: String = Tokens.rec, ground: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RecType[A, B] = f.trec(name, ground, q, via)
  def trec[A <: Obj, B <: Obj](ground: (A, B), grounds: (A, B)*)(implicit f: StorageFactory): RecType[A, B] = f.trec(ground, grounds: _*)
  def tlst[A <: Obj](name: String = Tokens.lst, ground: List[A] = List.empty[A], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): LstType[A] = f.tlst(name, ground, q, via)
  def tlst[A <: Obj](value: A, values: A*)(implicit f: StorageFactory): LstType[A] = f.tlst(value, values: _*)
  /////////VALUES/////////
  def obj(ground: Any)(implicit f: StorageFactory): ObjValue = f.obj(ground)
  def bool(ground: Boolean)(implicit f: StorageFactory): BoolValue = f.bool(ground)
  def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*)(implicit f: StorageFactory): BoolStrm = f.bool(value1, value2, valuesN: _*)
  def int(ground: Long)(implicit f: StorageFactory): IntValue = f.int(ground)
  def int(value1: IntValue, value2: IntValue, valuesN: IntValue*)(implicit f: StorageFactory): IntStrm = f.int(value1, value2, valuesN: _*)
  def real(ground: Double)(implicit f: StorageFactory): RealValue = f.vreal(Tokens.real, ground, qOne)
  def real(ground: Float)(implicit f: StorageFactory): RealValue = f.vreal(Tokens.real, ground.doubleValue(), qOne)
  def real(value1: RealValue, value2: RealValue, valuesN: RealValue*)(implicit f: StorageFactory): RealStrm = f.real(value1, value2, valuesN: _*)
  def str(ground: String)(implicit f: StorageFactory): StrValue = f.vstr(Tokens.str, ground, qOne)
  def str(value1: StrValue, value2: StrValue, valuesN: StrValue*)(implicit f: StorageFactory): StrStrm = f.str(value1, value2, valuesN: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](ground: collection.Map[A, B])(implicit f: StorageFactory): RecValue[A, B] = f.vrec(Tokens.rec, ground, qOne)
  def vrec[A <: Value[Obj], B <: Value[Obj]](ground: (A, B), grounds: (A, B)*)(implicit f: StorageFactory): RecValue[A, B] = f.vrec(ground, grounds: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](value1: RecValue[A, B], value2: RecValue[A, B], valuesN: RecValue[A, B]*)(implicit f: StorageFactory): RecStrm[A, B] = f.vrec(value1, value2, valuesN: _*)
  def vrec[A <: Value[Obj], B <: Value[Obj]](values: Iterator[RecValue[A, B]])(implicit f: StorageFactory): RecStrm[A, B] = f.vrec(values)
  def vlst[A <: Value[Obj]]()(implicit f: StorageFactory): LstValue[A] = f.vlst(value = List.empty[A])
  def vlst[A <: Value[Obj]](name: String = Tokens.lst, ground: List[A], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): LstValue[A] = f.vlst(name, ground, q, via)
  def vlst[A <: Value[Obj]](ground: A, grounds: A*)(implicit f: StorageFactory): LstValue[A] = f.vlst(value = (ground +: grounds).toList)
  //
  def vbool(name: String = Tokens.bool, ground: Boolean, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): BoolValue = f.vbool(name, ground, q, via)
  def vint(name: String = Tokens.int, ground: Long, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): IntValue = f.vint(name, ground, q, via)
  def vreal(name: String = Tokens.real, ground: Double, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RealValue = f.vreal(name, ground, q, via)
  def vstr(name: String = Tokens.str, ground: String, q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): StrValue = f.vstr(name, ground, q, via)
  def vrec[A <: Value[Obj], B <: Value[Obj]](name: String = Tokens.rec, ground: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base())(implicit f: StorageFactory): RecValue[A, B] = f.vrec(name, ground, q, via)
  def strm[O <: Obj](seq: Seq[O])(implicit f: StorageFactory): OStrm[O] = f.strm[O](seq)
  def strm[O <: Obj](implicit f: StorageFactory): OStrm[O] = f.strm[O]
  /////////CONSTANTS//////
  lazy val btrue: BoolValue = bool(ground = true)
  lazy val bfalse: BoolValue = bool(ground = false)
  lazy val qZero: (IntValue, IntValue) = (int(0), int(0))
  lazy val qOne: (IntValue, IntValue) = (int(1), int(1))
  lazy val qMark: (IntValue, IntValue) = (int(0), int(1))
  lazy val qPlus: (IntValue, IntValue) = (int(1), int(Long.MaxValue))
  lazy val qStar: (IntValue, IntValue) = (int(0), int(Long.MaxValue))
  lazy val * : (IntValue, IntValue) = qStar
  lazy val ? : (IntValue, IntValue) = qMark
  lazy val + : (IntValue, IntValue) = qPlus
  def asType[O <: Obj](obj: O): OType[O] = (obj match {
    case apoly: Poly[_] if apoly.isValue => apoly.clone(ground = (apoly.ground._1, apoly.groundList.map(x => asType[Obj](x))))
    case atype: Type[_] => atype
    case _: IntValue | _: IntStrm => tint(name = obj.name, q = obj.q)
    case _: RealValue | _: RealStrm => treal(name = obj.name, q = obj.q)
    case _: StrValue | _: StrStrm => tstr(name = obj.name, q = obj.q)
    case _: BoolValue | _: BoolStrm => tbool(name = obj.name, q = obj.q)
    case _: RecStrm[_, _] => trec(name = obj.name, ground = Map.empty, q = obj.q)
    case recval: RecValue[_, _] => trec(name = recval.name, ground = recval.ground, q = recval.q)
    case lstval: LstValue[_] => tlst(name = lstval.name, ground = lstval.ground, q = lstval.q)

  }).asInstanceOf[OType[O]]
  def isSymbol[O <: Obj](obj: O): Boolean = obj match {
    case _: Value[_] => false
    case atype: Type[_] => atype.root && atype.getClass.equals(tobj().getClass) && !atype.name.equals(Tokens.obj) && !atype.name.equals(Tokens.empty)
  }
  implicit val mmstoreFactory: StorageFactory = new StorageFactory {
    //override def :|[A <: Obj](values: A*): Poly[A] = new OPoly[A](ground = values.toList)
    /////////TYPES/////////
    override def tobj(name: String = Tokens.obj, q: IntQ = qOne, via: ViaTuple = base()): ObjType = new TObj(name, q, via)
    override def tbool(name: String = Tokens.bool, q: IntQ = qOne, via: ViaTuple = base()): BoolType = new TBool(name, q, via)
    override def tint(name: String = Tokens.int, q: IntQ = qOne, via: ViaTuple = base()): IntType = new TInt(name, q, via)
    override def treal(name: String = Tokens.real, q: IntQ = qOne, via: ViaTuple = base()): RealType = new TReal(name, q, via)
    override def tstr(name: String = Tokens.str, q: IntQ = qOne, via: ViaTuple = base()): StrType = new TStr(name, q, via)
    override def trec[A <: Obj, B <: Obj](name: String = Tokens.rec, value: collection.Map[A, B], q: IntQ = qOne, via: ViaTuple = base()): RecType[A, B] = new TRec[A, B](name, value, q, via)
    override def tlst[A <: Obj](name: String = Tokens.lst, value: List[A], q: IntQ = qOne, via: ViaTuple = base()): LstType[A] = new TLst[A](name, value, q, via)
    /////////VALUES/////////
    override def int(ground: Long): IntValue = new VInt(ground = ground) // NECESSARY FOR Q PRELOAD
    override def obj(ground: Any): ObjValue = new VObj(ground = ground)
    override def vbool(name: String, ground: Boolean, q: IntQ, via: ViaTuple): BoolValue = new VBool(name, ground, q, via)
    override def bool(value1: BoolValue, value2: BoolValue, valuesN: BoolValue*): BoolStrm = new VBoolStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vint(name: String, ground: Long, q: IntQ, via: ViaTuple): IntValue = new VInt(name, ground, q, via)
    override def int(value1: IntValue, value2: IntValue, valuesN: IntValue*): IntStrm = new VIntStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vreal(name: String, value: Double, q: IntQ, via: ViaTuple): RealValue = new VReal(name, value, q, base())
    override def real(value1: RealValue, value2: RealValue, valuesN: RealValue*): RealStrm = new VRealStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vstr(name: String, ground: String, q: IntQ, via: ViaTuple): StrValue = new VStr(name, ground, q, base())
    override def str(value1: StrValue, value2: StrValue, valuesN: StrValue*): StrStrm = new VStrStrm(values = MultiSet(value1 +: (value2 +: valuesN)))
    override def vrec[A <: Value[Obj], B <: Value[Obj]](name: String, ground: collection.Map[A, B], q: IntQ, via: ViaTuple): RecValue[A, B] = new VRec[A, B](name, ground, q, via)
    override def vrec[A <: Value[Obj], B <: Value[Obj]](values: Iterator[RecValue[A, B]]): RecStrm[A, B] = new VRecStrm(values = MultiSet(values.toSeq))
    override def vlst[A <: Value[Obj]](name: String, ground: List[A], q: IntQ, via: ViaTuple): LstValue[A] = new VLst[A](name, ground, q, via)
    //
    override def strm[O <: Obj]: OStrm[O] = VEmptyStrm.empty[O]
    override def strm[O <: Obj](values: Seq[O]): OStrm[O] = {
      (values.headOption.getOrElse(null) match {
        case _: Bool => new VBoolStrm(values = MultiSet[BoolValue](values.asInstanceOf[Seq[BoolValue]]))
        case _: Int => new VIntStrm(values = MultiSet(values.asInstanceOf[Seq[IntValue]]))
        case _: Real => new VRealStrm(values = MultiSet(values.asInstanceOf[Seq[RealValue]]))
        case _: Str => new VStrStrm(values = MultiSet(values.asInstanceOf[Seq[StrValue]]))
        case _: Rec[_, _] => new VRecStrm[Value[Obj], Value[Obj]](values = MultiSet(values.asInstanceOf[Seq[RecValue[Value[Obj], Value[Obj]]]]))
        case _: Poly[_] => new VPolyStrm[Obj](values = MultiSet(values.asInstanceOf[Seq[Poly[Obj]]]))
        case _ => VEmptyStrm.empty[O]
      }).asInstanceOf[OStrm[O]]
    }
  }
}
