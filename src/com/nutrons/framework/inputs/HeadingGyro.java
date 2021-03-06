package com.nutrons.framework.inputs;

import static com.nutrons.framework.util.FlowOperators.toFlow;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import io.reactivex.Flowable;


public class HeadingGyro {

  private final Flowable<Double> gyroReadings;
  private final ADXRS450_Gyro gyro;

  public HeadingGyro() {
    this.gyro = new ADXRS450_Gyro();
    this.gyroReadings = toFlow(() -> this.gyro.getAngle());
  }

  public void reset() {
    gyro.reset();
  }

  public double getAngle() {
    return gyro.getAngle();
  }

  /**
   * A Flowable providing data from the second joystick's y-axis.
   **/
  public Flowable<Double> getGyroReadings() {
    return this.gyroReadings;
  }
}

