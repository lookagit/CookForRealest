package com.example.luka.cookforreal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
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
import android.widget.ImageButton;
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
    ImageView icons;
    TextView tvTitles;
    TextView tvPreps;
    TextView tvStorys;
    TextView tvIngris;
    ImageButton floatButton;
    HashMap<String, String> hashIngredients = null;
    private String idFromMain;
    public String ajdeMolimTe;
    public int brojim = 0;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_second);
        Bundle extras = getIntent().getExtras();
        idFromMain = extras.getString("data");
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Odma dodje brt poyyy");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
                .showImageOnFail(R.drawable.ic_empty) // resource or drawable
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
         icons = (ImageView)findViewById(R.id.ivIcons);
         tvTitles = (TextView)findViewById(R.id.tvTitles);
         tvPreps = (TextView)findViewById(R.id.tvPreps);
         tvStorys = (TextView)findViewById(R.id.tvSteps);
         tvIngris = (TextView)findViewById(R.id.tvIngris);
         floatButton  = (ImageButton)findViewById(R.id.imageButton);
         floatButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                 startActivity(intent);
             }
         });
        new JSONTasks().execute("http://46.101.236.188/v1/recipes/get-init-recipes","http://46.101.236.188/v1/ingredients/get-ingredients");

    }
    public class JSONTasks extends AsyncTask<String, String, List<CookModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
        @Override
        protected List<CookModel> doInBackground(String... params) {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(params[1]).build();
            try {
                Response responseIngredients = client.newCall(request).execute();
                String final_json_ingredients = responseIngredients.body().string();
                JSONObject parentObjectIngredients = new JSONObject(final_json_ingredients);
                JSONArray parentArrayIngredients = parentObjectIngredients.getJSONArray("ingredients");
                hashIngredients = new HashMap<String, String>();
                for (int i = 0; i < parentArrayIngredients.length(); i++) {
                    JSONObject finalObjectIngr = parentArrayIngredients.getJSONObject(i);
                     IngredientsModel cookModelIngred = new IngredientsModel();
                    cookModelIngred.setId(finalObjectIngr.getString("id"));
                    cookModelIngred.setName(finalObjectIngr.getString("name"));
                    hashIngredients.put(cookModelIngred.getId(), cookModelIngred.getName());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpClient client1 = new OkHttpClient();
            Request request1 = new Request.Builder().url(params[0]).build();

            try {
                Response response = client1.newCall(request1).execute();
                String final_json = response.body().string();
                JSONObject parentObject = new JSONObject(final_json);
                JSONArray parentArray = parentObject.getJSONArray("recipes");
                List<CookModel> cookModelList = new ArrayList<>();


                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    CookModel cookModel = new CookModel();
                    cookModel.setId(finalObject.getString("id"));
                    cookModel.setTitle(finalObject.getString("title"));
                    cookModel.setDefault_preparation(finalObject.getString("preparation_time"));
                    cookModel.setImage(finalObject.getString("image_file_name"));
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
                        ingredients.setName((String)hashIngredients.get(ingredients.getId()));
                        ingredientsList.add(ingredients);
                    }
                    cookModel.setIngredients(ingredientsList);
                    ajdeMolimTe = finalObject.getString("id");

                    if(ajdeMolimTe.equals(idFromMain))
                    {
                        brojim = i;
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
                     final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBars);

                    ImageLoader.getInstance().displayImage(result.get(brojim).getImage(), icons, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                    Typeface james = Typeface.createFromAsset(getAssets(), getString(R.string.typeface_devroyun));
                    tvTitles.setText(result.get(brojim).getTitle());
                    tvTitles.setTypeface(james);
                    tvPreps.setText("Ukupno vreme pripreme je oko "+result.get(brojim).getDefault_preparation()+"min.");
                    StringBuffer ingridienceBuff = new StringBuffer();
                    StringBuffer storyBuff = new StringBuffer();
                    int brojac = 1;
                    for (CookModel.Steps steps : result.get(brojim).getSteps()) {
                        String brojacString = Integer.toString(brojac);
                        storyBuff.append(brojacString + ".korak:\n " + steps.getText() + "\n\nTrajanje: " + steps.getTimer() + "min.\n\n");
                        brojac++;
                    }
                    tvStorys.setText(storyBuff);
                    int zarez = 0;
                    for (CookModel.Ingredients ing : result.get(brojim).getIngredients()) {
                        ingridienceBuff.append(ing.getName());
                        if(zarez < result.get(0).getIngredients().size()-1)
                        {
                            ingridienceBuff.append(" ,");
                        }else
                        {
                            ingridienceBuff.append(".");
                        }
                        zarez++;
                    }
                    tvIngris.setText(ingridienceBuff);
                    dialog.dismiss();


        }
    }
}





