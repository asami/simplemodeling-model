package org.simplemodeling.model.statemachine

import org.goldenport.Consequence
import org.goldenport.convert.ValueReader
import org.goldenport.util.SmEnumClass

/*
 * @since   Apr.  7, 2026
 * @version Apr.  7, 2026
 * @author  ASAMI, Tomoharu
 */
enum ActivationStatus(
  val dbValue: Int
) extends StateMachine {
  case Inactive extends ActivationStatus(1)
  case Active extends ActivationStatus(2)
  case Deactivated extends ActivationStatus(3)
  case Expired extends ActivationStatus(4)

  def isOperational: Boolean =
    this == ActivationStatus.Active
}

object ActivationStatus extends SmEnumClass[ActivationStatus] {
  protected def enum_Values = values

  val default: ActivationStatus = Inactive

  object Event {
    val Activate = "activate"
    val Deactivate = "deactivate"
    val Expire = "expire"
    val Restore = "restore"
    val Renew = "renew"
  }

  private val _by_db_value = values.map(x => x.dbValue -> x).toMap

  def fromDbValue(dbvalue: Int): Option[ActivationStatus] =
    _by_db_value.get(dbvalue)

  given ValueReader[ActivationStatus] with
    def readC(v: Any): Consequence[ActivationStatus] = v match
      case m: ActivationStatus => Consequence.success(m)
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
      name = "activation_status",
      states = Vector(
        StateDef(
          Inactive.stateName,
          transitions = Vector(
            TransitionDef(Active.stateName, Event.Activate, None, Vector.empty, priority = 10),
            TransitionDef(Expired.stateName, Event.Expire, None, Vector.empty, priority = 20)
          )
        ),
        StateDef(
          Active.stateName,
          transitions = Vector(
            TransitionDef(Deactivated.stateName, Event.Deactivate, None, Vector.empty, priority = 10),
            TransitionDef(Expired.stateName, Event.Expire, None, Vector.empty, priority = 20)
          )
        ),
        StateDef(
          Deactivated.stateName,
          transitions = Vector(
            TransitionDef(Active.stateName, Event.Restore, None, Vector.empty, priority = 10),
            TransitionDef(Expired.stateName, Event.Expire, None, Vector.empty, priority = 20)
          )
        ),
        StateDef(
          Expired.stateName,
          transitions = Vector(
            TransitionDef(Active.stateName, Event.Renew, None, Vector.empty, priority = 10)
          )
        )
      ),
      initialState = default.stateName
    )

  private val _evaluator = new GuardEvaluator[ActivationStatus, EventDef] {
    def eval(
      guard: GuardExpr,
      state: ActivationStatus,
      event: EventDef
    ): Either[GuardError, Boolean] = Right(true)
  }

  def asStateMachineDef: StateMachineDef =
    _state_machine

  def transition(
    current: ActivationStatus,
    eventname: String
  ): Either[PlannerError, Option[ActivationStatus]] =
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
