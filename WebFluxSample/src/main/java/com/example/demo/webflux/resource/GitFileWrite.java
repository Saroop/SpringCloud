package com.example.demo.webflux.resource;

public class GitFileWrite {
	
	private String message;
	private String content;
	private String sha;
	private Committer committer;
	
	public GitFileWrite() {
		super();
	}
	
	public GitFileWrite(String message, String content, String sha, Committer committer) {
		super();
		this.message = message;
		this.content = content;
		this.sha = sha;
		this.committer = committer;
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
	public Committer getCommitter() {
		return committer;
	}
	public void setCommitter(Committer committer) {
		this.committer = committer;
	}
	
	
}
