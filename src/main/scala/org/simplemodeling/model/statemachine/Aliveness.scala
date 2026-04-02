package org.simplemodeling.model.statemachine

import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.util.SmEnumClass

/*
 * @since   Aug.  2, 2025
 *  version Mar. 29, 2026
 * @version Apr.  3, 2026
 * @author  ASAMI, Tomoharu
 */
enum Aliveness(
  val dbValue: Int
) extends StateMachine {
  case Alive extends Aliveness(1)
  case Suspended extends Aliveness(2)
  case Dead extends Aliveness(3)

  def isOperational: Boolean =
    this == Aliveness.Alive
}

object Aliveness extends SmEnumClass[Aliveness] {
  protected def enum_Values = values

  val default: Aliveness = Alive

  object Event {
    val Suspend = "suspend"
    val Resume = "resume"
    val Terminate = "terminate"
  }

  private val _by_db_value = values.map(x => x.dbValue -> x).toMap

  def fromDbValue(dbvalue: Int): Option[Aliveness] =
    _by_db_value.get(dbvalue)

  given ValueReader[Aliveness] with
    def readC(v: Any): Consequence[Aliveness] = v match
      case m: Aliveness => Consequence.success(m)
      case n: Int =>
        fromDbValue(n).map(Consequence.success).getOrElse(Consequence.failValueInvalid(v, org.goldenport.schema.XInt))
      case n: Long if n.isValidInt =>
        readC(n.toInt)
      case s: String =>
        s.trim.toIntOption.flatMap(fromDbValue).orElse(from(s)).map(Consequence.success).getOrElse(Consequence.failValueInvalid(v, org.goldenport.schema.XString))
      case _ =>
        Consequence.failValueInvalid(v, org.goldenport.schema.XString)

  private val _state_machine =
    StateMachineDef(
      name = "aliveness",
      states = Vector(
        StateDef(
          Alive.stateName,
          transitions = Vector(
            TransitionDef(Suspended.stateName, Event.Suspend, None, Vector.empty, priority = 10),
            TransitionDef(Dead.stateName, Event.Terminate, None, Vector.empty, priority = 20)
          )
        ),
        StateDef(
          Suspended.stateName,
          transitions = Vector(
            TransitionDef(Alive.stateName, Event.Resume, None, Vector.empty, priority = 10),
            TransitionDef(Dead.stateName, Event.Terminate, None, Vector.empty, priority = 20)
          )
        ),
        StateDef(
          Dead.stateName,
          transitions = Vector.empty
        )
      ),
      initialState = default.stateName
    )

  private val _evaluator = new GuardEvaluator[Aliveness, EventDef] {
    def eval(
      guard: GuardExpr,
      state: Aliveness,
      event: EventDef
    ): Either[GuardError, Boolean] = Right(true)
  }

  def asStateMachineDef: StateMachineDef =
    _state_machine

  def transition(
    current: Aliveness,
    eventname: String
  ): Either[PlannerError, Option[Aliveness]] =
    DeterministicPlanner
      .select(
        sm = _state_machine,
        currentstate = current.stateName,
        eventname = eventname,
        state = current,
        event = EventDef(eventname),
        evaluator = _evaluator
      )
      .map(_.flatMap(x => from(x.transition.to)))
}
