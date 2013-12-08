package com.androidiansoft.gaming.yahtzee.fragments;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.androidiansoft.gaming.yahtzee.activities.Provider;
import com.androidiansoft.gaming.yahtzee.activities.R;
import com.androidiansoft.gaming.yahtzee.activities.TurnData;
import com.androidiansoft.gaming.yahtzee.data.DBHelper;

public class Gameboard extends Fragment {

	public static final String ARG_SECTION_NUMBER = "section_number";
	static EditText die1;
	static EditText die2;
	static EditText die3;
	static EditText die4;
	static EditText die5;
	Random generator = new Random();
	public static int counter = 0;
	public static boolean rollComplete = false;
	ToggleButton button1;
	ToggleButton button2;
	ToggleButton button3;
	ToggleButton button4;
	ToggleButton button5;
	static TextView totalScore;
	static TextView oppTotalScore;
	TextView totalScoreText;
	public static int numbers[] = new int[5];
	public static String DICE1 = "dice1";
	public static String DICE2 = "dice2";
	public static String DICE3 = "dice3";
	public static String DICE4 = "dice4";
	public static String DICE5 = "dice5";
	static Button rollButton;
	Toast mToast;

	public Gameboard() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View V = inflater.inflate(R.layout.gameboard, container, false);
		die1 = (EditText) V.findViewById(R.id.die1);
		die2 = (EditText) V.findViewById(R.id.die2);
		die3 = (EditText) V.findViewById(R.id.die3);
		die4 = (EditText) V.findViewById(R.id.die4);
		die5 = (EditText) V.findViewById(R.id.die5);
		button1 = (ToggleButton) V.findViewById(R.id.button1);
		button2 = (ToggleButton) V.findViewById(R.id.button2);
		button3 = (ToggleButton) V.findViewById(R.id.button3);
		button4 = (ToggleButton) V.findViewById(R.id.button4);
		button5 = (ToggleButton) V.findViewById(R.id.button5);
		totalScore = (TextView) V.findViewById(R.id.totalScore);
		oppTotalScore = (TextView) V.findViewById(R.id.oppScoreTotal);
		totalScoreText = (TextView) V.findViewById(R.id.totScoreText);
		rollButton = (Button) V.findViewById(R.id.rollButton);
		rollButton.setText(R.string.roll1);
		die1.setKeyListener(null);
		die2.setKeyListener(null);
		die3.setKeyListener(null);
		die4.setKeyListener(null);
		die5.setKeyListener(null);
		die1.setCursorVisible(false);
		die2.setCursorVisible(false);
		die3.setCursorVisible(false);
		die4.setCursorVisible(false);
		die5.setCursorVisible(false);
		button1.setClickable(false);
		button2.setClickable(false);
		button3.setClickable(false);
		button4.setClickable(false);
		button5.setClickable(false);
		die1.setText("");
		die2.setText("");
		die3.setText("");
		die4.setText("");
		die5.setText("");

