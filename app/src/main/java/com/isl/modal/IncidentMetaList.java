package com.isl.modal;
import java.util.ArrayList;

public class IncidentMetaList {
	public ArrayList<Equipment> equipment;
	public ArrayList<Param> param;
	public ArrayList<Operators> opertors;
	public ArrayList<RcaCateg> rcaCategory;
	public ArrayList<Groups> groups;
	public ArrayList<BeanSpareParts> spareParts;
	public ArrayList<UserContact> user;
	public ArrayList<DgType> dgtype;


	public ArrayList<DgType> getDgtype() {
		return dgtype;
	}

	public void setDgtype(ArrayList<DgType> dgtype) {
		this.dgtype = dgtype;
	}

	/**
	 * @return the equipment
	 */
	public ArrayList<Equipment> getEquipment() {
		return equipment;
	}
	/**
	 * @param equipment the equipment to set
	 */
	public void setEquipment(ArrayList<Equipment> equipment) {
		this.equipment = equipment;
	}
	/**
	 * @return the param
	 */
	public ArrayList<Param> getParam() {
		return param;
	}
	/**
	 * @param param the param to set
	 */
	public void setParam(ArrayList<Param> param) {
		this.param = param;
	}
	/**
	 * @return the opertors
	 */
	public ArrayList<Operators> getOpertors() {
		return opertors;
	}
	/**
	 * @param opertors the opertors to set
	 */
	public void setOpertors(ArrayList<Operators> opertors) {
		this.opertors = opertors;
	}
	/**
	 * @return the rcaCategory
	 */
	public ArrayList<RcaCateg> getRcaCategory() {
		return rcaCategory;
	}
	/**
	 * @param rcaCategory the rcaCategory to set
	 */
	public void setRcaCategory(ArrayList<RcaCateg> rcaCategory) {
		this.rcaCategory = rcaCategory;
	}
	/**
	 * @return the groups
	 */
	public ArrayList<Groups> getGroups() {
		return groups;
	}
	/**
	 * @param groups the groups to set
	 */
	public void setGroups(ArrayList<Groups> groups) {
		this.groups = groups;
	}
	public ArrayList<BeanSpareParts> getSpareParts() {
		return spareParts;
	}
	public void setSpareParts(ArrayList<BeanSpareParts> spareParts) {
		this.spareParts = spareParts;
	}
	
	public ArrayList<UserContact> getUser() {
		return user;
	}
	public void setUser(ArrayList<UserContact> user) {
		this.user = user;
	}
	
	
}
