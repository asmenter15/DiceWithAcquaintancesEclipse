package com.androidiansoft.gaming.yahtzee.data;

public class Turn {

	int userId;
	int gameId;
	int scoreSelected;
	int pointsForScore;
	int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getScoreSelected() {
		return scoreSelected;
	}

	public void setScoreSelected(int scoreSelected) {
		this.scoreSelected = scoreSelected;
	}

	public int getPointsForScore() {
		return pointsForScore;
	}

	public void setPointsForScore(int pointsForScore) {
		this.pointsForScore = pointsForScore;
	}

}
