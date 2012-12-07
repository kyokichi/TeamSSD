/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SeniorProject;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tiffany
 */
public class ScheduleTimer {

      public ScheduleTimer(long initialDelay, long delaySecond, long stopAfter)
      {
        fInitialDelay = initialDelay;
        fDelaySecond = delaySecond;
        fShutdownAfter = stopAfter;
        fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);
      }

      void activateTimerThenStop(ScheduleJob sj, String testName) throws ClassNotFoundException,
      SQLException
      {
        String x = sj.addTestInfoRecord(testName);
        Runnable soundAlarmTask = new ScheduleTask(sj, x);
        ScheduledFuture<?> soundAlarmFuture =
          fScheduler.scheduleWithFixedDelay(soundAlarmTask, fInitialDelay,
                                            fDelaySecond, TimeUnit.SECONDS);
        Runnable stopAlarm = new StopTask(soundAlarmFuture);
        fScheduler.schedule(stopAlarm, fShutdownAfter, TimeUnit.SECONDS);

      }

      // PRIVATE
      private final ScheduledExecutorService fScheduler;
      private final long fInitialDelay;
      private final long fDelaySecond;
      private final long fShutdownAfter;

      private static final int NUM_THREADS = 1;
      private static final boolean DONT_INTERRUPT_IF_RUNNING = false;

      private static final class ScheduleTask implements Runnable
      {
        ScheduleJob sj;
        String x;
        public ScheduleTask (ScheduleJob sj, String x)
        {
            this.sj = sj;
            this.x = x;
        }
        public void run()
        {
            try
            {
                sj.addRecords("1", x);
            }
            catch(Exception e)
            {
                e.getMessage();
            }
        }
      }

      private final class StopTask implements Runnable
      {
        StopTask(ScheduledFuture<?> aSchedFuture)
        {
          fSchedFuture = aSchedFuture;
        }
        public void run()
        {
          System.out.println("Stop recording.");
          fSchedFuture.cancel(DONT_INTERRUPT_IF_RUNNING);

          /*scheduler shutdown*/
          fScheduler.shutdown();
        }
        private ScheduledFuture<?> fSchedFuture;
      }
      
      public static void method(int year, int month, int day, int hour, int min, int endSecs, String testName) throws InterruptedException,
      ClassNotFoundException, SQLException
      {
          System.out.println("Main started.");
          ScheduleJob sj = new ScheduleJob();
          long x = sj.futureTime(year, month, day, hour, min);
          ScheduleTimer job = new ScheduleTimer(x, 1, x + endSecs);
          job.activateTimerThenStop(sj, testName);

          System.out.println("Main ended.");
      }

      public static void main(String [] args) throws InterruptedException,
      ClassNotFoundException, SQLException
      {
          /*System.out.println("Main started.");
          ScheduleJob sj = new ScheduleJob();
          long x = sj.futureTime(2012, 10, 24, 13, 30);
          ScheduleTimer job = new ScheduleTimer(x, 1, x + 10);
          job.activateTimerThenStop(sj, "testNameGoesHere");

          System.out.println("Main ended.");*/
          //method(2012, 11, 06, 18, 29, 10, "test_name_goes_here2");
          passParam("2012-12-06", "10", "lalala", "18:34");

      }

      public static void passParam(String date, String sec, String test_name, String time) throws ClassNotFoundException, SQLException
      {
          int year, month, day, hour, min;

          String [] dates = date.split("-");
          year = Integer.parseInt(dates[0]);
          month = Integer.parseInt(dates[1]) - 1;
          day = Integer.parseInt(dates[2]);

          String [] times = time.split(":");
          hour = Integer.parseInt(times[0]);
          min= Integer.parseInt(times[1]);

          int s = Integer.parseInt(sec);

          System.out.println("Main started.");
          ScheduleJob sj = new ScheduleJob();
          long x = sj.futureTime(year, month, day, hour, min);
          ScheduleTimer job = new ScheduleTimer(x, 1, x + s);
          job.activateTimerThenStop(sj, test_name);

          System.out.println("Main ended.");
      }
}
