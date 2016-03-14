package com.example.sanggon.synaptopchallenge;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    private static final String TAG = BrowseActivity.class.getSimpleName();

    GridView gridView;
    PoiAdapter mAdapter;
    List<PointOfInterest> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        gridView = (GridView)findViewById(R.id.gridView);

        try {
            items = parseXML();
        } catch (org.xmlpull.v1.XmlPullParserException e) {
            Log.w(TAG, "XMLPullParserException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.w(TAG, "ioexception");
            e.printStackTrace();
        }

        mAdapter = new PoiAdapter(this, items);
        gridView.setAdapter(mAdapter);
        gridView.setNumColumns(3);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Do nothing for now
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id) {
            case R.id.action_settings:
                break;
            case R.id.grid:
                mAdapter.setMode(PoiAdapter.Mode.GRID);
                gridView.setNumColumns(3);
                break;
            case R.id.list:
                mAdapter.setMode(PoiAdapter.Mode.LIST);
                gridView.setNumColumns(1);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<PointOfInterest> parseXML() throws XmlPullParserException, IOException {
        InputStream inputstream = getResources().openRawResource(
                getResources().getIdentifier("places",
                        "raw", getPackageName()));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(inputstream, null);

        PointOfInterest poi;
        String title = "";
        String lng = "";
        String lat = "";
        String url = "";
        List<PointOfInterest> points = new ArrayList<>();
        String attrib = "";
        boolean entering = false;

        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                Log.i(TAG, "Start document");
            } else if(eventType == XmlPullParser.START_TAG) {
                if (xpp.getName().equals("dict")) {
                    title = "";
                    lng = "";
                    lat = "";
                    url = "";
                } else if (xpp.getName().equals("string")) {
                    entering = true;
                }
            } else if(eventType == XmlPullParser.END_TAG) {
                if (xpp.getName().equals("dict")) {
                    points.add(new PointOfInterest(title, lng, lat, url));
                } else if (xpp.getName().equals("string")) {
                    entering = false;
                }
            } else if(eventType == XmlPullParser.TEXT) {
                if (entering) {
                    if (attrib.equals("title")) {
                        title = xpp.getText();
                    } else if (attrib.equals("lng")) {
                        lng = xpp.getText();
                    } else if (attrib.equals("lat")) {
                        lat = xpp.getText();
                    } else if (attrib.equals("url")) {
                        url = xpp.getText();
                    }
                } else if (xpp.getText().equals("title")) {
                    attrib = "title";

                } else if (xpp.getText().equals("lng")) {
                    attrib = "lng";
                    lng = xpp.getText();
                } else if (xpp.getText().equals("lat")) {
                    attrib = "lat";
                    lat = xpp.getText();
                } else if (xpp.getText().equals("url")) {
                    attrib = "url";
                    url = xpp.getText();
                }
            }
            eventType = xpp.next();
        }
        return points;
    }
}
