package io.vamshedhar.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Assignment: HomeWork 1
 * File Name: MainActivity.java
 *
 * @author Vamshedhar Reddy Chintala
 * @author Anjani Nalla
 */

public class MainActivity extends AppCompatActivity {

    TextView resultTextView;
    String operation = "";
    String savedResult;
    boolean operationClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = (TextView) findViewById(R.id.resultTextView);

        savedResult = resultTextView.getText().toString();
    }

    private static String round(String value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().toString();

    }

    public void swapSign(View view){
        String resultText = resultTextView.getText().toString();

        if(resultText.equals("Err!")){
            resultText = "0";
        }

        BigDecimal bd = new BigDecimal(resultText);

        BigDecimal newResult = bd.multiply(new BigDecimal(-1));

        resultTextView.setText(newResult.toString());


    }

    public void setResult(String result){
        if(result.contains("E")){
            resultTextView.setText("Err!");
        } else{
            if(result.contains(".")){
                int index = result.indexOf(".");

                if (result.length() < 16){
                    resultTextView.setText(result);
                } else if (index > 14){
                    resultTextView.setText("Err!");
                } else{
                    resultTextView.setText(round(result, 14 - index));
                }
            } else{
                if(result.length() > 14){
                    resultTextView.setText("Err!");
                } else {
                    resultTextView.setText(result);
                }
            }
        }

    }

    public String calculateResult(String firstNumber, String secondNumber, String operation){

        BigDecimal firstValue = new BigDecimal(firstNumber);
        BigDecimal secondValue = new BigDecimal(secondNumber);

        BigDecimal finalValue;

        switch(operation){
            case "+":
                finalValue = firstValue.add(secondValue);
                break;
            case "-":
                finalValue = firstValue.subtract(secondValue);
                break;
            case "/":
                if(secondValue.equals(BigDecimal.ZERO)){
                    return "Err!";
                } else {
                    finalValue = firstValue.divide(secondValue, 14, RoundingMode.HALF_UP);
                }
                break;
            case "x":
                finalValue = firstValue.multiply(secondValue);
                break;
            default:
                finalValue = secondValue;
                break;
        }

        if(finalValue.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0){

            return Long.toString(finalValue.longValue());
        } else{
            return finalValue.toString();
        }
    }

    public void onEqualsClick(View view){

        String resultText = resultTextView.getText().toString();

        if(resultText.equals("Err!")){
            resultText = "0";
        }

        String result = calculateResult(savedResult, resultText, operation);

        operation = "";
        operationClicked = true;

        setResult(result);

        resultText = resultTextView.getText().toString();

        if(resultText.equals("Err!")){
            resultText = "0";
        }

        savedResult = resultText;
    }

    public void onOperationClick(View view){
        Button operationButton = (Button) view;

        if(resultTextView.getText().toString().equals("Err!")){
            setResult("0");
        }

        if (!operation.equals("") && !operationClicked){
            onEqualsClick(view);
        }

        operationClicked = true;

        operation = operationButton.getText().toString();
        savedResult = resultTextView.getText().toString();
    }

    public void onACClick(View view){
        setResult("0");
        operation = "";
        savedResult = "0";
    }

    public void onDotClick(View view){

        if (operationClicked){
            String newResult =  "0.";
            resultTextView.setText(newResult);
            operationClicked = false;
        } else{
            String oldResult = resultTextView.getText().toString();

            if(oldResult.equals("Err!")){
                oldResult = "0";
            }

            if(!oldResult.contains(".")){
                String newResult = oldResult + ".";
                resultTextView.setText(newResult);
            }
        }

    }

    public void onNumberClick(View view){
        Button numberButton = (Button) view;

        String resultText = resultTextView.getText().toString();

        if(resultText.equals("Err!")){
            resultText = "0";
        }

        if((resultText.contains(".") && resultText.length() < 15) || resultText.length() < 14 || operationClicked){
            String newResult;

            if (operationClicked){
                savedResult = resultTextView.getText().toString();
                newResult = numberButton.getText().toString();
                operationClicked = false;
            } else {
                newResult = resultTextView.getText().toString() + numberButton.getText().toString();
            }

            if(!newResult.contains(".")){
                Long resultValue = Long.parseLong(newResult);
                setResult(resultValue.toString());
            } else{
                setResult(newResult);
            }
        }

    }
}
