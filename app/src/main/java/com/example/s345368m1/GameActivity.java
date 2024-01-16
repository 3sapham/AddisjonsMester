package com.example.s345368m1;

import static android.app.PendingIntent.getActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements MyDialog.MittInterface {
    TextView equation;
    EditText user_input;
    Button backspace_btn, ok_btn, exit_btn;
    String[] equation_array, answer_array;
    ProgressBar progress_bar;
    private int equation_num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String pref_quantity = sharedPref.getString("quantityKey", "5");
        int equation_quantity = Integer.parseInt(pref_quantity);

        for (int i = 0; i < 10; i++) {
            final int number = i;
            String btn_name = "btn" + i;

            int res_id = getResources().getIdentifier(btn_name, "id", getPackageName());
            Button num_btn = findViewById(res_id);

            num_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user_input.append(Integer.toString(number));
                }
            });
        }

        backspace_btn = findViewById(R.id.backspace_btn);
        ok_btn = findViewById(R.id.ok_btn);
        exit_btn = findViewById(R.id.exit_btn);

        equation = findViewById(R.id.equationView);
        user_input = findViewById(R.id.answerInput);

        equation_array = getResources().getStringArray(R.array.equations);
        answer_array = getResources().getStringArray(R.array.answers);

        progress_bar = findViewById(R.id.progress_bar);

        if (savedInstanceState != null) {
            String equationText = savedInstanceState.getString("equation_text");
            equation.setText(equationText);
            equation_num = savedInstanceState.getInt("equation_num", 1);
            equation_array = savedInstanceState.getStringArray("equation_array");
            answer_array = savedInstanceState.getStringArray("answer_array");
        } else {
            mixEquations();
            showNextEquation();
        }

        backspace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input_string = user_input.getText().toString();
                int input_length = input_string.length();
                if (input_length > 0){
                    user_input.setText(input_string.substring(0, input_length-1));
                }
            }
        });

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view);
            }
        });

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int user_answer = Integer.parseInt(user_input.getText().toString());
                int correct_answer = Integer.parseInt(answer_array[equation_num]);

                if (user_answer == correct_answer) {
                    progress_bar.incrementProgressBy(100/equation_quantity);
                    user_input.setText(R.string.correct);
                    user_input.setBackgroundResource(R.color.green);
                    equation_num++;

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (equation_num != equation_quantity) {
                                user_input.setBackgroundResource(R.color.black);
                                showNextEquation();
                            } else {
                                Intent i = new Intent(GameActivity.this, FinishActivity.class);
                                startActivity(i);
                            }
                        }
                    }, 1000);
                } else {
                    user_input.setBackgroundResource(R.color.red);
                    user_input.setText(R.string.wrong);

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            user_input.setBackgroundResource(R.color.black);
                            user_input.setText("");
                        }
                    }, 1000);
                }
            }
        });
    }

    private void mixEquations() {
        Random random = new Random();
        for (int i = 0; i < equation_array.length ; i++) {
            int index = random.nextInt(i + 1);

            String temp_eq = equation_array[i];
            equation_array[i] = equation_array[index];
            equation_array[index] = temp_eq;

            String temp_ans = answer_array[i];
            answer_array[i] = answer_array[index];
            answer_array[index] = temp_ans;
        }
    }

    private void showNextEquation() {
        equation.setText(equation_array[equation_num]);
        user_input.setText("");
    }

    public void onYesClick(){
        finish();
    }

    public void onNoClick(){
        return;
    }

    public void showDialog(View v) {
        DialogFragment dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), "Tittel");
    }

    public void onBackPressed() {
        DialogFragment dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), "Tittel");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("equation_text", equation.getText().toString());
        outState.putInt("equation_num", equation_num);
        outState.putStringArray("equation_array", equation_array);
        outState.putStringArray("answer_array", answer_array);
    }
}