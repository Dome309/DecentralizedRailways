package DBmanager;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;

import java.util.Date;

public class DataBaseManager {
    private String uri = "mongodb://localhost";
    private String nameDB = "RailwayDB";
    private String collectionName;
    private String trainId;
    private String trainType;
    private String attribute;
    private String status;
    private String data;
    private MqttMessage messageMqtt;
    private String message;
    private Date date;
    JSONObject jsonMessage;
    public void startDB(){
        //Construct a ServerApi instance using the ServerApi.builder() method
        ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
        MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri))
                .serverApi(serverApi).build();
        //Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            MongoDatabase database = mongoClient.getDatabase(nameDB);
            try {
                MongoCollection<Document> collection = database.getCollection(collectionName);
                Document doc = Document.parse(jsonMessage.toJSONString());
                collection.insertOne(doc);
                System.out.println("Insert completed");
            } catch (MongoException me) {
                System.err.println(me);
            }
        }
    }

    public void setCollectionName(String collectionName, JSONObject jsonMessage, Date date){
        this.collectionName = collectionName;
        this.jsonMessage = jsonMessage;
        this.date = date;
        startDB();
    }
}
