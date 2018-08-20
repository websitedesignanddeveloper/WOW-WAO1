package com.application.wowwao1.Models;

import com.google.gson.annotations.SerializedName;

public class AdsData {

	@SerializedName("image")
	private String image;

	@SerializedName("name")
	private String name;

	@SerializedName("link")
	private String link;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private String id;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

}