package com.isl.modal;

import java.util.ArrayList;

public class ResponceLoginList {
	public ArrayList<ResponseLoginDetails>  Details;
	public ArrayList<ResponceFormRightDetails>  Form;
	
	public ArrayList<ResponseLoginDetails> getDetails() {
		return Details;
	}
	public void setDetails(ArrayList<ResponseLoginDetails> details) {
		Details = details;
	}
	public ArrayList<ResponceFormRightDetails> getForm() {
		return Form;
	}
	public void setForm(ArrayList<ResponceFormRightDetails> form) {
		Form = form;
	}
}
