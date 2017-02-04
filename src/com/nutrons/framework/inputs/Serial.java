package com.nutrons.framework.inputs;

import static com.nutrons.framework.util.FlowOperators.toFlow;

import edu.wpi.first.wpilibj.SerialPort;
import io.reactivex.Flowable;

/**
 * A wrapper around WPI's SerialPort class which provides
 * Flowables for data sent to the roboRIO over serial.
 */
public class Serial {

  private static final int DEFAULT_BAUD_RATE = 9600;
  private static final char DEFAULT_TERMINATION_CHARACTER = '\n';
  private final SerialPort serial;
  private final int bufferSize;
  private final int packetLength;
  private final Flowable<byte[]> dataStream;
  private final char terminationCharacter;

  /**
   * Create Serial streams from a WPI Serial.
   *
   * @param bufferSize represents how many bytes to cache unread before clearing buffer
   * @param packetLength represents the length of each read from the buffer
   */
  public Serial(int bufferSize, int packetLength) {
    this(DEFAULT_BAUD_RATE, SerialPort.Port.kUSB, bufferSize, packetLength,
        DEFAULT_TERMINATION_CHARACTER);
  }

  /**
   * Create Serial streams from a WPI Serial.
   *
   * @param bufferSize represents how many bytes to cache unread before clearing buffer
   * @param packetLength represents the length of each read from the buffer
   * @param terminationCharacter allows users to set a custom termination character - default is the
   * newline character '\n'
   */
  public Serial(SerialPort.Port port,
      int bufferSize,
      int packetLength,
      char terminationCharacter) {
    this(DEFAULT_BAUD_RATE, port, bufferSize, packetLength, terminationCharacter);
  }

  /**
   * Create Serial streams from a WPI Serial
   *
   * @param bufferSize represents how many bytes to cache unread before clearing buffer
   * @param packetLength represents the length of each read from the buffer
   * @param terminationCharacter allows users to set a custom termination character - default is the
   * newline character '\n'
   * @param baudrate See <a href="https://en.wikipedia.org/wiki/Symbol_rate">Symbol Rate</a>
   */
  public Serial(int baudrate,
      SerialPort.Port port,
      int bufferSize,
      int packetLength,
      char terminationCharacter) {
    this.serial = new SerialPort(baudrate, port);
    this.bufferSize = bufferSize;
    this.packetLength = packetLength;
    this.terminationCharacter = terminationCharacter;

    this.serial.enableTermination(terminationCharacter);

    this.dataStream = toFlow(() -> {
      if (serial.getBytesReceived() > this.bufferSize) { //Clear out old values
        serial.reset();
      }
      return serial.read(packetLength);
    }).filter(x -> x.length == packetLength);
  }

  /**
   * A Flowable providing data from the serial.
   **/
  public Flowable<byte[]> getDataStream() {
    return this.dataStream;
  }
}