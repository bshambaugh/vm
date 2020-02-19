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

package org.mmadt.language.mmlang

import org.mmadt.language.Tokens
import org.mmadt.language.obj._
import org.mmadt.language.obj.`type`.{BoolType, IntType}
import org.mmadt.language.obj.op._
import org.mmadt.language.obj.value.{BoolValue, IntValue, RecValue, StrValue}
import org.mmadt.storage.obj._

import scala.util.matching.Regex
import scala.util.parsing.combinator.JavaTokenParsers

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
object mmlangParser extends JavaTokenParsers {

  override val whiteSpace:Regex = """[\s\n]+""".r

  def parse[T](expression:String):T = this.parseAll(expr,expression).get.asInstanceOf[T]

  def expr:Parser[Any] = single | multiple | obj

  def single:Parser[O] = (obj <~ "=>") ~ objType ^^ (x => (x._1 ==> x._2).asInstanceOf[O]) // TODO: I'm improperly typing to Type (why?)
  def multiple:Parser[Iterator[O]] = (obj <~ "==>") ~ objType ^^ (x => x._1 ===> x._2)

  def canonicalType:Parser[OType] = (Tokens.bool | Tokens.int | Tokens.str | Tokens.rec) ~ (quantifier ?) ^^ {
    case atype ~ q => q.foldLeft(atype match {
      case Tokens.bool => bool
      case Tokens.int => int
      case Tokens.str => str
      case Tokens.rec => rec
    })((t,q) => t.q(q))
  }

  def objType:Parser[OType] = ((canonicalType <~ "<=") ?) ~ canonicalType ~ rep[Inst](inst) ^^ {
    case Some(range) ~ domain ~ insts => (range <= insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[OType]))
    case None ~ domain ~ insts => insts.foldLeft(domain)((x,y) => y(x).asInstanceOf[OType])
  }

  def quantifier:Parser[TQ] = ("{" ~> quantifierType <~ "}") | ("{" ~> intValue ~ (("," ~> intValue) ?) <~ "}") ^^ (x => (x._1,x._2.getOrElse(x._1)))
  def quantifierType:Parser[TQ] = ("\\*".r | "\\?".r | "\\+".r) ^^ {
    case "*" => qStar
    case "?" => qMark
    case "+" => qPlus
  }

  def obj:Parser[O] = objValue | objType
  def boolValue:Parser[BoolValue] = "true|false".r ^^ (x => bool(x.toBoolean))
  def intValue:Parser[IntValue] = wholeNumber ^^ (x => int(x.toLong))
  def strValue:Parser[StrValue] = ("""'([^'\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*'""").r ^^ (x => str(x.subSequence(1,x.length - 1).toString))
  def recValue:Parser[RecValue[O,O]] = "[" ~> repsep((obj <~ (":" | "->")) ~ obj,("," | "|")) <~ "]" ^^ (x => rec(x.reverse.map(o => (o._1,o._2)).toMap))
  def objValue:Parser[OValue] = (boolValue | intValue | strValue | recValue) ~ (quantifier ?) ^^ (x => x._1.q(x._2.getOrElse(qOne)))

  def inst:Parser[Inst] = chooseSugar | sugarlessInst
  def chooseSugar:Parser[Inst] = recValue ^^ (x => ChooseOp(x.asInstanceOf[RecValue[OType,O]]))
  def sugarlessInst:Parser[Inst] = "[" ~> ("""[a-zA-Z][a-zA-Z0-9]*""".r <~ ",") ~ obj <~ "]" ^^ { // TODO: (hint:Option[OType] = None) (so users don't have to prefix their instruction compositions with a domain)
    case op ~ arg => op match {
      case Tokens.plus => arg match {
        case arg:IntValue => PlusOp(arg)
        case arg:IntType => PlusOp(arg)
        case arg:StrValue => PlusOp(arg)
      }
      case Tokens.mult => arg match {
        case arg:IntValue => MultOp(arg)
        case arg:IntType => MultOp(arg)
        case arg:StrValue => MultOp(arg)
      }
      case Tokens.gt => arg match {
        case arg:IntValue => GtOp(arg)
        case arg:StrValue => GtOp(arg)
      }
      case Tokens.is => arg match {
        case arg:BoolValue => IsOp(arg)
        case arg:BoolType => IsOp(arg)
      }
      case Tokens.choose => ChooseOp(arg.asInstanceOf[RecValue[OType,O]])
    }
  }
}

/*object LocalApp extends App {
  override def main(args:Array[String]):Unit ={
    mmlangParser.parseAll(mmlangParser.expr,"['marko':'44']") match {
      case mmlangParser.Success(result,_) => println(result + ":" + result.getClass)
      case _ => println("Could not parse the input string.")
    }
  }
}*/