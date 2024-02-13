package com.example.tipcalculator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;


public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {
    //declared variables for the widgets
    private EditText editTextBillAmount;
    private TextView textViewBillAmount;
    private TextView textViewPercent;
    private TextView tipTextView;
    private SeekBar wowseekBar;
    //declared the variables for the calculations
    private double billAmount = 0;
    private double percent = .15;
    private double tip = 0;
    private double total = 0;

    //setting the number formats to be used for the currency amounts , and percent amounts
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

//MP4
    //set the spinners name and the name of the number chosen associated with it
    Spinner spinner;
    private int spinnerNum = 0;
    //for the per person amount that must be paid
    private TextView textViewTotalPerPerson;
    private double amountPerPerson;
    //variables for the radio button choices
    private int clickityClick;

    //messaged display things for when user clicks share
    private String messageType = "Total Per Person: " + amountPerPerson + "Amount of People: " +
            spinnerNum + ", Bill: " + billAmount + ", Tip: " + tip;



    @Override
    //the onCreate method is what is called when the activity is starting
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //the content view is set on the activity_main xml file where all the locations could be found
    //for the listeners below
        setContentView(R.layout.activity_main);
    //added these listeners to their significant widgets
        editTextBillAmount = (EditText)findViewById(R.id.editText_BillAmount);
        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
        textViewPercent = (TextView)findViewById(R.id.textViewPercent);
        tipTextView = (TextView)findViewById(R.id.tipTextView);
        wowseekBar = (SeekBar)findViewById(R.id.seekBar);
    //this will start the seekbar on the app in the middle where the value is 15 percent
    //just as it was shown on the google doc
        wowseekBar.setProgress(15);
    //this sets the max for the seekbar to be 30%. change amount if you want it to be higher
        wowseekBar.setMax(30);


 /*Each View that  will be retrieved using findViewById needs to map to a View with the matching id
below is the views being mapped*/
        editTextBillAmount.addTextChangedListener((TextWatcher) this);
        textViewBillAmount = (TextView)findViewById(R.id.textView_BillAmount);
    //allows the seekbar to be used by the user and implements values as well for the tip %
    //SUPER IMPORTANTE. if this isnt here the seekbar value wouldn't change with the userinput
        //userinput meaning when they slide the bar the percent value changes
        wowseekBar.setOnSeekBarChangeListener(this);

//MP4 - making the view for the amount each person will pay
    textViewTotalPerPerson = (TextView)findViewById(R.id.AmountPerPerson);

//MP4 making the spinner get the number inputs
     spinner = (Spinner)findViewById(R.id.NumPeople);
     //uses the class implementation which reads users interactions with the spinner in the app
     spinner.setOnItemSelectedListener(this);
        //creating an array adapter using the default spinner resource from android
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.num_People, android.R.layout.simple_spinner_item);
        //specifying the layout when the choice appears to be a downward action
        adapter.setDropDownViewResource((android.R.layout.simple_spinner_dropdown_item));
        //applying the adapter we created above to the spinnner
        spinner.setAdapter(adapter);

        //saving instance for the rotation
        if(savedInstanceState != null) {
            billAmount = savedInstanceState.getInt("key1",0);
            percent = savedInstanceState.getInt("key2", 0);
            tip = savedInstanceState.getInt("key3", 0);
            total = savedInstanceState.getInt("key4", 0);
        }
        editTextBillAmount.setText(Integer.toString((int) billAmount));
        textViewBillAmount.setText(Integer.toString((int) total));
        textViewPercent.setText(Integer.toString((int) percent));
        tipTextView.setText(Integer.toString((int) tip));

    }

    //this saves the number the user has clicked for each of the teams after a rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //keys for each of the counts that the user has clicked so it's saved on rotation
        outState.putDouble("key1", billAmount);
        outState.putDouble("key2", percent);
        outState.putDouble("key2", tip);
        outState.putDouble("key2", total);

    }

//MP4 - making the onselect listener work with the spinner
    public void onItemSelected(AdapterView<?> parent, View view, int itemChosen, long id){
        //calls back stating an item has been selected
        parent.getItemAtPosition(itemChosen);
        //initalizing spinnerNum to the number the user chooses. Had to put plus one so the num
        // of people primarily starts at the first number.
        spinnerNum = itemChosen + 1;
        //calls the calculate method to spilt the bill between the num of people chosen
        calculate();
    }
    public void onNothingSelected(AdapterView<?> parent){
    }

