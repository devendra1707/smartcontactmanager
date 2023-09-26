package com.smart.dao;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.smart.entities.MyOrder;

public interface MyOrderRepository extends JpaRepositoryImplementation<MyOrder, Long> {
public MyOrder findByOrderId(String orderId);
	
}
