package com.phi.entities.dataTypes;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.json.CodeValueDianoemaDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.annotations.CodeValueEquivalent;
import com.phi.annotations.CodeValueExtension;

/**
 * CodeValue for "Dianoema" CodeSystem
 */

@Entity
@Table(name = "code_value_dianoema", uniqueConstraints= {@UniqueConstraint(columnNames={"oid","version"})})
@org.hibernate.annotations.Table(appliesTo = "code_value_dianoema", indexes = { @Index(name="IX_Code_Value_dianoema", columnNames = { "id", "version", "typeDB", "statusDB" } ) })
@Audited
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
@JsonDeserialize(using = CodeValueDianoemaDeserializer.class)
public class CodeValueDianoema extends CodeValue {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2704488721880524548L;

	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueDianoema.class)
    @JoinColumn(name="code_value_parent")
	@ForeignKey(name="FK_CodeValueDianoema_Parent")
	@Index(name="IX_CodeValueDianoema_Parent")
	public CodeValue getParent() {
		return parent;
	}

	@Override
	public void setParent(CodeValue parent) {
		this.parent = parent;
	}

	@Override
	@OneToMany(fetch=FetchType.LAZY, targetEntity=CodeValueDianoema.class, mappedBy="parent")
	public Collection<CodeValue> getChildren() {
		return children;
	}

	@Override
	public void setChildren(Collection<CodeValue> children) {
		this.children = children;
	}
	
	/**
	 * Code Extension score
	 */
	private Integer score;

	@Column(name="score")
	@CodeValueExtension(name="Score")
	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
	
	
	/**
	 * RELATION WITH DIANOEMA TYPE
	 */
	private Collection<CodeValueDianoema> relationsDianoema = new HashSet<CodeValueDianoema>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "code_equivalent_dianoema", joinColumns = { @JoinColumn(name = "cv_dianoema1", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "cv_dianoema2", nullable = false, updatable = false) })
	@ForeignKey(name = "FK_CV_DIANOEMA1", inverseName = "FK_CV_DIANOEMA2")
	@CodeValueEquivalent(targetType = CodeValueDianoema.class)
	public Collection<CodeValueDianoema> getRelationsDianoema() {
		return this.relationsDianoema;
	}

	public void setRelationsDianoema(
			Collection<CodeValueDianoema> relationsDianoema) {
		this.relationsDianoema = relationsDianoema;
	}

	/**
	 * INVERSE RELATION WITH DIANOEMA TYPE
	 */
	private Collection<CodeValueDianoema> inverseRelationsDianoema = new HashSet<CodeValueDianoema>();

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "relationsDianoema")
	@CodeValueEquivalent(targetType = CodeValueDianoema.class, isInverse = true)
	public Collection<CodeValueDianoema> getInverseRelationsDianoema() {
		return this.inverseRelationsDianoema;
	}

	public void setInverseRelationsDianoema(Collection<CodeValueDianoema> inverseRelationsDianoema) {
		this.inverseRelationsDianoema = inverseRelationsDianoema;
	}
}