//MP4 - making the buttonclicked methods for the radio buttons
    public void onRadioButtonClicked(View view){
        //boolean used to deem if the user selected an option. will be 'true' if chosen and the calculate method will be called;
        boolean chosen = ((RadioButton) view).isChecked();
        //switch/case method used so each button has a different case
        switch(view.getId()){
            //nothing is changed in this case
            case R.id.NoButton:
                if(chosen){
                    clickityClick = 0;
                    calculate();
                    break;
                }
                //the tip is rounded up in this case
            case R.id.TipButton:
                if(chosen){
                    clickityClick = 1;
                    calculate();
                    break;
                }
                //total is rounded up in this case
            case R.id.TotalButton:
                if(chosen){
                    clickityClick = 2;
                    calculate();
                    break;
                }
        }
    }
//MP4 inflate
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


//MP4 - display toast messsage for the info
    //did length long becayse that shows the text notification for a long period of time
    public void displayToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }


//MP4 share button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_share:
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                //making string to outprint the population
                String message = "Total Per Person: " + amountPerPerson + ", Amount of People: " +
                        spinnerNum + ", Bill: " + billAmount + ", Tip: " + tip;
                sendIntent.putExtra("sms_body", message);
                startActivity(sendIntent);
                return true;

            case R.id.action_info:
                displayToast(getString(R.string.icon));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //beforeTextChanged is called to notify within the charsequence the count 'int i ' characters
    // beginning at start 'int i1' will be replaced by new text with length 'int i2' after
    // int i, int i1, and int i2 represent start, after, count
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    //onTextChanged is called to notify that the charsequence with count characters beginning at the
    //start have just replaced the old text that have taken length before
    // int i, int i1, and int i2 represent start, before, count respectively
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d("MainActivity", "inside onTextChanged method: charSequence= "+charSequence);
    //charSequence is converted to a String and parsed to a double
        billAmount = Double.parseDouble(charSequence.toString()) / 100;
        Log.d("MainActivity", "Bill Amount = "+billAmount);
    //setText on the textView
        textViewBillAmount.setText(currencyFormat.format(billAmount));
    //perform tip and total calculation and update UI by calling calculate
         calculate();
    }

    //this method is called to notify that somewhere within the charsequence the text has been changed
    @Override
    public void afterTextChanged(Editable editable) {
    }

    //this method notifies that the seekbar has been moved and the progress on it has been changed
    //so the value of the % is changed for the tip
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        percent = progress / 100; //calculate percent based on seeker value
        calculate();
    }

    //method that notfies the seekbar to start tracking the users movement and to move with the users direction
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    //this is for when the user let's go of the bar and returns the value they left it at
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    // calculate and display tip and total amounts
    private void calculate() {
        Log.d("MainActivity", "inside calculate method");
    //in the calculate i needed to add the percent so it can be displayed in the tip bar every time the seekbar is touched
    // i did this with the method below. Remember that I named my seekbar wowseekbar
       percent = wowseekBar.getProgress();
       percent = percent/100;
        //formated percent and display in percentTextView
        textViewPercent.setText(percentFormat.format(percent));
    // calculated the tip and total
        tip = billAmount * percent;
        // display tip and total formatted as currency
        //user currencyFormat instead of percentFormat to set the textViewTip
        tipTextView.setText(currencyFormat.format(tip));
    //user currencyFormat instead of percentFormat to set the textViewTip
        total = tip + billAmount;
        // display tip and total formatted as currency
        textViewBillAmount.setText(currencyFormat.format(total));
//MP4 calculating the amount per person based on what amount of people were chosen in the spinner
        amountPerPerson = total / spinnerNum;
        textViewTotalPerPerson.setText(currencyFormat.format(amountPerPerson));
//MP4 calculating the radio button options
        if(clickityClick == 1){
            tip = (int) Math.ceil(tip);
            total = tip + billAmount;
            amountPerPerson = total / spinnerNum;
            tipTextView.setText(currencyFormat.format(tip));
            textViewBillAmount.setText(currencyFormat.format(total));
            textViewTotalPerPerson.setText(currencyFormat.format(amountPerPerson));
        }
        if(clickityClick == 2) {
            total = (int) Math.ceil(total);
            amountPerPerson = total / spinnerNum;
            tipTextView.setText(currencyFormat.format(tip));
            textViewBillAmount.setText(currencyFormat.format(total));
            textViewTotalPerPerson.setText(currencyFormat.format(amountPerPerson));
        }





    }

}
