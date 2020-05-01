package org.mmadt.storage.obj.value.strm.util

import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.value.strm.Strm
import org.mmadt.language.obj.{Obj, _}
import org.mmadt.storage.StorageFactory._



class MultiSet[A <: Obj](val baseSet: Set[A] = Set.empty[A]) extends Seq[A] {
  def get(a: A): Option[A] = baseSet.find(b => a.asInstanceOf[Value[_]].value.equals(b.asInstanceOf[Value[_]].value))
  def put(a: A): MultiSet[A] = {
    val oldObj:Option[A] = this.get(a)
    new MultiSet[A](oldObj.map(x => baseSet-x).getOrElse(baseSet) + oldObj.map(x => x.clone(q=plusQ(a, x))).getOrElse(a))
  }
  def objSize: Long = baseSet.size
  def qSize: IntQ = baseSet.foldRight(qZero)((a, b) => plusQ(a.q, b))
  override def length: scala.Int = objSize.toInt
  override def apply(idx: scala.Int): A = this.baseSet.toSeq.apply(idx)
  override def iterator: Iterator[A] = this.baseSet.iterator
  override def toSeq: Seq[A] = baseSet.toSeq

  override def hashCode: scala.Int = baseSet.hashCode()
  override def equals(other: Any): Boolean = other match {
    case multiSet: MultiSet[_] => multiSet.baseSet == this.baseSet
    case _ => false
  }
}

object MultiSet {
  def put[A <: Obj](objs: A*): MultiSet[A] = objs.foldLeft(new MultiSet[A])((a, b) => a.put(b))
  def apply[A <: Obj](objs: Seq[A]): MultiSet[A] = objs.foldLeft(new MultiSet[A])((a, b) => a.put(b))
  def test(a: Strm[Obj], b: Strm[Obj]): Boolean = MultiSet(a.values) == MultiSet(b.values)
}
