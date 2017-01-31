package com.nutrons.framework.controllers;

import static com.ctre.CANTalon.TalonControlMode;
import static com.nutrons.framework.util.FlowOperators.toFlow;

import com.ctre.CANTalon;
import io.reactivex.Flowable;

public class Talon extends LoopSpeedController {
  private final Flowable<FeedbackEvent> feedback;
  private final CANTalon talon;

  /**
   * Creates a talon on the given port.
   */
  public Talon(int port) {
    this.talon = new CANTalon(port);
    this.feedback = toFlow(() -> () -> this.talon.getError());
  }

  /**
   * Creates a talon that initially follows another talon.
   * @param toFollow the talon to follow
   */
  public Talon(int port, Talon toFollow) {
    this(port);
    this.changeControlMode(CANTalon.TalonControlMode.Follower);
    this.set(toFollow.id());
  }

  void set(double value) {
    this.talon.set(value);
  }

  void changeControlMode(TalonControlMode mode) {
    this.talon.changeControlMode(mode);
  }

  void setLoopProperties(double setpoint, double pval,
                         double ival, double dval, double fval) {
    this.talon.setSetpoint(setpoint);
    this.talon.setPID(pval, ival, dval);
    this.talon.setF(fval);
  }

  @Override
  public Flowable<FeedbackEvent> feedback() {
    return this.feedback;
  }

  @Override
  public void accept(ControllerEvent event) {
    event.actOn(this);
  }

  int id() {
    return this.talon.getDeviceID();
  }
}