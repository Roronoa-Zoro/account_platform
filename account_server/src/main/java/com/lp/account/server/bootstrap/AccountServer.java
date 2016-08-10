package com.lp.account.server.bootstrap;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AccountServer {

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("spring-jdbc.xml");
		ac.start();
		System.err.println("start to serving, press any key to exit...");
		System.in.read();
		ac.close();
	}

}