		// Set listner for roll button
		rollButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				try {
					rollAction();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
		});
		return V;
	}

	// SelectionSort
	public int[] selectionSort() {
		int min;
		int tmp;

		for (int i = 0; i < numbers.length - 1; i++) {
			min = i;
			for (int pos = i + 1; pos < numbers.length; pos++)
				if (numbers[pos] < numbers[min])
					min = pos;

			tmp = numbers[min];
			numbers[min] = numbers[i];
			numbers[i] = tmp;
		}

		return numbers;

	}

	// Roll dice
	private void rollAction() throws InterruptedException, ExecutionException,
			TimeoutException {
		int dice1 = 0;
		int dice2 = 0;
		int dice3 = 0;
		int dice4 = 0;
		int dice5 = 0;
		// Roll 1
		if (counter == 0) {
			rollAll();
			button1.setClickable(true);
			button2.setClickable(true);
			button3.setClickable(true);
			button4.setClickable(true);
			button5.setClickable(true);
			dice1 = Integer.parseInt(die1.getText().toString());
			dice2 = Integer.parseInt(die2.getText().toString());
			dice3 = Integer.parseInt(die3.getText().toString());
			dice4 = Integer.parseInt(die4.getText().toString());
			dice5 = Integer.parseInt(die5.getText().toString());
			counter++;
			rollButton.setText(R.string.roll2);
			
			ContentValues rollTable = new ContentValues();
			
			rollTable.put(DBHelper.COLUMN_TURN_ID, Scoreboard.turnId);
			rollTable.put(DBHelper.COLUMN_ROLL_NUMBER, 1);
			rollTable.put(DBHelper.COLUMN_DIE1, dice1);
			rollTable.put(DBHelper.COLUMN_DIE2, dice2);
			rollTable.put(DBHelper.COLUMN_DIE3, dice3);
			rollTable.put(DBHelper.COLUMN_DIE4, dice4);
			rollTable.put(DBHelper.COLUMN_DIE5, dice5);
			
			getActivity().getContentResolver().insert(Provider.ROLL_URI, rollTable);
			
			//TurnData.postDataFromRoll(Scoreboard.gameId, dice1, dice2, dice3,
			//		dice4, dice5, 1);
			// Roll 2
		} else if (counter == 1) {
			diceSelectedRoll();
			dice1 = Integer.parseInt(die1.getText().toString());
			dice2 = Integer.parseInt(die2.getText().toString());
			dice3 = Integer.parseInt(die3.getText().toString());
			dice4 = Integer.parseInt(die4.getText().toString());
			dice5 = Integer.parseInt(die5.getText().toString());
			counter++;
			rollButton.setText(R.string.roll3);
			
			ContentValues rollTable = new ContentValues();
			
			rollTable.put(DBHelper.COLUMN_TURN_ID, Scoreboard.turnId);
			rollTable.put(DBHelper.COLUMN_ROLL_NUMBER, 2);
			rollTable.put(DBHelper.COLUMN_DIE1, dice1);
			rollTable.put(DBHelper.COLUMN_DIE2, dice2);
			rollTable.put(DBHelper.COLUMN_DIE3, dice3);
			rollTable.put(DBHelper.COLUMN_DIE4, dice4);
			rollTable.put(DBHelper.COLUMN_DIE5, dice5);
			
			getActivity().getContentResolver().insert(Provider.ROLL_URI, rollTable);
			
			//TurnData.postDataFromRoll(Scoreboard.gameId, dice1, dice2, dice3,
			//		dice4, dice5, 2);
			// Roll 3
		} else if (counter == 2) {
			diceSelectedRoll();
			button1.setChecked(false);
			button2.setChecked(false);
			button3.setChecked(false);
			button4.setChecked(false);
			button5.setChecked(false);
			button1.setClickable(false);
			button2.setClickable(false);
			button3.setClickable(false);
			button4.setClickable(false);
			button5.setClickable(false);
			rollButton.setClickable(false);
			rollButton.setText(R.string.choose_score);
			moveDiceToArray();
			numbers = selectionSort();
			rollComplete = true;
			counter++;
			dice1 = Integer.parseInt(die1.getText().toString());
			dice2 = Integer.parseInt(die2.getText().toString());
			dice3 = Integer.parseInt(die3.getText().toString());
			dice4 = Integer.parseInt(die4.getText().toString());
			dice5 = Integer.parseInt(die5.getText().toString());
			
			ContentValues rollTable = new ContentValues();
			
			rollTable.put(DBHelper.COLUMN_TURN_ID, Scoreboard.turnId);
			rollTable.put(DBHelper.COLUMN_ROLL_NUMBER, 3);
			rollTable.put(DBHelper.COLUMN_DIE1, dice1);
			rollTable.put(DBHelper.COLUMN_DIE2, dice2);
			rollTable.put(DBHelper.COLUMN_DIE3, dice3);
			rollTable.put(DBHelper.COLUMN_DIE4, dice4);
			rollTable.put(DBHelper.COLUMN_DIE5, dice5);
			
			getActivity().getContentResolver().insert(Provider.ROLL_URI, rollTable);
			
			//TurnData.postDataFromRoll(Scoreboard.gameId, dice1, dice2, dice3,
			//		dice4, dice5, 3);
		}

	}

	// Roll only the dice that are not toggled
	private void diceSelectedRoll() {
		if (!button1.isChecked()) {
			roll1();
		}
		if (!button2.isChecked()) {
			roll2();
		}
		if (!button3.isChecked()) {
			roll3();
		}
		if (!button4.isChecked()) {
			roll4();
		}
		if (!button5.isChecked()) {
			roll5();
		}
	}

	// Roll all dice
	private void rollAll() {
		die1.setText(Integer.toString(generator.nextInt(6) + 1));
		die2.setText(Integer.toString(generator.nextInt(6) + 1));
		die3.setText(Integer.toString(generator.nextInt(6) + 1));
		die4.setText(Integer.toString(generator.nextInt(6) + 1));
		die5.setText(Integer.toString(generator.nextInt(6) + 1));
	}

	// Roll die 5
	public void roll5() {

		die5.setText(Integer.toString(generator.nextInt(6) + 1));

	}

	// Rol die 4
	public void roll4() {

		die4.setText(Integer.toString(generator.nextInt(6) + 1));

	}

	// Roll die 3
	public void roll3() {

		die3.setText(Integer.toString(generator.nextInt(6) + 1));

	}

	// Roll die 2
	public void roll2() {

		die2.setText(Integer.toString(generator.nextInt(6) + 1));

	}

	// Roll die 1
	public void roll1() {

		die1.setText(Integer.toString(generator.nextInt(6) + 1));

	}

	// Move dice number to an array
	public void moveDiceToArray() {
		numbers[0] = Integer.parseInt(die1.getText().toString());
		numbers[1] = Integer.parseInt(die2.getText().toString());
		numbers[2] = Integer.parseInt(die3.getText().toString());
		numbers[3] = Integer.parseInt(die4.getText().toString());
		numbers[4] = Integer.parseInt(die5.getText().toString());

	}

	// Reset roll factors
	public static void resetRoll() throws InterruptedException,
			ExecutionException, TimeoutException {
		totalScore.setText(Scoreboard.total + "");
		oppTotalScore.setText(Scoreboard.oppTotal + "");
		rollButton.setText(R.string.rolldice);
		// rollButton.setClickable(true);
		rollComplete = false;
		counter = 0;
		die1.setText("");
		die2.setText("");
		die3.setText("");
		die4.setText("");
		die5.setText("");
	}
}
