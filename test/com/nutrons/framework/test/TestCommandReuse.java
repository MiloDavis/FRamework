package com.nutrons.framework.test;

import static com.nutrons.framework.test.TestCommand.waitForDisposable;
import static com.nutrons.framework.util.Command.parallel;
import static junit.framework.TestCase.assertTrue;

import com.nutrons.framework.util.Command;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class TestCommandReuse {

  @Test
  public void testEndConditionReuse() {
    int[] record = new int[2];
    record[0] = 0; // this is the value that the command will change
    record[1] = 0; // if 1, the command will end.
    Command change = Command.create(() -> record[0] = 1).until(() -> record[1] == 1);
    waitForDisposable(parallel(change, Command.create(() -> record[1] = 1)
        .delayStart(2, TimeUnit.SECONDS)).execute());
    assertTrue(record[0] == 1);
    // now we try it again.
    record[0] = 0;
    record[1] = 0;
    long start = System.currentTimeMillis();
    waitForDisposable(change.killAfter(1, TimeUnit.SECONDS).execute());
    // assert that command still functioned
    assertTrue(record[0] == 1);
    // assert that command only quit because 1 second passed, proving that the command is reusable.
    assertTrue(start + 1000 < System.currentTimeMillis());
  }
}