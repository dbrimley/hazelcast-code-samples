import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.json.Json;
import com.hazelcast.json.JsonValue;
import com.hazelcast.query.Predicates;

import java.util.Collection;

public class JsonDemo {

    private static String jsonJohn =
            "{ \"age\": 42, \"name\": \"John\", \"company\": { \"address\": \"someaddress\", \"name\":" + " \"hazelcast\" } }";
    private static String jsonString = "this is still a json value";

    private IMap<Integer, JsonValue> map;
    private IMap<JsonValue, Integer> inverseMap;
    private HazelcastInstance hazelcastInstance;

    public static void main(String[] args) {
        JsonDemo thisDemo = new JsonDemo();
        thisDemo.connectToCluster();
        thisDemo.getTheMaps();
        thisDemo.basicMapInteraction();
        thisDemo.querying();
        thisDemo.addIndex();
        thisDemo.querying();
    }

    private void getTheMaps() {
        map = hazelcastInstance.getMap("map");
        inverseMap = hazelcastInstance.getMap("inverseMap");
    }

    private void addIndex() {
        // indexing
        map.addIndex("age", true);
    }

    private void querying() {
        // querying
        Collection<JsonValue> results = map.values(Predicates.equal("company.name", "hazelcast"));
        Collection<JsonValue> results2 = map.values(Predicates.equal("this", "this is still a json value"));
        Collection<Integer> results3 = inverseMap.values(Predicates.greaterThan("__key.age", 20));
    }

    private void basicMapInteraction() {
        // basic map interaction
        map.put(1, Json.asJson(jsonJohn));
        // value will cause the j.asObject().set() call below to fail as it returns a different JsonObject
        map.put(1, Json.value(jsonJohn));
        map.put(2, Json.asJson(jsonString));

        JsonValue j = map.get(1);
        j.asObject().set("age", 5);
        map.put(1, j);
        inverseMap.put(Json.asJson(jsonJohn), 2);
    }

    private void connectToCluster() {
        ClientConfig clientConfig = new ClientConfig();
        hazelcastInstance = HazelcastClient.newHazelcastClient(clientConfig);
    }

}
