/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package events;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import model.Customer;
import org.apache.kafka.common.serialization.Deserializer;

/**
 *
 * @author nmpellias
 */
public class CustomerDeserializer implements Deserializer<Customer> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public Customer deserialize(String topic, byte[] data) {
        ObjectMapper mapper = new ObjectMapper();
        Customer object = null;
//        try {
//            object = mapper.readValue(data, Customer.class);
//        } catch (Exception exception) {
//            System.out.println("Error in deserializing bytes " + exception);
//        }
        return object;
    }

    @Override
    public void close() {

    }

}





