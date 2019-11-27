/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamongodb;

import controllers.CustomerDao;
import events.ConsumerCreator;
import events.IKafkaConstants;
import events.ProducerCreator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import model.Address;
import model.Customer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 *
 * @author nmpellias
 */
public class JavaMongoDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // CREATING
//        String c = "Y";
//        while(c.equalsIgnoreCase("Y")){
//            Customer customer = new Customer();
//            Address address = new Address();
//            Scanner scn = new Scanner(System.in);
//            
//            System.out.print("Would you like to enter a new Customer? (Y/N)");
//            c = scn.nextLine();
//            
//            if(!c.equals("Y"))
//                break;
//            
//            System.out.print("Enter Customer ID: ");
//            customer.setId(Integer.parseInt(scn.nextLine()));
//            System.out.print("Enter Customer First Name: ");
//            customer.setFirstName(scn.nextLine());
//            System.out.print("Enter Customer Last Name: ");
//            customer.setLastName(scn.nextLine());
//            
//            System.out.print("Enter Customer's Address, Road Name: ");
//            address.setRoadName(scn.nextLine());
//            System.out.print("Enter Customer's Address, Number: ");
//            address.setNumber(Integer.parseInt(scn.nextLine()));
//            System.out.print("Enter Customer's Address, Area: ");
//            address.setArea(scn.nextLine());
//            System.out.print("Enter Customer's Address, Zip: ");
//            address.setZip(Integer.parseInt(scn.nextLine()));
//            System.out.print("Enter Customer's Address, City: ");
//            address.setCity(scn.nextLine());
//            System.out.print("Enter Customer's Address, Country: ");
//            address.setCountry(scn.nextLine());
//            
//            customer.setAddress(address);
//            
//            System.out.println("Very Well. I am storing your Customer and notify the Event Hub...");
//            new CustomerDao().save(customer);
//            
//        }
//        
//        // READING ALL
//        List<Customer> customers = new CustomerDao().getAll();
//        for (Customer customer : customers) {
//            System.out.println(customer.toString());
//        }
        
//        // READING ONE BY CUST_ID
//        Optional<Customer> cust = new CustomerDao().get(3L);
//        cust.ifPresent(customer -> {
//            System.out.println( "CUSTOMER FOUND : " + customer); 
//        });
//        
//        // DELETING
//        if(cust.isPresent()){
//            new CustomerDao().delete(cust.get());
//        }
//        // and check again
//        List<Customer> custrs = new CustomerDao().getAll();
//        for (Customer customer : custrs) {
//            System.out.println(customer.toString());
//        }
        
        // UPDATING
        // Fetch first the customer that we want to update
        Optional<Customer> custmr = new CustomerDao().get(4L);
        if(custmr.isPresent()){
            String[] params = new String[]{null,"Bellias",null,null,null,"11635",null,null};
            new CustomerDao().update(custmr.get(), params);
        }
        // and check again
        List<Customer> cusrs = new CustomerDao().getAll();
        for (Customer customer : cusrs) {
            System.out.println(customer.toString());
        }
            
    }

    static void runConsumer() {
        Consumer<Long, String> consumer = ConsumerCreator.createConsumer();
        int noMessageFound = 0;
        while (true) {
            ConsumerRecords<Long, String> consumerRecords = consumer.poll(1000);
            // 1000 is the time in milliseconds consumer will wait if no record is found at broker.
            if (consumerRecords.count() == 0) {
                noMessageFound++;
                if (noMessageFound > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT) // If no message found count is reached to threshold exit loop.  
                {
                    break;
                } else {
                    continue;
                }
            }
            //print each record. 
            consumerRecords.forEach(record -> {
                System.out.println("Record Key " + record.key());
                System.out.println("Record value " + record.value());
                System.out.println("Record partition " + record.partition());
                System.out.println("Record offset " + record.offset());
            });
            // commits the offset of record to broker. 
            consumer.commitAsync();
        }
        consumer.close();
    }


}














