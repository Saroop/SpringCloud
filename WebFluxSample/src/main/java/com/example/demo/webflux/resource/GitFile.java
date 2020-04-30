package com.example.demo.webflux.resource;

public class GitFile {
	
	private String message;
	private String content;
	private String sha;
	
	public GitFile() {
		super();
	}
	
	public GitFile(String message, String content, String sha) {
		super();
		this.message = message;
		this.content = content;
		this.sha = sha;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}

}
