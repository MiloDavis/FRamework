package com.nutrons.framework.test;

import static com.nutrons.framework.commands.Command.parallel;
import static junit.framework.TestCase.assertTrue;

import com.nutrons.framework.commands.Command;
import io.reactivex.Flowable;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

public class MultiCommandTest {
  private Command delay;

  @Before
  public void setupCommands() {
    delay = Command.fromAction(() -> {
    }).killAfter(1000, TimeUnit.MILLISECONDS);
  }

  @Test
  public void testSerialAndParallel() {
    long start = System.currentTimeMillis();
    parallel(delay.then(delay), delay).execute(true).blockingSubscribe();
    assertTrue(System.currentTimeMillis() - start < 3000);
    assertTrue(System.currentTimeMillis() - start > 2000);
  }

  @Test
  public void testOneThenAnother() throws InterruptedException {
    int[] record = new int[1];
    Command oneThenZero = Command.just(x -> {
      record[0] = 1;
      return Flowable.just(() -> record[0] = 0);
    });
    oneThenZero.delayFinish(2000, TimeUnit.MILLISECONDS).execute(true);
    Thread.sleep(1000);
    assertTrue(record[0] == 1);
    Thread.sleep(2000);
    assertTrue(record[0] == 0);
  }
}
