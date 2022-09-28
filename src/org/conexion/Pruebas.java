package org.conexion;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;




public class Pruebas {

	private static final String PATHCERTIFICADOSDIGITALES = "C:\\ConfigFEArg\\certificados\\padronarba.jks"; 

	public static void main(String[] args) throws Exception {
		Conexion con = new Conexion("https://cot.arba.gov.ar/TransporteBienes/SeguridadCliente/", "");
		Map<String,String> params = new HashMap<String,String>();
		File file = new File("C:/Users/Matias/Documents/TB_33714210349_000000_20220404_000005.txt");

		params.put("usuario", "33714210349");
		params.put("password", "637884");
		params.put("file", file.getAbsolutePath());

//		System.out.print(con.post("presentarRemitos.do", null,params.toString()));
		



		//add your file here.
		         /*This is to add file content*/
		MultipartUtility multipart;
		List<String> response = new LinkedList<String>();
        try {
    		multipart = new MultipartUtility("https://cot.arba.gov.ar/TransporteBienes/SeguridadCliente/presentarRemitos.do",
    										"utf-8");

//			multipart.addFormField("user", "33714210349");
//			multipart.addFormField("password", "637884");
//			multipart.addFilePart("file", file);
			response = multipart.finish2(file);

		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        StringBuilder resp = new StringBuilder();
        for(String string: response){
        	resp.append(string +"\n");
        }
				
        System.out.print(resp);
		    
		    
        
	    
	}
	
	
}
