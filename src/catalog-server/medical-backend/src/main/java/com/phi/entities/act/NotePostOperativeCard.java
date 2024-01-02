package com.phi.entities.act;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.PostOperativeCard;
import javax.persistence.Column;
@javax.persistence.Entity
@Table(name = "note_post_op_card")
@Audited
public class NotePostOperativeCard extends Annotation {

	private static final long serialVersionUID = 609283126L;

	/**
	*  javadoc for values60
	*/
	private String values60;

	@Column(name="values60")
	public String getValues60(){
		return values60;
	}

	public void setValues60(String values60){
		this.values60 = values60;
	}
	
	/**
	*  javadoc for values45
	*/
	private String values45;

	@Column(name="values45")
	public String getValues45(){
		return values45;
	}

	public void setValues45(String values45){
		this.values45 = values45;
	}
	
	/**
	*  javadoc for values30
	*/
	private String values30;

	@Column(name="values30")
	public String getValues30(){
		return values30;
	}

	public void setValues30(String values30){
		this.values30 = values30;
	}

	/**
	*  javadoc for values15
	*/
	private String values15;

	@Column(name="values15")
	public String getValues15(){
		return values15;
	}

	public void setValues15(String values15){
		this.values15 = values15;
	}

	/**
	*  javadoc for textNote
	*/
	private String textNote;

	@Column(name="text_note")
	public String getTextNote(){
		return textNote;
	}

	public void setTextNote(String textNote){
		this.textNote = textNote;
	}
	
	/**
	*  javadoc for entranceValue
	*/
	private String entranceValue;

	@Column(name="entrance_value")
	public String getEntranceValue(){
		return entranceValue;
	}

	public void setEntranceValue(String entranceValue){
		this.entranceValue = entranceValue;
	}

	/**
	*  javadoc for appliedTherapy
	*/
	private String appliedTherapy;

	@Column(name="applied_therapy")
	public String getAppliedTherapy(){
		return appliedTherapy;
	}

	public void setAppliedTherapy(String appliedTherapy){
		this.appliedTherapy = appliedTherapy;
	}


	/**
	*  javadoc for postOperativeCard
	*/
	private PostOperativeCard postOperativeCard;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="post_operative_card_id")
	@ForeignKey(name="FK_Note_postOpCard")
	@Index(name="IX_Note_postOpCard")
	public PostOperativeCard getPostOperativeCard(){
		return postOperativeCard;
	}

	public void setPostOperativeCard(PostOperativeCard postOperativeCard){
		this.postOperativeCard = postOperativeCard;
	}


}
