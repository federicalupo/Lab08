package it.polito.tdp.extflightdelays.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		model.creaGrafo(500);
		System.out.println(model.nVertici());
		System.out.println(model.nArchi());
		
		//metodo 1 322, 1485
		
		//metodo 2-3 => 322, 2173
	}

}
