package com.example.luka.cookforreal.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by luka on 8/4/2016.
 */
public class CookModel {
    private String id;
    private String title;
    @SerializedName("image_file_name")
    private String image;
    private String likes;
    @SerializedName("preparation_time")
    private String default_preparation;
    private List<Steps> steps;
    private List<Tags> tags;
    private List<Ingredients> ingredients;


    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public List<Ingredients> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        this.ingredients = ingredients;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getDefault_preparation() {
        return default_preparation;
    }

    public void setDefault_preparation(String default_preparation) {
        this.default_preparation = default_preparation;
    }





    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public List<Steps> getStoryList() {
        return steps;
    }

    public void setStoryList(List<Steps> storyList) {
        this.steps = storyList;
    }




    public static class Steps{
        private String text;
        private String timer;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getTimer() {
            return timer;
        }

        public void setTimer(String timer) {
            this.timer = timer;
        }
    }



    public static class Tags{
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
    public static class Ingredients{
        private String id;
        private String quantity;
        private String preferred_measure;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPreferred_measure() {
            return preferred_measure;
        }

        public void setPreferred_measure(String preferred_measure) {
            this.preferred_measure = preferred_measure;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
    }

}
