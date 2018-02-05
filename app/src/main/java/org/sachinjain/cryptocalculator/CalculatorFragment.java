package org.sachinjain.cryptocalculator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import static org.sachinjain.cryptocalculator.R.color.btc;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CalculatorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CalculatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalculatorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ToggleButton mToggleCurrency;

    private Button mBTC;
    private Button mBCH;
    private Button mETH;
    private Button mLTC;

    private Button mCurrentPrice;
    private Button mCalculate;
    private Button mClear;

    private EditText mCoinQuantity;
    private EditText mBuyPrice;
    private EditText mSellPrice;
    private EditText mTransactionFees;

    private TextView mCalculation;

    private int status;
    private int currency_status;

    private FrameLayout mBackground;

    private OnFragmentInteractionListener mListener;

    public CalculatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalculatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalculatorFragment newInstance(String param1, String param2) {
        CalculatorFragment fragment = new CalculatorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public String currCoin(){
        if(status == 1){
            return "BTC";
        } else if (status == 2){
            return "BCH";
        } else if (status == 3){
            return "ETH";
        } else if (status == 4){
            return "LTC";
        }
        return null;
    }

    public String fix_price(double input){
        DecimalFormat decimalFormat = new DecimalFormat("###########0.00");
        return decimalFormat.format(input);
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                double resultVal = 0.00;
                if (currency_status == 1){
                    resultVal = jsonObject.getDouble("USD");
                } else {
                    resultVal = jsonObject.getDouble("EUR");
                }
                mSellPrice.setText(fix_price(resultVal));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try{
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                return buffer.toString();

            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null){
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                Double multiplier = null;
                String sign = "$";
                if (currency_status == 2){
                    multiplier = 1/jsonObject.getDouble("USD");
                    sign = "€";
                } else if (currency_status == 1){
                    multiplier = 1/jsonObject.getDouble("EUR");
                }

                if (TextUtils.isEmpty(mBuyPrice.getText()) == false){
                    Double price = Double.parseDouble(mBuyPrice.getText().toString());
                    Double converted = price * multiplier;
                    mBuyPrice.setText(fix_price(converted));
                }
                if (TextUtils.isEmpty(mSellPrice.getText()) == false){
                    Double price = Double.parseDouble(mSellPrice.getText().toString());
                    Double converted = price * multiplier;
                    mSellPrice.setText(fix_price(converted));
                }
                if (TextUtils.isEmpty(mTransactionFees.getText()) == false){
                    Double price = Double.parseDouble(mTransactionFees.getText().toString());
                    Double converted = price * multiplier;
                    mTransactionFees.setText(fix_price(converted));
                }
                if (TextUtils.isEmpty(mCalculation.getText()) == false){
                    Double price = Double.parseDouble(mCalculation.getText().toString().substring(1));
                    Double converted = price * multiplier;
                    mCalculation.setText(sign + fix_price(converted));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        status = 1;
        currency_status = 1;

        mBackground = (FrameLayout)view.findViewById(R.id.background);

        final int btcColor = getResources().getColor(R.color.btc);
        final int bchColor = getResources().getColor(R.color.bch);
        final int ethColor = getResources().getColor(R.color.eth);
        final int ltcColor = getResources().getColor(R.color.ltc);
        final int profit = getResources().getColor(R.color.darkgreen);
        final int loss = getResources().getColor(R.color.darkred);
        final int gray = getResources().getColor(R.color.darkgray);

        final Drawable dollar = getResources().getDrawable(R.drawable.ic_attach_money_black_24dp);
        final Drawable euro = getResources().getDrawable(R.drawable.ic_euro_symbol_black_24dp);
        final Drawable btc = getResources().getDrawable(R.drawable.ic_bitcoin);
        final Drawable bch = getResources().getDrawable(R.drawable.ic_bitcoincash);
        final Drawable eth = getResources().getDrawable(R.drawable.ic_ethereum);
        final Drawable ltc = getResources().getDrawable(R.drawable.ic_litecoin);

        mToggleCurrency = (ToggleButton)view.findViewById(R.id.toggle_currency);

        mBTC = (Button)view.findViewById(R.id.BTC_button);
        mBCH = (Button)view.findViewById(R.id.BCH_button);
        mETH = (Button)view.findViewById(R.id.ETH_button);
        mLTC = (Button)view.findViewById(R.id.LTC_button);

        mCurrentPrice = (Button)view.findViewById(R.id.current_price);
        mCalculate = (Button)view.findViewById(R.id.calculate);
        mClear = (Button)view.findViewById(R.id.clear_button);

        mCoinQuantity = (EditText) view.findViewById(R.id.coinquantity);
        mBuyPrice = (EditText)view.findViewById(R.id.buyprice);
        mSellPrice = (EditText)view.findViewById(R.id.sellprice);
        mTransactionFees = (EditText)view.findViewById(R.id.fees);
        mCalculation = (TextView)view.findViewById(R.id.calculation);

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCoinQuantity.setText("");
                mBuyPrice.setText("");
                mSellPrice.setText("");
                mTransactionFees.setText("");
                mCalculation.setText("");
            }
        });

        mToggleCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mCoinQuantity.setText("");
                // mBuyPrice.setText("");
                // mSellPrice.setText("");
                // mTransactionFees.setText("");
                if(mToggleCurrency.isChecked()){
                    currency_status = 2;
                    mBuyPrice.setCompoundDrawablesWithIntrinsicBounds(euro, null, null, null);
                    mSellPrice.setCompoundDrawablesWithIntrinsicBounds(euro, null, null, null);
                    mTransactionFees.setCompoundDrawablesWithIntrinsicBounds(euro, null, null, null);
                } else{
                    currency_status = 1;
                    mBuyPrice.setCompoundDrawablesWithIntrinsicBounds(dollar, null, null, null);
                    mSellPrice.setCompoundDrawablesWithIntrinsicBounds(dollar, null, null, null);
                    mTransactionFees.setCompoundDrawablesWithIntrinsicBounds(dollar, null, null, null);
                }

                String currency = "USD";
                if (currency_status == 2){
                    currency = "EUR";
                }
                String link = "https://min-api.cryptocompare.com/data/price?fsym=" + currency + "&tsyms=USD,EUR";
                new JSONTask2().execute(link);
            }
        });

        mCurrentPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute("https://min-api.cryptocompare.com/data/price?fsym=" + currCoin() + "&tsyms=USD,EUR");
            }
        });

        mCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mCoinQuantity.getText())){
                    mCoinQuantity.setError("Required!");
                    return;
                } else if (TextUtils.isEmpty(mBuyPrice.getText())){
                    mBuyPrice.setError("Required!");
                    return;
                } else if (TextUtils.isEmpty(mSellPrice.getText())){
                    mSellPrice.setError("Required!");
                    return;
                }
                if (TextUtils.isEmpty(mTransactionFees.getText())){
                    mTransactionFees.setText("0");
                }

                double coinQuant = Double.parseDouble(mCoinQuantity.getText().toString());
                double buyPrice = Double.parseDouble(mBuyPrice.getText().toString());
                double sellPrice = Double.parseDouble(mSellPrice.getText().toString());
                double tFees = Double.parseDouble(mTransactionFees.getText().toString());

                if (mTransactionFees.getText().toString().equals("0")){
                    mTransactionFees.setText("");
                }

                double initVal = coinQuant * buyPrice;
                double finalVal = coinQuant * sellPrice;
                double result = finalVal - initVal - tFees;

                if (result < 0.0){
                    mCalculation.setTextColor(loss);
                } else if (result > 0.0){
                    mCalculation.setTextColor(profit);
                } else{
                    mCalculation.setTextColor(gray);
                }

                String sign = "$";
                if (currency_status == 2){
                    sign = "€";
                }

                mCalculation.setText(sign + fix_price(result));

            }
        });

        // https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD

        mBTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackground.setBackgroundColor(btcColor);
                mCoinQuantity.setCompoundDrawablesWithIntrinsicBounds(btc, null, null, null);
                if (status != 1){
                    mCoinQuantity.setText("");
                    mBuyPrice.setText("");
                    mSellPrice.setText("");
                    mTransactionFees.setText("");
                    mCalculation.setText("");
                }
                status = 1;
            }
        });

        mBCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackground.setBackgroundColor(bchColor);
                mCoinQuantity.setCompoundDrawablesWithIntrinsicBounds(bch, null, null, null);
                if (status != 2){
                    mCoinQuantity.setText("");
                    mBuyPrice.setText("");
                    mSellPrice.setText("");
                    mTransactionFees.setText("");
                    mCalculation.setText("");
                }
                status = 2;
            }
        });

        mETH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackground.setBackgroundColor(ethColor);
                mCoinQuantity.setCompoundDrawablesWithIntrinsicBounds(eth, null, null, null);
                if (status != 3){
                    mCoinQuantity.setText("");
                    mBuyPrice.setText("");
                    mSellPrice.setText("");
                    mTransactionFees.setText("");
                    mCalculation.setText("");
                }
                status = 3;
            }
        });

        mLTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackground.setBackgroundColor(ltcColor);
                mCoinQuantity.setCompoundDrawablesWithIntrinsicBounds(ltc, null, null, null);
                if (status != 4){
                    mCoinQuantity.setText("");
                    mBuyPrice.setText("");
                    mSellPrice.setText("");
                    mTransactionFees.setText("");
                    mCalculation.setText("");
                }
                status = 4;
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            // Toast.makeText(context, "Calculator Fragment Attached", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
