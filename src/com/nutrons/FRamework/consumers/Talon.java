package com.nutrons.FRamework.consumers;

import com.ctre.CANTalon;

public class Talon extends LoopSpeedController {
  private CANTalon talon;

  public Talon(CANTalon talon) {
    this.talon = talon;
  }

  @Override
  public void onNext(LoopControllerEvent motorEvent) {
    motorEvent.actOn(this);
  }

  @Override
  void set(double value) {

  }

  void setMode(CANTalon.TalonControlMode mode) {
    this.talon.changeControlMode(mode);
  }

  @Override
  void setLoopProperties(double p, double i, double d, double f) {
    this.talon.setPID(p, i, d);
    this.talon.setF(f);
  }
}
