/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import events.IKafkaConstants;
import events.ProducerCreator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Address;
import model.Customer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.bson.Document;

/**
 *
 * @author nmpellias
 */
public class CustomerDao implements Dao<Customer> {

    @Override
    public Optional<Customer> get(long id) {
        MongoClient mongoClient = MongoClients.create();// "mongodb://46.227.62.21:27017"
        MongoDatabase database = mongoClient.getDatabase("nikolaosdb");
        MongoCollection<Document> bsonCustomers = database.getCollection("customer");

        Document custDoc = bsonCustomers.find(eq("cust_id", id)).first();

        return Optional.ofNullable(bson2Customer(custDoc));
    }

    @Override
    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList();

        MongoClient mongoClient = MongoClients.create();// "mongodb://46.227.62.21:27017"
        MongoDatabase database = mongoClient.getDatabase("nikolaosdb");
        MongoCollection<Document> bsonCustomers = database.getCollection("customer");

        try (MongoCursor<Document> cur = bsonCustomers.find().iterator()) {
            while (cur.hasNext()) {
                Document custDoc = cur.next();
                customers.add(bson2Customer(custDoc));
                runProducer("READING", custDoc.toJson());
            }
        }

        return customers;
    }

    @Override
    public void save(Customer t) {
        MongoClient mongoClient = MongoClients.create();// "mongodb://46.227.62.21:27017"
        MongoDatabase database = mongoClient.getDatabase("nikolaosdb");
        MongoCollection<Document> bsonCustomers = database.getCollection("customer");

        Document custDoc = customer2Bson(t);
        // Storing into MongoDB
        bsonCustomers.insertOne(custDoc);
        // Send the storing event into Kafka in JSON format
        runProducer("STORING", custDoc.toJson());
    }

    @Override
    public void update(Customer t, String[] params) {
        MongoClient mongoClient = MongoClients.create();// "mongodb://46.227.62.21:27017"
        MongoDatabase database = mongoClient.getDatabase("nikolaosdb");
        MongoCollection<Document> bsonCustomers = database.getCollection("customer");

        t.setFirstName((params[0] == null)?t.getFirstName():params[0]);
        t.setLastName((params[1] == null)?t.getLastName():params[1]);
        
        Address addressToUpdate = t.getAddress();
        addressToUpdate.setRoadName((params[2] == null)?t.getAddress().getRoadName():params[2]);
        addressToUpdate.setNumber((params[3] == null)?t.getAddress().getNumber():Integer.parseInt(params[3]));
        addressToUpdate.setArea((params[4] == null)?t.getAddress().getArea():params[4]);
        addressToUpdate.setZip((params[5] == null)?t.getAddress().getZip():Integer.parseInt(params[5]));
        addressToUpdate.setCity((params[6] == null)?t.getAddress().getCity():params[6]);
        addressToUpdate.setCountry((params[7] == null)?t.getAddress().getCountry():params[7]);
        
        t.setAddress(addressToUpdate);
        
        Document custDoc = customer2Bson(t);
        // Updating into database
        bsonCustomers.updateOne(eq("cust_id", t.getId()), new Document("$set", custDoc));
        // Send the updating event into Kafka in JSON format
        runProducer("UPDATING", custDoc.toJson());
    }

    @Override
    public void delete(Customer t) {
        MongoClient mongoClient = MongoClients.create();// "mongodb://46.227.62.21:27017"
        MongoDatabase database = mongoClient.getDatabase("nikolaosdb");
        MongoCollection<Document> bsonCustomers = database.getCollection("customer");

        Document custDoc = customer2Bson(t);
        // Deleting from database
        bsonCustomers.findOneAndDelete(custDoc);
        // Send the deleting event into Kafka in JSON format
        runProducer("DELETING", custDoc.toJson());
    }

    private static void runProducer(String message, String data) {
        Producer<Long, String> producer = ProducerCreator.createProducer();
        ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(IKafkaConstants.TOPIC_NAME, message + " | " + data);
        try {
            RecordMetadata metadata = producer.send(record).get();
            // System.out.println(message + " | " + data + " to partition " + metadata.partition()
            //         + " with offset " + metadata.offset());
        } catch (ExecutionException e) {
            System.out.println("Error in sending record");
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println("Error in sending record");
            System.out.println(e);
        }
    }

    private static Customer bson2Customer(Document custDoc) {
        List<Object> customerData = new ArrayList(custDoc.values());
        Customer customer = new Customer();
        // System.out.println((ObjectId)customerData.get(0));
        customer.setId((long) customerData.get(1));
        customer.setFirstName((String) customerData.get(2));
        customer.setLastName((String) customerData.get(3));

        List<Object> addressData = new ArrayList(((Document) customerData.get(4)).values());
        Address address = new Address();
        address.setRoadName((String) addressData.get(0));
        address.setNumber((int) addressData.get(1));
        address.setArea((String) addressData.get(2));
        address.setZip((int) addressData.get(3));
        address.setCity((String) addressData.get(4));
        address.setCountry((String) addressData.get(5));

        customer.setAddress(address);

        return customer;
    }

    private static Document customer2Bson(Customer t) {
        Document document = new Document("cust_id", t.getId())
                .append("cust_first_name", t.getFirstName())
                .append("cust_last_name", t.getLastName())
                .append("cust_address", new Document("road_name", t.getAddress().getRoadName())
                        .append("number", t.getAddress().getNumber())
                        .append("area", t.getAddress().getArea())
                        .append("zip", t.getAddress().getZip())
                        .append("city", t.getAddress().getCity())
                        .append("country", t.getAddress().getCountry()));
        return document;
    }
}















