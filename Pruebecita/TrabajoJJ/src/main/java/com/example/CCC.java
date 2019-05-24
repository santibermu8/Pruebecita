package com.example;

import java.io.IOException;

import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;


import org.xml.sax.SAXException;

@WebServlet(
    name = "ComputerComponetsComparator",
    urlPatterns = {"/ccc"}
)



public class CCC extends HttpServlet {
	
	public FuenteDato scrapPCC = new ScrappingPCC();
	public FuenteDato scrapCU = new ScrappingCU();
	public AmazonAPI amazon = new AmazonAPI();
	private ArrayList<Producto> productos = new ArrayList<Producto>();
	
	
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
    productos = new ArrayList<Producto>(); 
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");

    
    IntegracionDatos intDatos = new IntegracionDatos(productos,scrapPCC.query(request.getParameter("query")),60);
    intDatos.procesarDatosCU(productos, scrapCU.query(request.getParameter("query")));
    
    AmazonXPath amazonXPath;

    /// PARA VER SALIDAS DE AMAZON
    /*String salida_amazon = amazon.query("intel core i7");
    response.getWriter().print(salida_amazon);*/
    
    
    ArrayList<String> datos_amazon = new ArrayList<String>();
    for(int i=0; i<productos.size(); i++) {
	    amazonXPath = new AmazonXPath(productos.get(i).getNombre());
	    try {
			datos_amazon = amazonXPath.getInformacion();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(!datos_amazon.isEmpty())
	    	productos.get(i).addOferta(datos_amazon, "Amazon");
    }
    
    
    
    request.setAttribute("MatchedProducts", productos);
    RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
    try {
		dispatcher.forward(request, response);
	} catch (ServletException e) {
		e.printStackTrace();
	}
  }
  
  public ArrayList<Producto> getProductoNombre(String nombre)
  {
	  ArrayList<Producto> productos_matching = new ArrayList<Producto>();
	  
	  for(int i = 0; i < productos.size(); ++i)
	  {
		  if(productos.get(i).nameMatching(nombre))
			  productos_matching.add(productos.get(i));
	  }
	  
	  return productos_matching;
  }
}