package com.phionsoft.zentriumph.models;

import java.util.ArrayList;

public class BusinessSectorInfo {
	private ArrayList<IndustrialEquipmentInfo> listIndustrialEquipment;
	private ArrayList<EmployeeInfo> listEmployee;
	private ArrayList<InputInfo> listInput;
	private ArrayList<OutputInfo> listOutput;
	
	public BusinessSectorInfo(ArrayList<IndustrialEquipmentInfo> ie, ArrayList<EmployeeInfo> e,
							ArrayList<InputInfo> i, ArrayList<OutputInfo> o){
		setListIndustrialEquipment(ie);
		setListEmployee(e);
		setListInput(i);
		setListOutput(o);
	}

	public void setListIndustrialEquipment(ArrayList<IndustrialEquipmentInfo> m) {
		listIndustrialEquipment = m;
	}

	public void setListEmployee(ArrayList<EmployeeInfo> e) {
		listEmployee = e;
	}

	public void setListInput(ArrayList<InputInfo> i) {
		listInput = i;
	}

	public void setListOutput(ArrayList<OutputInfo> o) {
		listOutput = o;
	}
	
	public ArrayList<IndustrialEquipmentInfo> getListIndustrialEquipment(){
		return listIndustrialEquipment;
	}
	
	public ArrayList<EmployeeInfo> getListEmployee(){
		return listEmployee;
	}
	
	public ArrayList<InputInfo> getListInput(){
		return listInput;
	}
	
	public ArrayList<OutputInfo> getListOutput(){
		return listOutput;
	}
}
