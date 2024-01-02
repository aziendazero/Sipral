package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "quest_tabella")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class QuestTabella extends BaseEntity {

	private static final long serialVersionUID = 588827331L;

	/**
	*  javadoc for bl03
	*/
	private Boolean bl03;

	@Column(name="bl03")
	public Boolean getBl03(){
		return bl03;
	}

	public void setBl03(Boolean bl03){
		this.bl03 = bl03;
	}

	/**
	*  javadoc for code03
	*/
	private CodeValuePhi code03;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code03")
	@ForeignKey(name="FK_QuestTabella_code03")
	@Index(name="IX_QuestTabella_code03")
	public CodeValuePhi getCode03(){
		return code03;
	}

	public void setCode03(CodeValuePhi code03){
		this.code03 = code03;
	}

	/**
	*  javadoc for code02
	*/
	private CodeValuePhi code02;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code02")
	@ForeignKey(name="FK_QuestTabella_code02")
	@Index(name="IX_QuestTabella_code02")
	public CodeValuePhi getCode02(){
		return code02;
	}

	public void setCode02(CodeValuePhi code02){
		this.code02 = code02;
	}

	/**
	*  javadoc for code01
	*/
	private CodeValuePhi code01;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code01")
	@ForeignKey(name="FK_QuestTabella_code01")
	@Index(name="IX_QuestTabella_code01")
	public CodeValuePhi getCode01(){
		return code01;
	}

	public void setCode01(CodeValuePhi code01){
		this.code01 = code01;
	}

	/**
	*  javadoc for bl02
	*/
	private Boolean bl02;

	@Column(name="bl02")
	public Boolean getBl02(){
		return bl02;
	}

	public void setBl02(Boolean bl02){
		this.bl02 = bl02;
	}

	/**
	*  javadoc for bl01
	*/
	private Boolean bl01;

	@Column(name="bl01")
	public Boolean getBl01(){
		return bl01;
	}

	public void setBl01(Boolean bl01){
		this.bl01 = bl01;
	}

	/**
	*  javadoc for text09
	*/
	private String text09;

	@Column(name="text09")
	public String getText09(){
		return text09;
	}

	public void setText09(String text09){
		this.text09 = text09;
	}

	/**
	*  javadoc for text08
	*/
	private String text08;

	@Column(name="text08")
	public String getText08(){
		return text08;
	}

	public void setText08(String text08){
		this.text08 = text08;
	}

	/**
	*  javadoc for text07
	*/
	private String text07;

	@Column(name="text07")
	public String getText07(){
		return text07;
	}

	public void setText07(String text07){
		this.text07 = text07;
	}

	/**
	*  javadoc for text06
	*/
	private String text06;

	@Column(name="text06")
	public String getText06(){
		return text06;
	}

	public void setText06(String text06){
		this.text06 = text06;
	}

	/**
	*  javadoc for text05
	*/
	private String text05;

	@Column(name="text05")
	public String getText05(){
		return text05;
	}

	public void setText05(String text05){
		this.text05 = text05;
	}

	/**
	*  javadoc for text04
	*/
	private String text04;

	@Column(name="text04")
	public String getText04(){
		return text04;
	}

	public void setText04(String text04){
		this.text04 = text04;
	}

	/**
	*  javadoc for text03
	*/
	private String text03;

	@Column(name="text03")
	public String getText03(){
		return text03;
	}

	public void setText03(String text03){
		this.text03 = text03;
	}

	/**
	*  javadoc for text02
	*/
	private String text02;

	@Column(name="text02")
	public String getText02(){
		return text02;
	}

	public void setText02(String text02){
		this.text02 = text02;
	}

	/**
	*  javadoc for text01
	*/
	private String text01;

	@Column(name="text01")
	public String getText01(){
		return text01;
	}

	public void setText01(String text01){
		this.text01 = text01;
	}

	/**
	*  javadoc for numProgr
	*/
	private Integer numProgr;

	@Column(name="num_progr")
	public Integer getNumProgr(){
		return numProgr;
	}

	public void setNumProgr(Integer numProgr){
		this.numProgr = numProgr;
	}

	/**
	*  javadoc for question
	*/
	private String question;

	@Column(name="question")
	public String getQuestion(){
		return question;
	}

	public void setQuestion(String question){
		this.question = question;
	}

	/**
	*  javadoc for entitySeq
	*/
	private String entitySeq;

	@Column(name="entity_seq")
	public String getEntitySeq(){
		return entitySeq;
	}

	public void setEntitySeq(String entitySeq){
		this.entitySeq = entitySeq;
	}

	/**
	*  javadoc for entityPath
	*/
	private String entityPath;

	@Column(name="entity_path")
	public String getEntityPath(){
		return entityPath;
	}

	public void setEntityPath(String entityPath){
		this.entityPath = entityPath;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "QuestTabella_sequence")
	@SequenceGenerator(name = "QuestTabella_sequence", sequenceName = "QuestTabella_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
