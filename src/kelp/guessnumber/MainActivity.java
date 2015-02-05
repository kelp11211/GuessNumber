package kelp.guessnumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView board, description;
	private EditText  input;
	private Button guessBtn;
	private GuessManager gManager;
	private InputMethodManager imm;
	private int number_size, times;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set action bar color
        getActionBar().setBackgroundDrawable(getResources().getDrawable((R.drawable.title)));
        // get previous data
        getSharedPreferencesData();
        // initialize the views that will be used
        initialViews();
        // get the InputMethodManager instance
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // generate a manager of guess number game
        gManager = new GuessManager(number_size, times);
    }
    // get previous data saved by key-value pairs
    private void getSharedPreferencesData() {
    	SharedPreferences sp = this.getSharedPreferences("GuessNumber", Context.MODE_PRIVATE);
    	number_size = sp.getInt("NumberSize", 4);
    	times = sp.getInt("Times", 10);
    }
    // update key-value pairs with new data
    private void updateSharedPreferencesData() {
		SharedPreferences.Editor  editor = this.getSharedPreferences("GuessNumber", Context.MODE_PRIVATE).edit();
		editor.putInt("NumberSize", number_size);
		editor.putInt("Times", times);
		editor.commit();
	}
    // initialize views that will be used 
    private void initialViews() {
    	// board, which shows the guessing process and result  
    	board = (TextView) this.findViewById(R.id.main_textview_board);
    	board.setMovementMethod(new ScrollingMovementMethod());
    	// description, which is used to tell players the remainder 
    	description = (TextView) this.findViewById(R.id.main_textview_description);
    	description.setText("猜"+number_size+"個0~9不重複數字\t限制猜"+times+"次");
    	// input field, let player input the number
    	input = (EditText ) this.findViewById(R.id.main_edittext_number);
    	// button used to submit the inputed number 
    	guessBtn = (Button) this.findViewById(R.id.main_button_guess);
    	guessBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//  get the numerical string from input field
				String guess = input.getText().toString();
				// clean the number in input field 
				input.setText("");
				// remove focus 
				input.clearFocus();
				 // let keyboard be hidden
		        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		        
				//  check whether  the number is valid or not 
				if (!gManager.verify(guess))
					Toast.makeText(getBaseContext(), gManager.getVerifyResponse(), Toast.LENGTH_SHORT).show();
				else {
					String result = gManager.guessNumber(guess);
					StringBuilder builder = new StringBuilder();
					builder.append("輸入 " + guess+"\t");
					builder.append("結果 " + result);
					board.append(builder.toString()+"\n");
					
					description.setText("猜"+number_size+"個0~9不重複數字\t剩餘"+gManager.getRemanderTimes()+"次");
					// start a new game after five seconds as success or run out the number of times 
					if (gManager.isMatched()) {
						board.append("5秒後重開新局");
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								startNewGame();
							}
						}, 5000);
					}
				}
			}
    	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
	        case R.id.action_newgame:
	        	startNewGame();
	        	return true;
	        case R.id.action_settings:
	        	openSettingDialog();
	        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // start a new game
    private void startNewGame() {
    	description.setText("猜"+number_size+"個0~9不重複數字\t限制猜"+times+"次");
    	board.setText("");
    	gManager = new GuessManager(number_size, times);
    }
    // open the dialog in order to set the different mode
    private void openSettingDialog() {
    	LayoutInflater inflater = LayoutInflater.from(getBaseContext());
    	// the view will be shown in dialog
    	View dialog_view = inflater.inflate(R.layout.setting_dialog, null);
    	// radio group, let player choices different mode
    	RadioGroup radio_group = (RadioGroup) dialog_view.findViewById(R.id.dialog_radiogroup);
    	// show the description about each mode
    	final TextView radio_detail = (TextView) dialog_view.findViewById(R.id.dialog_textview_detail);
    	radio_detail.setText(R.string.dialog_detail);
    	radio_group.clearCheck();
    	radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
    		public void onCheckedChanged(RadioGroup group, int checkedId) {
    			switch(checkedId) {
    			case R.id.radio_easy:        //  set to easy mode
    				number_size = 3;
    				times = 10;
    				radio_detail.setText(R.string.easy_description);
    				break;
    			case R.id.radio_normal:    //  set to normal mode
    				number_size = 4;
    				times = 10;
    				radio_detail.setText(R.string.normal_description);
    				break;
    			case R.id.radio_hard:       //  set to hard mode
    				number_size = 5;
    				times = 15;
    				radio_detail.setText(R.string.hard_description);
    				break;
    			}
            }
        });
    	// dialog builder
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_title);     // dialog title
		builder.setView(dialog_view);               // dialog view
		builder.setPositiveButton(R.string.dialog_positive_button, new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateSharedPreferencesData();
				startNewGame();
				dialog.dismiss();     // close the dialog
			}
		});
		builder.setNegativeButton(R.string.dialog_negative_button, new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		// create dialog from builder and show it
		builder.create().show();
    }
}
