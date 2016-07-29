// Copyright 2016 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
////////////////////////////////////////////////////////////////////////////////
package com.google.pubsub.kafka.sink;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.pubsub.kafka.common.ConnectorUtils;
import com.google.pubsub.kafka.source.CloudPubSubSourceConnector;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.errors.ConnectException;
import org.junit.Before;
import org.junit.Test;

/** Tests for {@link CloudPubSubSinkConnector}. */
public class CloudPubSubSinkConnectorTest {

  private static final int NUM_TASKS = 10;
  private static final String CPS_PROJECT = "hello";
  private static final String CPS_TOPIC = "world";
  private static final String INVALID_CPS_MIN_BATCH_SIZE = "Not an int";

  private CloudPubSubSinkConnector connector;
  private Map<String, String> props;

  @Before
  public void setup() {
    connector = new CloudPubSubSinkConnector();
    props = new HashMap<>();
    props.put(ConnectorUtils.CPS_PROJECT_CONFIG, CPS_PROJECT);
    props.put(ConnectorUtils.CPS_TOPIC_CONFIG, CPS_TOPIC);
  }

  @Test(expected = ConfigException.class)
  public void testStartWhenRequiredConfigMissing() {
    connector.start(new HashMap<>());
  }

  @Test(expected = ConfigException.class)
  public void testStartWhenConfigHasInvalidMinBatchSize() {
    props.put(CloudPubSubSinkConnector.CPS_MIN_BATCH_SIZE_CONFIG, INVALID_CPS_MIN_BATCH_SIZE);
    connector.start(props);
  }

  @Test
  public void testTaskConfigs() {
    connector.start(props);
    List<Map<String, String>> taskConfigs = connector.taskConfigs(NUM_TASKS);
    assertEquals(taskConfigs.size(), NUM_TASKS);
    for (int i = 0; i < taskConfigs.size(); ++i) {
      assertEquals(taskConfigs.get(i), props);
    }
  }

  @Test
  public void testTaskClass() {
    assertEquals(CloudPubSubSinkTask.class, connector.taskClass());
  }
}
