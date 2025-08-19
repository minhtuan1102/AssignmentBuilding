package com.javaweb.api;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import com.javaweb.model.BuildingDTO;
import com.javaweb.model.ErrorRespondDTO;
import com.javaweb.service.BuildingService;

import customexception.FieldRequiredException;

@RestController
public class BuildingAPI {
	
	@Autowired
	private BuildingService buildingService;
	@GetMapping(value = "/api/building/")
	public List<BuildingDTO> GetBuilding(@RequestParam Map<String,Object> params,
										@RequestParam(name="typeCode", required = false) List<String>typeCode) {
		List<BuildingDTO> result = buildingService.findAll(params,typeCode);
		return result;	
	}
	
	@DeleteMapping(value = "api/building/{id}")
	public void deleteBuilding(@PathVariable Integer id) {
		System.out.print("Da xoa toa nha co id la " + id + " roi");
	}
}
