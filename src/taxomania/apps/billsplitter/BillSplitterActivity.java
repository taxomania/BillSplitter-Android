package taxomania.apps.billsplitter;

import java.text.NumberFormat;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BillSplitterActivity extends Activity {
	private static boolean decimal = false, decComp = false;
	private int noGuests, tip;
	private TextView guests, tips;
	private EditText et;
	private double sub;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		guests = (TextView) findViewById(R.id.noGuests);
		tips = (TextView) findViewById(R.id.tipVal);
		et = (EditText) findViewById(R.id.total);
		setListeners();
		initialise();
	}

	private void initialise() {
		et.setText("");
		noGuests = 4;
		tip = 10;
		tips.setText(tip + "%");
		guests.setText("" + noGuests);
		TextView tv = (TextView) findViewById(R.id.tipTot);
		tv.setText("");
		tv = (TextView) findViewById(R.id.tot);
		tv.setText("");
		tv = (TextView) findViewById(R.id.each);
		tv.setText("");
	}

	private void transition(ImageView view, int drawable) {
		TransitionDrawable transition = (TransitionDrawable) getResources()
				.getDrawable(drawable);
		transition.startTransition(75);
		view.setImageDrawable(transition);
	}

	private void initiateTransition(ImageView view, int d1, int d2) {
		transition(view, d1);
		transition(view, d2);
	}

	public void upTip(View view) {
		if (tip == 25)
			return;
		initiateTransition((ImageView) view, R.drawable.plus,
				R.drawable.reverseplus);
		tip = Integer.parseInt(tips.getText().toString().replace("%", "")) + 5;
		tips.setText(tip + "%");
		submit(view);
	}

	public void downTip(View view) {
		if (tip == 0)
			return;
		initiateTransition((ImageView) view, R.drawable.plusorminus,
				R.drawable.reverseminus);
		tip = Integer.parseInt(tips.getText().toString().replace("%", "")) - 5;
		tips.setText(tip + "%");
		submit(view);
	}

	public void upGuest(View view) {
		if (noGuests == 12)
			return;
		initiateTransition((ImageView) view, R.drawable.plus,
				R.drawable.reverseplus);
		noGuests = Integer.parseInt(guests.getText().toString()) + 1;
		guests.setText("" + noGuests);
		submit(view);
	}

	public void downGuest(View view) {
		if (noGuests == 1)
			return;
		initiateTransition((ImageView) view, R.drawable.plusorminus,
				R.drawable.reverseminus);
		noGuests = Integer.parseInt(guests.getText().toString()) - 1;
		guests.setText("" + noGuests);
		submit(view);
	}

	private double calcTip() {
		sub = Double.parseDouble(et.getText().toString());
		return sub * tip / 100.0;
	}
	private double calcTot(double totTip) {return sub + totTip;}
	private double calcEach(double tot) {return tot/noGuests;}

	public void submit(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		if (et.getText().toString().equals("")) return;
		NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.UK);
		double val = calcTip();
		TextView tv = (TextView) findViewById(R.id.tipTot);
		tv.setText(nf.format(val));
		val = calcTot(val);
		tv = (TextView) findViewById(R.id.tot);
		tv.setText(nf.format(val));
		tv = (TextView) findViewById(R.id.each);
		tv.setText(nf.format(calcEach(val)));
	}

	public void reset(View view) {
		if (view.getId() == R.id.total) et.setText("");
		else initialise();
	}

	private void setListeners() {
		TextView.OnEditorActionListener editListener = new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_NULL) {
					if (v.getText().toString().equals(""))
						v.setText("0.00");
					else if (!decimal)
						v.setText(v.getText().toString() + ".00");
					else if (!decComp)
						v.setText(v.getText().toString() + "0");
					submit(v);
				}
				return true;
			}
		};
		et.setOnEditorActionListener(editListener);
		TextWatcher watcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.contains("."))
					decimal = true;
				else
					decimal = false;
				if (!decimal) {
					if (str.length() == 5) {
						if (str.charAt(str.length() - 1) == '.')
							return;
						String st = str.substring(0, str.length() - 1);
						s.clear();
						s.append(st);
					}
				} else {
					String a = str.substring(str.lastIndexOf(".") + 1);
					if (a.length() != 2)
						decComp = false;
					if (a.length() == 3) {
						a = a.substring(0, 2);
						str = str.substring(0, str.length() - 1);
						str.concat(a);
						s.clear();
						s.append(str);
						decComp = true;
					}
					if (str.length() == 8) {
						String st = str.substring(0, str.length() - 1);
						s.clear();
						s.append(st);
					}
				}
			}
		};
		et.addTextChangedListener(watcher);
	}

}