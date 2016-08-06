package com.example.luka.cookforreal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luka.cookforreal.models.CookModel;
import com.example.luka.cookforreal.models.IngredientsModel;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SecondActivity extends AppCompatActivity {
    private ListView lvCooks;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        lvCooks = (ListView)findViewById(R.id.lvCooks);
        new JSONTasks().execute("http://46.101.236.188/v1/recipes/get-init-recipes","http://46.101.236.188/v1/ingredients/get-ingredients");

    }
    public class JSONTasks extends AsyncTask<String, String, List<CookModel>> {
        @Override
        protected List<CookModel> doInBackground(String... params) {
            HashMap<String, String> hashIngredients = null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(params[1]).build();
            try {
                Response responseIngredients = client.newCall(request).execute();
                String final_json_ingredients = responseIngredients.body().string();
                JSONObject parentObjectIngredients = new JSONObject(final_json_ingredients);
                JSONArray parentArrayIngredients = parentObjectIngredients.getJSONArray("ingredients");
                hashIngredients = new HashMap<>();
                Gson gsonIngredients = new Gson();
                for (int i = 0; i < parentArrayIngredients.length(); i++) {
                    JSONObject finalObject = parentArrayIngredients.getJSONObject(i);
                    IngredientsModel ingredientsModel = gsonIngredients.fromJson(finalObject.toString(), IngredientsModel.class);
                    hashIngredients.put(ingredientsModel.getId(), ingredientsModel.getName());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpClient client1 = new OkHttpClient();
            Request request1 = new Request.Builder().url(params[0]).build();

            try {
                Response response = client.newCall(request1).execute();
                String final_json = response.body().string();
                JSONObject parentObject = new JSONObject(final_json);
                JSONArray parentArray = parentObject.getJSONArray("recipes");
                List<CookModel> cookModelList = new ArrayList<>();


                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    CookModel cookModel = new CookModel();
                    cookModel.setTitle(finalObject.getString("title"));
                    cookModel.setDefault_preparation(finalObject.getString("preparation_time"));
                    List<CookModel.Steps> stepsList = new ArrayList<>();
                    for (int j = 0; j < finalObject.getJSONArray("steps").length(); j++) {
                        CookModel.Steps steps = new CookModel.Steps();
                        steps.setText(finalObject.getJSONArray("steps").getJSONObject(j).getString("text"));
                        steps.setTimer(finalObject.getJSONArray("steps").getJSONObject(j).getString("timer"));
                        stepsList.add(steps);
                    }
                    cookModel.setSteps(stepsList);
                    List<CookModel.Ingredients> ingredientsList = new ArrayList<>();
                    for (int l = 0; l < finalObject.getJSONArray("ingredients").length(); l++) {
                        CookModel.Ingredients ingredients = new CookModel.Ingredients();
                        ingredients.setId(finalObject.getJSONArray("ingredients").getJSONObject(l).getString("id"));

                        if (hashIngredients != null) {
                            if (hashIngredients.get(ingredients.getId()) != null) {
                                ingredients.setName(hashIngredients.get(ingredients.getId()));
                            } else {
                                ingredients.setName("NE znamo :)");
                            }
                        }


                        ingredientsList.add(ingredients);
                    }
                    cookModelList.add(cookModel);
                }

                return cookModelList;

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<CookModel> result) {
            super.onPostExecute(result);
            CookAdapter adapters = new CookAdapter(getApplicationContext(), R.layout.owa, result);
            lvCooks.setAdapter(adapters);
        }
    }

    public class CookAdapter extends ArrayAdapter
    {
        private List<CookModel> cookModelList;
        private int resource;
        private LayoutInflater inflater;
        public CookAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            cookModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(resource, null);
                holder.tvTitles = (TextView) findViewById(R.id.tvTitles);
                holder.tvPreps = (TextView) findViewById(R.id.tvPreps);
                holder.tvStorys = (TextView) findViewById(R.id.tvStorys);
                holder.tvIngris = (TextView) findViewById(R.id.tvIngris);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvTitles.setText(cookModelList.get(position).getTitle());
            holder.tvPreps.setText(cookModelList.get(position).getDefault_preparation());
            StringBuffer storyStringBuff = new StringBuffer();
            StringBuffer ingriStringBuff = new StringBuffer();
            int jaBrojim = 1;
            for (CookModel.Steps steps : cookModelList.get(position).getSteps()) {

                String jaBrojimAliUStringu = Integer.toString(jaBrojim);
                storyStringBuff.append(jaBrojimAliUStringu + ".korak:\n" + steps.getText() + "\n" + "Portebno vremena: " + steps.getTimer() + "\n\n");
                jaBrojim++;
            }
            holder.tvStorys.setText(storyStringBuff);
            ingriStringBuff.append("SASTOJCI\n");
            for (CookModel.Ingredients ingri : cookModelList.get(position).getIngredients()) {
                ingriStringBuff.append(ingri.getName() + ", ");
            }
            holder.tvIngris.setText(ingriStringBuff);

            return convertView;
        }
        class ViewHolder{

            TextView tvTitles;
            TextView tvPreps;
            TextView tvStorys;
            TextView tvIngris;
        }
    }
}



