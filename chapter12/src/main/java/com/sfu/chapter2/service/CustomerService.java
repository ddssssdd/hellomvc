package com.sfu.chapter2.service;

import com.sfu.chapter2.helper.DatabaseHelper;
import com.sfu.chapter2.model.Customer;
import com.sfu.chapter2.util.PropsUtil;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class CustomerService {
    private static final org.slf4j.Logger LOGGER= LoggerFactory.getLogger(PropsUtil.class);


    public List<Customer> getCustomerList(String keyword){
        Connection connection = null;
        try{
            //List<Customer> customerList = new ArrayList<>();
            String sql = "select * from customer";

            return DatabaseHelper.queryEntityList(Customer.class,sql);
            /*
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                Customer customer = new Customer();
                customer.setId(rs.getLong("id"));
                customer.setName(rs.getString("name"));
                customer.setContact(rs.getString("contact"));
                customer.setTelephone(rs.getString("telephone"));
                customer.setEmail(rs.getString("email"));
                customer.setRemark(rs.getString("remark"));
                customerList.add(customer);
            }*/

            //return customerList;
        }catch (Exception ex){
            LOGGER.error("Execute sql error", ex);
        }
        finally {
            DatabaseHelper.closeConnection();
        }
        return null;
    }

    public Customer getCustomer(Long id){
        return null;
    }

    public boolean createCustomer(Map<String,Object> fieldMap){
        return DatabaseHelper.insertEntity(Customer.class, fieldMap);
    }

    public boolean updateCustomer(long id ,Map<String,Object> fieldMap){
        return DatabaseHelper.updateEntity(Customer.class, id, fieldMap);
    }

    public boolean deleteCustoemr(Long id){
        return DatabaseHelper.deleteEntity(Customer.class, id);
    }
}
