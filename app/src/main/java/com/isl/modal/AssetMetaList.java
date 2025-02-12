package com.isl.modal;

import java.util.ArrayList;

public class AssetMetaList {
	public ArrayList<Param> param;
	public ArrayList<DgType> dgtype;
	public ArrayList<Vender> vendors;
		
	public ArrayList<Param> getParam() {
		return param;
	}
	public void setParam(ArrayList<Param> param) {
		this.param = param;
	}
	public ArrayList<DgType> getDgtype() {
		return dgtype;
	}
	public void setDgtype(ArrayList<DgType> dgtype) {
		this.dgtype = dgtype;
	}
	
	public ArrayList<Vender> getVendors() {
		return vendors;
	}
	public void setVendors(ArrayList<Vender> vendors) {
		this.vendors = vendors;
	}	
}
