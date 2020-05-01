package com.example.demo.webflux.resource;

public class Committer {
	
	private String name;
	private String email;
	
	public Committer(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}
	
	public Committer() {
		super();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
