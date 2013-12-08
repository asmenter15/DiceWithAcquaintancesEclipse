package com.androidiansoft.gaming.yahtzee.data;

public class Game {

	String name;
	int valid;
	int id;
	int connectedUser;
	int createdUser;
	int singlePlayer;
	int turn;
	int crePts;
	int conPts;
	int creBonus;
	int conBonus;
	String oppFirstName;
	String oppLastName;
	String oppUsername;

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverKey) {
		this.serverId = serverKey;
	}

	String serverId;

	public String getOppFirstName() {
		return oppFirstName;
	}

	public void setOppFirstName(String oppFirstName) {
		this.oppFirstName = oppFirstName;
	}

	public String getOppLastName() {
		return oppLastName;
	}

	public void setOppLastName(String oppLastName) {
		this.oppLastName = oppLastName;
	}

	public String getOppUsername() {
		return oppUsername;
	}

	public void setOppUsername(String oppUsername) {
		this.oppUsername = oppUsername;
	}

	public int getCreBonus() {
		return creBonus;
	}

	public void setCreBonus(int creBonus) {
		this.creBonus = creBonus;
	}

	public int getConBonus() {
		return conBonus;
	}

	public void setConBonus(int conBonus) {
		this.conBonus = conBonus;
	}

	public int getCrePts() {
		return crePts;
	}

	public void setCrePts(int crePts) {
		this.crePts = crePts;
	}

	public int getConPts() {
		return conPts;
	}

	public void setConPts(int conPts) {
		this.conPts = conPts;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public int getSinglePlayer() {
		return singlePlayer;
	}

	public void setSinglePlayer(int singlePlayer) {
		this.singlePlayer = singlePlayer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValid() {
		return valid;
	}

	public void setValid(int valid) {
		this.valid = valid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getConnectedUser() {
		return connectedUser;
	}

	public void setConnectedUser(int connectedUser) {
		this.connectedUser = connectedUser;
	}

	public int getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(int i) {
		this.createdUser = i;
	}
}
