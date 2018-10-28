package com.example.ais3.moneytaps.Models;

public class MoneyTapAdapter
{
    public String title = "";

    public thumbnail thumbnail = new thumbnail();

    public terms terms = new terms();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public com.example.ais3.moneytaps.Models.thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(com.example.ais3.moneytaps.Models.thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

   /* public com.example.ais3.moneytaps.Models.terms getTerms() {
        return terms;
    }

    public void setTerms(com.example.ais3.moneytaps.Models.terms terms) {
        this.terms = terms;
    }*/
}
