package org.sachinjain.cryptocalculator;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.jar.JarOutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MarketsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MarketsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView mBTC;
    private TextView mBCH;
    private TextView mETH;
    private TextView mLTC;

    public MarketsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarketsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarketsFragment newInstance(String param1, String param2) {
        MarketsFragment fragment = new MarketsFragment();
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

    public String fix_price(double input){
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###,###.00");
        return decimalFormat.format(input);
    }

    public class JSONTask extends AsyncTask<String, String, String>{

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
                double btc2usd = jsonObject.getJSONObject("BTC").getDouble("USD");
                double btc2eur = jsonObject.getJSONObject("BTC").getDouble("EUR");
                double bch2usd = jsonObject.getJSONObject("BCH").getDouble("USD");
                double bch2eur = jsonObject.getJSONObject("BCH").getDouble("EUR");
                double eth2usd = jsonObject.getJSONObject("ETH").getDouble("USD");
                double eth2eur = jsonObject.getJSONObject("ETH").getDouble("EUR");
                double ltc2usd = jsonObject.getJSONObject("LTC").getDouble("USD");
                double ltc2eur = jsonObject.getJSONObject("LTC").getDouble("EUR");

                String[] btcArray = mBTC.getText().toString().split("\n");
                String[] bchArray = mBCH.getText().toString().split("\n");
                String[] ethArray = mETH.getText().toString().split("\n");
                String[] ltcArray = mLTC.getText().toString().split("\n");

                // "LTC\n\n$\n€\n\n1D: \n1W: "
                // 'ltc', '', '$', '€', '', '1D: ', '1W: '

                btcArray[2] = "$" + fix_price(btc2usd);
                btcArray[3] = "€" + fix_price(btc2eur);
                mBTC.setText(TextUtils.join("\n", btcArray));
                bchArray[2] = "$" + fix_price(bch2usd);
                bchArray[3] = "€" + fix_price(bch2eur);
                mBCH.setText(TextUtils.join("\n", bchArray));
                ethArray[2] = "$" + fix_price(eth2usd);
                ethArray[3] = "€" + fix_price(eth2eur);
                mETH.setText(TextUtils.join("\n", ethArray));
                ltcArray[2] = "$" + fix_price(ltc2usd);
                ltcArray[3] = "€" + fix_price(ltc2eur);
                mLTC.setText(TextUtils.join("\n", ltcArray));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_markets, container, false);
        mBTC = (TextView)view.findViewById(R.id.BTC_block);
        mBCH = (TextView)view.findViewById(R.id.BCH_block);
        mETH = (TextView)view.findViewById(R.id.ETH_block);
        mLTC = (TextView)view.findViewById(R.id.LTC_block);
        new JSONTask().execute("https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,BCH,ETH,LTC&tsyms=USD,EUR");

        Button mRefreshButton = (Button)view.findViewById(R.id.refresh_button);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTask().execute("https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,BCH,ETH,LTC&tsyms=USD,EUR");
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
            // Toast.makeText(context, "Markets Fragment Attached", Toast.LENGTH_SHORT).show();
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
