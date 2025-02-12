package com.isl.modal;

import java.util.ArrayList;

public class EnergyMetaList {
	public ArrayList<Param> param;
	public ArrayList<DgType> dgtype;
	public ArrayList<FuelSuppliers> fuelsuppliers;
	public ArrayList<Vender> vendors;
	public ArrayList<BeansSites>  sites;
	public ArrayList<UserContact> user;

	public ArrayList<UserContact> getUser() {
		return user;
	}

	public void setUser(ArrayList<UserContact> user) {
		this.user = user;
	}

	public ArrayList<BeansSites> getSites() {
            return sites;
     }
    public void setSites(ArrayList<BeansSites> sites) {
            this.sites = sites;
     }
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
	public ArrayList<FuelSuppliers> getFuelsuppliers() {
		return fuelsuppliers;
	}
	public void setFuelsuppliers(ArrayList<FuelSuppliers> fuelsuppliers) {
		this.fuelsuppliers = fuelsuppliers;
	}
	public ArrayList<Vender> getVendors() {
		return vendors;
	}
	public void setVendors(ArrayList<Vender> vendors) {
		this.vendors = vendors;
	}	
}
