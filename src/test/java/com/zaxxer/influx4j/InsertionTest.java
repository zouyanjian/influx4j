/*
 * Copyright (c) 2017, Brett Wooldridge.  All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaxxer.influx4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.zaxxer.influx4j.util.DaemonThreadFactory;


public class InsertionTest {
   private PointFactory pointFactory;
   private InfluxDB influxDB;

   @Before
   public void createFactory() throws Exception {
      pointFactory = PointFactory.builder()
            .setThreadFactory(new DaemonThreadFactory("Point"))
            .build();

      influxDB = InfluxDB.builder()
         .setConnection("127.0.0.1", 9086, InfluxDB.Protocol.HTTP)
         .setUsername("influx4j")
         .setPassword("influx4j")
         .setDatabase("influx4j")
         .setThreadFactory(new DaemonThreadFactory("Flusher"))
         .build();

      TimeUnit.SECONDS.sleep(1);
   }

   @After
   public void shutdownFactory() throws Exception {
      System.out.println(System.currentTimeMillis() + " shutting down...");
      pointFactory.close();

      TimeUnit.SECONDS.sleep(1);

      influxDB.close();

      System.out.println(System.currentTimeMillis() + " shutdown.");
   }

   @Test
   public void testSingleInsert() throws Exception {

      System.out.println(System.currentTimeMillis() + " starting testSingleInsert()...");

      final Point point = pointFactory.createPoint("testMeasurement")
         .tag("tag", "apple")
         .field("boolean", true);

      influxDB.write(point);

      TimeUnit.SECONDS.sleep(1);

      System.out.println(System.currentTimeMillis() + " completed testSingleInsert().");

      // TODO query and verify
   }

   // @Test
   public void testMultipleInserts() throws Exception {

      for (int i = 0; i < 1000; i++) {
         final Point point = pointFactory.createPoint("testMeasurement")
            .tag("tag", "banana")
            .field("count", i);

         influxDB.write(point);
      }

      TimeUnit.SECONDS.sleep(1);

      // TODO query and verify
   }
}
