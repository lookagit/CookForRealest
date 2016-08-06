package com.example.luka.cookforreal.models;

import java.util.List;

/**
 * Created by luka on 8/5/2016.
 */
public class TagsModel {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Tags> getSteps() {
        return steps;
    }

    public void setSteps(List<Tags> steps) {
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String id;
    private String name;
    private List<Tags> steps;



    public static class Tags {
        private String id;
        private String  name;
        private String tag_category_id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag_category_id() {
            return tag_category_id;
        }

        public void setTag_category_id(String tag_category_id) {
            this.tag_category_id = tag_category_id;
        }
    }
}
