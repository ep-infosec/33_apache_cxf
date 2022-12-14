/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cxf.systest.ws.rm;

import java.util.logging.Logger;

import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.common.logging.LogUtils;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.greeter_control.Greeter;
import org.apache.cxf.greeter_control.GreeterService;
import org.apache.cxf.systest.ws.util.ConnectionHelper;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.cxf.testutil.common.AbstractBusTestServerBase;
import org.apache.cxf.ws.rm.RMManager;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the gzip feature does not interfere with the ws-rm retransmission.
 * Note that the current retransmission logic isn't optimal (in some sense, wrong)
 * and stores the wire-message and retransmits this wire-message directly. This
 * approach is not practical when ws-security is enabled and each message needs to be
 * timestamped and signed. Therefore, the current retransmission logic needs to be
 * changed one day.
 *
 * Independently of this fix, this test verifies the gzip feature does not interfere
 * with retransmission.
 */
public class RetransmissionGZIPTest extends AbstractBusClientServerTestBase {
    public static final String PORT = allocatePort(Server.class);
    public static final String DECOUPLE_PORT = allocatePort(RetransmissionGZIPTest.class);

    private static final Logger LOG = LogUtils.getLogger(RetransmissionQueueTest.class);
    private Bus bus;

    public static class Server extends AbstractBusTestServerBase {

        protected void run()  {
            SpringBusFactory bf = new SpringBusFactory();
            Bus bus = bf.createBus("/org/apache/cxf/systest/ws/rm/gzip-enabled.xml");
            BusFactory.setDefaultBus(bus);
            LoggingInInterceptor in = new LoggingInInterceptor();
            bus.getInInterceptors().add(in);
            bus.getInFaultInterceptors().add(in);
            LoggingOutInterceptor out = new LoggingOutInterceptor();
            bus.getOutInterceptors().add(out);
            bus.getOutFaultInterceptors().add(out);

            GreeterImpl implementor = new GreeterImpl();
            String address = "http://localhost:" + PORT + "/SoapContext/GreeterPort";

            Endpoint ep = Endpoint.create(implementor);
            ep.publish(address);

            LOG.info("Published greeter endpoint.");
        }

        public static void main(String[] args) {
            try {
                Server s = new Server();
                s.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            } finally {
                System.out.println("done!");
            }
        }
    }

    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly",
                   launchServer(Server.class, true));
    }

    @Test
    public void testDecoupleFaultHandling() throws Exception {
        SpringBusFactory bf = new SpringBusFactory();
        bus = bf.createBus("/org/apache/cxf/systest/ws/rm/gzip-enabled.xml");
        BusFactory.setDefaultBus(bus);
        LoggingInInterceptor in = new LoggingInInterceptor();
        bus.getInInterceptors().add(in);
        bus.getInFaultInterceptors().add(in);
        LoggingOutInterceptor out = new LoggingOutInterceptor();
        bus.getOutInterceptors().add(out);
        bus.getExtension(RMManager.class).getConfiguration().setBaseRetransmissionInterval(Long.valueOf(4000));

        // an interceptor to simulate a transmission error
        MessageLossSimulator loser = new MessageLossSimulator();
        bus.getOutInterceptors().add(loser);

        bus.getOutFaultInterceptors().add(out);

        GreeterService gs = new GreeterService();
        final Greeter greeter = gs.getGreeterPort();
        updateAddressPort(greeter, PORT);
        LOG.fine("Created greeter client.");

        ConnectionHelper.setKeepAliveConnection(greeter, true);
        loser.setMode(-1);
        loser.setThrowsException(true);

        try {
            greeter.greetMeOneWay("oneway");
        } catch (Exception e) {
            fail("fault thrown after queued for retransmission");
        }
        Thread.sleep(2000);

        RMManager manager = bus.getExtension(RMManager.class);
        boolean empty = manager.getRetransmissionQueue().isEmpty();
        assertFalse("RetransmissionQueue is empty", empty);

        loser.setMode(1);

        Thread.sleep(6000);

        empty = manager.getRetransmissionQueue().isEmpty();
        assertTrue("RetransmissionQueue not cleared", empty);
    }

}
