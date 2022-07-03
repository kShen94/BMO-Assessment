package com.domain;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;  
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;  

@Path("/valet")  
public class Api {  
	String URI = "https://www.bankofcanada.ca/valet/observations/FXUSDCAD,AVG.INTWO";
	double usdcadAverage = 0,corraAverage = 0;
	Valet valet;

	/**
	 * GET method that returns data given date params. If no params 
	 * @param start - Start date
	 * @param end - End Date
	 * @return - High, low, mean average USD/CAD rate for the period
	 * 		-High, low, mean average CORRA rate for the period.
	 * 		-Pearson coefficient of correlation between USD/CAD and CORRA.
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String dates(@QueryParam("start_date") String start, @QueryParam("end_date") String end) {
		//2022-06-20
		String startDate = "";
		String endDate = "";
		String query = "";
		String pcoeff = "";
		if(start!=null) {
			startDate = "start_date="+start.trim();
		}
		if(end !=null) {
			endDate = "end_date="+ end.trim();
		}
		if(!startDate.isEmpty() && !endDate.isEmpty())
			query = startDate+"&"+endDate;
		else
			query = startDate+endDate;
		String data = query(query);
		if(valet != null)
			pcoeff = pearsonCoeff();
		return data + pcoeff;
	}
	
	/**
	 * Calculates the pearson coefficient correlation using the valet data
	 * @return - html text of correlation
	 */
	public String pearsonCoeff() {
		float xy = 0,x2 =0,y2 = 0;
		for(Observations obs:valet.getObservations()) {
			//some cases where AVG.INTWO was missing, FXUSDCAD did not exist before 2017
			if(obs.getAVGINTWO()!=null && obs.getFXUSDCAD() !=null) {
				double usd = obs.getFXUSDCAD().getV();
				double corra = obs.getAVGINTWO().getV();
				xy+= (usd-usdcadAverage)*(corra-corraAverage);
				x2+= (usd-usdcadAverage)*(usd-usdcadAverage);
				y2+= (corra-corraAverage)*(corra-corraAverage);
			}
		}
		return "Pearson Correlation Coefficient <br>" +(xy/Math.sqrt(x2*y2));
	}
	
	/**
	 * Queries the valet api for json data within the provided date ranges
	 * @param query - formated date query
	 * @return - html text of calculated data, called from calculations(valet)
	 */
	public String query(String query) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(URI+"?"+query);
		ObjectMapper mapper = new ObjectMapper();
		try {
			String response = target.request()
					.accept(MediaType.APPLICATION_JSON)
					.get(String.class);
			valet = mapper.readValue(response, Valet.class);
		} catch (JsonParseException e)
        {
            e.printStackTrace();
        //catches bad requests due to bad date format
		}catch(BadRequestException e) {
			return "400, check dates";
		}catch (JsonMappingException e) 
		{
		e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return calculations(valet);
	}
	
	/**
	 * Calculates data from valet api query
	 * @param valet - data from valet api
	 * @return - html text of calculated data
	 */
	public String calculations(Valet valet) {
		double usdcadtotal = 0;
		double usdcadhigh = 0;
		double usdcadlow = 9999;
		int usdcaditems = 0;
		
		double corratotal = 0;
		double corrahigh = 0;
		double corralow = 9999;
		int corraitems = 0;
		
		String usdcad;
		String corra;
		
		for(Observations obs :valet.getObservations()) {
			//FXUSDCAD data
			if(obs.getFXUSDCAD()!= null) {
				double usdcadvalue = obs.getFXUSDCAD().getV();			
				usdcadtotal += usdcadvalue;
				if(usdcadvalue > usdcadhigh)
					usdcadhigh=usdcadvalue;
				if(usdcadvalue < usdcadlow)
					usdcadlow = usdcadvalue;
				usdcaditems++;
			}
			//AVG.INTWO data
			if(obs.getAVGINTWO()!=null) {
				double corravalue = obs.getAVGINTWO().getV();
				corratotal += corravalue;
				if(corravalue > corrahigh)
					corrahigh=corravalue;
				if(corravalue < corralow)
					corralow = corravalue;
				corraitems++;
			}
		}
		if(usdcaditems==0) 
			usdcad = "USDCAD <br> No Data <br>";
		else {
			usdcadAverage = usdcadtotal/usdcaditems;
			usdcad = "USDCAD <br>" + "high: " + usdcadhigh + "<br>low: " + usdcadlow + "<br>"+"average :" + usdcadAverage + "<br>";
		}
		if(corraitems==0)
			corra= "CORRA <br> No Data <br>";
		else {
			corraAverage = corratotal/corraitems;
			corra = "CORRA <br>" + "high: " + corrahigh + "<br>low: " + corralow + "<br>"+"average :" + corraAverage + "<br>";
		}
		return usdcad + corra;
	}
}   
